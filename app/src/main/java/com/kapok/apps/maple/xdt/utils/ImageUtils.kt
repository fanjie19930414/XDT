package com.kapok.apps.maple.xdt.utils

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.ColorMatrix
import android.graphics.ColorMatrixColorFilter
import android.graphics.Paint
import android.os.AsyncTask
import android.text.format.DateFormat

import com.kotlin.baselibrary.commen.BaseApplication

import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream
import java.util.Date

/**
 * Description: 图片处理类
 */
object ImageUtils {

    //计算图片的缩放值
    fun calculateInSampleSize(options: BitmapFactory.Options, reqWidth: Int, reqHeight: Int): Int {
        val height = options.outHeight
        val width = options.outWidth
        var inSampleSize = 1

        if (height > reqHeight || width > reqWidth) {
            val heightRatio = Math.round(height.toFloat() / reqHeight.toFloat())
            val widthRatio = Math.round(width.toFloat() / reqWidth.toFloat())
            inSampleSize = if (heightRatio < widthRatio) heightRatio else widthRatio
        }
        return inSampleSize
    }

    // 根据路径获得图片并压缩
    fun compressImage(filePath: String): File? {
        val options = BitmapFactory.Options()
        options.inJustDecodeBounds = true
        BitmapFactory.decodeFile(filePath, options)

        // Calculate inSampleSize
        options.inSampleSize = calculateInSampleSize(options, 720, 1080)

        // Decode bitmap with inSampleSize set
        options.inJustDecodeBounds = false

        val bm = BitmapFactory.decodeFile(filePath, options)

        val baos = ByteArrayOutputStream()
        bm.compress(Bitmap.CompressFormat.JPEG, 100, baos)
        val size = baos.toByteArray().size / 1024
        baos.reset()//重置baos即清空baos
        if (size > 1024 * 4) { //判断如果图片大于4M,进行40%压缩
            return getSmallBitmap(bm, 40)
        } else if (size > 1024 * 2) { //判断如果图片大于2M,进行60%压缩
            return getSmallBitmap(bm, 60)
        } else if (size > 1024) { //判断如果图片大于1M,进行80%压缩
            return getSmallBitmap(bm, 80)
        } else if (size > 512) { //判断如果图片大于512k,进行90%压缩
            return getSmallBitmap(bm, 90)
        }
        return getSmallBitmap(bm, 100)
    }

    //异步压缩图片
    fun compressImage(filePath: String, listener: OnCompressImageListener) {
        CompressImageTask(filePath, listener).execute()
    }

    interface OnCompressImageListener {
        fun onCompressSuccess(imageFile: File)

        fun onCompressFail()
    }

    private class CompressImageTask(
        private val imagePath: String,
        private val listener: OnCompressImageListener?
    ) : AsyncTask<Void, Void, File>() {

        override fun doInBackground(vararg voids: Void): File? {
            return compressImage(imagePath)
        }

        override fun onPostExecute(file: File?) {
            super.onPostExecute(file)
            if (listener != null) {
                if (file != null)
                    listener.onCompressSuccess(file)
                else
                    listener.onCompressFail()
            }
        }
    }

    fun getSmallBitmap(bm: Bitmap, quality: Int): File? {
        val imageFolderPath: String = BaseApplication.getImageFolderPath() ?: return null
        val photoPath = imageFolderPath + DateFormat.format("yyyy-MM-dd-hh-mm-ss", Date()) + ".jpg"
        val file = File(photoPath)
        try {
            bm.compress(Bitmap.CompressFormat.JPEG, quality, FileOutputStream(file))
        } catch (e: Exception) {
            e.printStackTrace()
            return null
        }
        return file
    }

    /**
     * 修改图片亮度(0-100)
     */
    fun setBitmapBrightness(srcBitmap: Bitmap, progress: Int): Bitmap {
        try {
            val bmp = Bitmap.createBitmap(
                srcBitmap.width, srcBitmap.height,
                Bitmap.Config.ARGB_8888
            )
            val brightness = progress - 127
            val cMatrix = ColorMatrix()
            cMatrix.set(
                floatArrayOf(
                    1f,
                    0f,
                    0f,
                    0f,
                    brightness.toFloat(),
                    0f,
                    1f,
                    0f,
                    0f,
                    brightness.toFloat(), // 改变亮度
                    0f,
                    0f,
                    1f,
                    0f,
                    brightness.toFloat(),
                    0f,
                    0f,
                    0f,
                    1f,
                    0f
                )
            )

            val paint = Paint()
            paint.colorFilter = ColorMatrixColorFilter(cMatrix)

            val canvas = Canvas(bmp)
            // 在Canvas上绘制一个已经存在的Bitmap。这样，dstBitmap就和srcBitmap一摸一样了
            canvas.drawBitmap(srcBitmap, 0f, 0f, paint)
            return bmp
        } catch (ex: OutOfMemoryError) {
            return srcBitmap
        }

    }
}
