package com.kapok.apps.maple.xdt.utils;

import android.content.Context;
import android.system.Os;

import com.alibaba.sdk.android.oss.ClientConfiguration;
import com.alibaba.sdk.android.oss.OSS;
import com.alibaba.sdk.android.oss.OSSClient;
import com.alibaba.sdk.android.oss.common.OSSLog;
import com.alibaba.sdk.android.oss.common.auth.OSSCredentialProvider;
import com.alibaba.sdk.android.oss.common.auth.OSSPlainTextAKSKCredentialProvider;
import com.kotlin.baselibrary.commen.BaseConstant;

/**
 * 图片上传工具类
 */
public class UploadPicUtils {
    public static OssService initOSS(Context context,String endpoint, String bucket,OssUIDisplayer ossUIDisplayer) {
        // 移动端是不安全环境，不建议直接使用阿里云主账号ak，sk的方式。建议使用STS方式。具体参
        // https://help.aliyun.com/document_detail/31920.html
        // 注意：SDK 提供的 PlainTextAKSKCredentialProvider 只建议在测试环境或者用户可以保证阿里云主账号AK，SK安全的前提下使用。具体使用如下
        // 主账户使用方式
        // String AK = "******";
        // String SK = "******";
        // credentialProvider = new PlainTextAKSKCredentialProvider(AK,SK)
        // 以下是使用STS Sever方式。
        // 如果用STS鉴权模式，推荐使用OSSAuthCredentialProvider方式直接访问鉴权应用服务器，token过期后可以自动更新。
        // 详见：https://help.aliyun.com/document_detail/31920.html
        // OSSClient的生命周期和应用程序的生命周期保持一致即可。在应用程序启动时创建一个ossClient，在应用程序结束时销毁即可。
        // 现在项目测试  使用存放AK SK存放本地方式
        OSSCredentialProvider credentialProvider = new OSSPlainTextAKSKCredentialProvider(BaseConstant.AK,BaseConstant.SK);
        ClientConfiguration conf = new ClientConfiguration();
        conf.setConnectionTimeout(15 * 1000); // 连接超时，默认15秒
        conf.setSocketTimeout(15 * 1000); // socket超时，默认15秒
        conf.setMaxConcurrentRequest(5); // 最大并发请求书，默认5个
        conf.setMaxErrorRetry(2); // 失败后最大重试次数，默认2次
        OSS oss = new OSSClient(context, endpoint, credentialProvider, conf);
        OSSLog.enableLog();
        return new OssService(oss,bucket,ossUIDisplayer);
    }
}
