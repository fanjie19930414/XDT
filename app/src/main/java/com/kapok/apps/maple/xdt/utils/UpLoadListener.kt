package com.kapok.apps.maple.xdt.utils

import com.alibaba.sdk.android.oss.ClientException
import com.alibaba.sdk.android.oss.ServiceException
import com.alibaba.sdk.android.oss.model.PutObjectRequest
import com.alibaba.sdk.android.oss.model.PutObjectResult

interface UpLoadListener {
    fun upLoadSuccess(request: PutObjectRequest, result: PutObjectResult)

    fun upLoadFailed(
        request: PutObjectRequest,
        clientExcepion: ClientException?,
        serviceException: ServiceException?
    )

    fun upLoadProgress(currentSize: Long, totalSize: Long)
}