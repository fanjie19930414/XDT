package com.kotlin.baselibrary.commen

class BaseConstant {
    companion object {
        // 域名
        const val baseUrl = "http://api-beta.xdt123.com/kapok-xdt-api/"
        // im域名
        const val baseImUrl = "http://api-beta.xdt123.com/"
        //SP表名
        const val TABLE_PREFS = "XDT"
        // OOS 参数
        // objectName 格式：/xdt/{yyyy}/{MM}/{dd}/{uuid}.ext
        // 图片拼接地址 BucketName.Endpoint/Object
        const val AK = "LTAInlCnxU51IPoU"
        const val SK = "KqJyNniMEHkVttq0jNYDgusraAcPY2"
        const val endPoint = "http://oss-cn-shenzhen.aliyuncs.com"
        private const val endPointNoHTTP = "oss-cn-shenzhen.aliyuncs.com"
        const val bucketName = "kapok-dolphin"
        const val ossPicUrl = "https://$bucketName.$endPointNoHTTP/"
        // 拍照RequestCode
        const val TAKE_A_PHOTO = 901
        // 拍视频RequestCode
        const val TAKE_A_VIDEO = 801
        // 拍照裁剪图片RequestCode
        const val CROP_PICTURE = 902
        // 选择相册后裁剪图片RequestCode
        const val ALBUM_PICTURE = 903
    }
}