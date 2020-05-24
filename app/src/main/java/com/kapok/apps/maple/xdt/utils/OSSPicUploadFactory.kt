package com.kapok.apps.maple.xdt.utils

import android.content.Context
import android.util.Log
import com.alibaba.sdk.android.oss.ClientConfiguration
import com.alibaba.sdk.android.oss.OSS
import com.alibaba.sdk.android.oss.OSSClient
import com.alibaba.sdk.android.oss.common.OSSLog
import com.alibaba.sdk.android.oss.common.auth.OSSCredentialProvider
import com.alibaba.sdk.android.oss.common.auth.OSSStsTokenCredentialProvider
import com.kotlin.baselibrary.commen.BaseConstant
import com.alibaba.sdk.android.oss.ServiceException
import com.alibaba.sdk.android.oss.ClientException
import com.alibaba.sdk.android.oss.model.PutObjectRequest
import com.alibaba.sdk.android.oss.model.PutObjectResult
import com.alibaba.sdk.android.oss.callback.OSSCompletedCallback
import com.alibaba.sdk.android.oss.callback.OSSProgressCallback
import com.alibaba.sdk.android.oss.internal.OSSAsyncTask
import com.alibaba.sdk.android.oss.model.OSSRequest

/**
 * OSS图片上传单例
 */
class OSSPicUploadFactory private constructor(context: Context, AK: String,SK: String,token: String) {
    private val credentialProvider: OSSCredentialProvider
    private val oss: OSS
    private var upLoadListener: UpLoadListener? = null
    private var task: OSSAsyncTask<PutObjectResult>? = null

    fun setUpLoadListener(listener: UpLoadListener) {
        upLoadListener = listener
    }

    companion object {
        fun getOSSPicUploadInstance(context: Context,AK: String,SK: String, token: String): OSSPicUploadFactory {
            val oSSPicUploadInstance: OSSPicUploadFactory by lazy {
                OSSPicUploadFactory(
                    context,
                    AK,
                    SK,
                    token
                )
            }
            return oSSPicUploadInstance
        }
    }

    init {
        credentialProvider = OSSStsTokenCredentialProvider(AK, SK, token)
        val config = ClientConfiguration()
        config.connectionTimeout = 15 * 1000 // 连接超时，默认15秒
        config.socketTimeout = 15 * 1000 // socket超时，默认15秒
        config.maxConcurrentRequest = 5 // 最大并发请求数，默认5个
        config.maxErrorRetry = 2 // 失败后最大重试次数，默认2次
        OSSLog.enableLog() //这个开启会支持写入手机sd卡中的一份日志文件位置在SDCard_path\OSSLog\logs.csv
        oss = OSSClient(context, BaseConstant.endPoint, credentialProvider, config)
    }

    /**
     * 上传1张图片调用
     */
    fun upLoadPicToOSS(picUrl: String, objectName: String) {
        // 构造上传请求
        val put = PutObjectRequest(BaseConstant.bucketName, objectName, picUrl)
        put.crC64 = OSSRequest.CRC64Config.YES
        // 异步上传时可以设置进度回调
        put.progressCallback = OSSProgressCallback { request, currentSize, totalSize ->
            upLoadListener?.upLoadProgress(currentSize, totalSize)
        }

        // task.waitUntilFinished(); // 可以等待直到任务完成
        task = oss.asyncPutObject(
            put,
            object : OSSCompletedCallback<PutObjectRequest, PutObjectResult> {
                override fun onSuccess(request: PutObjectRequest, result: PutObjectResult) {
                    upLoadListener?.upLoadSuccess(request, result)
                }

                override fun onFailure(
                    request: PutObjectRequest,
                    clientExcepion: ClientException?,
                    serviceException: ServiceException?
                ) {
                    upLoadListener?.upLoadFailed(request, clientExcepion, serviceException)
                    // 请求异常
                    clientExcepion?.printStackTrace()
                    if (serviceException != null) {
                        // 服务异常
                        Log.e("ErrorCode", serviceException.errorCode)
                        Log.e("RequestId", serviceException.requestId)
                        Log.e("HostId", serviceException.hostId)
                        Log.e("RawMessage", serviceException.rawMessage)
                    }
                }
            })
    }

    fun CancelUploadTask() {
        task?.cancel()
    }
}