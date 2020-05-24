package com.kotlin.baselibrary.commen

import android.app.Application
import android.os.Build
import android.os.Environment
import android.os.StrictMode
import android.support.multidex.MultiDex
import org.litepal.LitePal
import java.io.File
import kotlin.properties.Delegates

/**
 * @desciption: Application基类
 */
open class BaseApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        // 数据库
        LitePal.initialize(this)
        MultiDex.install(this)
        context = this
        // android 7.0系统解决拍照的问题
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            val builder = StrictMode.VmPolicy.Builder()
            StrictMode.setVmPolicy(builder.build())
            builder.detectFileUriExposure()
        }
    }

    /**
     * 全局伴生对象
     */
    companion object {
        var context: BaseApplication by Delegates.notNull()

        /**
         * Description:  创建照片目录
         */
        const val appFolderName = "XDT"
        const val videoFolderImgName = "MyVideoImg"
        private const val imgFolderName = "MyPhoto"
        private const val videoFolderName = "MyVideo"

        fun getImageFolderPath(): String {
            val path = (Environment.getExternalStorageDirectory().toString()
                    + File.separator + "Android" + File.separator + "data"
                    + File.separator + appFolderName + File.separator
                    + imgFolderName + File.separator)
            return if (makeDirs(path)) {
                path
            } else imgFolderName
        }

        fun getVideoFolderPath(): String {
            val path = (Environment.getExternalStorageDirectory().toString()
                    + File.separator + "Android" + File.separator + "data"
                    + File.separator + appFolderName + File.separator
                    + videoFolderName + File.separator)
            return if (makeDirs(path)) {
                path
            } else videoFolderName
        }

        private fun makeDirs(path: String): Boolean {
            val dir = File(path)
            return dir.exists() || dir.mkdirs()
        }
    }
}