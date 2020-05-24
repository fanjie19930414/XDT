package com.kapok.apps.maple.xdt.utils

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.PixelFormat
import android.graphics.Point
import android.hardware.Camera
import android.media.AudioManager
import android.media.MediaPlayer
import android.media.MediaRecorder
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.MotionEvent
import android.view.SurfaceHolder
import android.view.View
import android.view.WindowManager
import com.kapok.apps.maple.xdt.R
import com.kotlin.baselibrary.activity.BaseActivity
import com.kotlin.baselibrary.commen.BaseApplication
import com.kotlin.baselibrary.ex.setVisible
import com.kotlin.baselibrary.utils.DateUtils
import com.kotlin.baselibrary.utils.MediaUtils
import com.kotlin.baselibrary.utils.ToastUtils
import kotlinx.android.synthetic.main.activity_video_record.*
import java.io.File
import kotlin.math.abs

/**
 * 拍摄视频类
 */
class VideoRecordActivity : BaseActivity() {
    private val TAG = "VideoRecordActivity"
    private var mStartedFlag = false //录像中标志
    private var mPlayFlag = false
    private lateinit var mRecorder: MediaRecorder
    private lateinit var mSurfaceHolder: SurfaceHolder
    private lateinit var mCamera: Camera
    private lateinit var mMediaPlayer: MediaPlayer
    private lateinit var dirPath: String //目标文件夹地址
    private lateinit var path: String //最终视频路径
    private lateinit var imgPath: String //缩略图 或 拍照模式图片位置
    private var timer = 0 //计时器
    private val maxSec = 30 //视频总时长
    private var startTime: Long = 0L //起始时间毫秒
    private var stopTime: Long = 0L  //结束时间毫秒
    private var cameraReleaseEnable = true  //回收摄像头
    private var recorderReleaseEnable = false  //回收recorder
    private var playerReleaseEnable = false //回收player

    private var maxSizeWidth: Int = 0
    private var maxSizeHeight: Int = 0

    //用于记录视频录制时长
    var handler = Handler()
    var runnable = object : Runnable {
        override fun run() {
            timer++
//            Log.d("计数器","$timer")
            if (timer < 100) {
                // 之所以这里是100 是为了方便使用进度条
                mProgressBar.progress = timer
                //之所以每一百毫秒增加一次计时器是因为：总时长的毫秒数 / 100 即每次间隔延时的毫秒数 为 100
                handler.postDelayed(this, maxSec * 10L)
            } else {
                mProgressBar.progress = 100
                //停止录制 保存录制的流、显示供操作的ui
                ToastUtils.showMsg(this@VideoRecordActivity,"到最大录制时间")
                stopRecord()
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_video_record)
        mMediaPlayer = MediaPlayer()

        val holder = mSurfaceView.holder
        mRecorder = MediaRecorder()
        holder.addCallback(object : SurfaceHolder.Callback {
            override fun surfaceChanged(
                holder: SurfaceHolder?,
                format: Int,
                width: Int,
                height: Int
            ) {
                if (holder != null) {
                    mSurfaceHolder = holder
                }
                mCamera.apply {
                    startPreview()
                    cancelAutoFocus()
                    // 关键代码 该操作必须在开启预览之后进行（最后调用），
                    // 否则会黑屏，并提示该操作的下一步出错
                    // 只有执行该步骤后才可以使用MediaRecorder进行录制
                    // 否则会报 MediaRecorder(13280): start failed: -19
                    unlock()
                }
                cameraReleaseEnable = true
            }

            override fun surfaceDestroyed(holder: SurfaceHolder?) {
                handler.removeCallbacks(runnable)
            }

            override fun surfaceCreated(holder: SurfaceHolder?) {
                try {
                    mSurfaceHolder = holder!!
                    //使用后置摄像头
                    mCamera = Camera.open(Camera.CameraInfo.CAMERA_FACING_BACK)
                    mCamera.apply {
                        setDisplayOrientation(90)//旋转90度
                        setPreviewDisplay(holder)
                        val params = mCamera.parameters
                        //注意此处需要根据摄像头获取最优像素，//如果不设置会按照系统默认配置最低160x120分辨率
                        val size = getPreviewSize()
                        params.apply {
                            setPictureSize(size.first, size.second)
                            jpegQuality = 100
                            pictureFormat = PixelFormat.JPEG
                            focusMode = Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE//1连续对焦
                        }
                        parameters = params
                    }

                } catch (e: RuntimeException) {
                    //Camera.open() 在摄像头服务无法连接时可能会抛出 RuntimeException
                    e.printStackTrace()
                }
            }
        })
        mBtnRecord.setOnTouchListener { _, event ->
            Log.d("点击屏幕", "${event.action}")
            if (event.action == MotionEvent.ACTION_DOWN) {
                startRecord()
            }
            if (event.action == MotionEvent.ACTION_UP) {
                stopRecord()
            }
            true
        }
        mBtnPlay.setOnClickListener {
            playRecord()
        }
        mBtnCancle.setOnClickListener {
            stopPlay()
            val videoFile = File(path)
            if (videoFile.exists() && videoFile.isFile) {
                videoFile.delete()
            }
            setResult(Activity.RESULT_CANCELED)
            finish()
        }
        mBtnSubmit.setOnClickListener {
            stopPlay()
            val intent = Intent().apply {
                putExtra("path", path)
                putExtra("imagePath", imgPath)
            }
            // 回调给上一页
            setResult(Activity.RESULT_OK, intent)
            finish()
        }
    }

    override fun onStop() {
        super.onStop()
        if (mPlayFlag) {
            stopPlay()
        }
        if (mStartedFlag) {
            Log.d("页面stop", "")
            stopRecord()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        if (recorderReleaseEnable) mRecorder.release()
        if (cameraReleaseEnable) {
            mCamera.stopPreview()
            mCamera.release()
        }
        if (playerReleaseEnable) {
            mMediaPlayer.release()
        }
    }

    // 开始录制
    private fun startRecord() {
        timer = 0
        if (!mStartedFlag) {
            mStartedFlag = true
            mLlRecordOp.visibility = View.INVISIBLE
            mBtnPlay.setVisible(false)
            mLlRecordBtn.visibility = View.VISIBLE
            mProgressBar.visibility = View.VISIBLE //进度条可见
            //开始计时
            handler.postDelayed(runnable, maxSec * 10L)
            recorderReleaseEnable = true
            mRecorder.apply {
                reset()
                setCamera(mCamera)
                // 设置音频源与视频源 这两项需要放在setOutputFormat之前
                setAudioSource(MediaRecorder.AudioSource.CAMCORDER)
                setVideoSource(MediaRecorder.VideoSource.CAMERA)
                //设置输出格式
                setOutputFormat(MediaRecorder.OutputFormat.MPEG_4)
                //这两项需要放在setOutputFormat之后 IOS必须使用ACC
                setAudioEncoder(MediaRecorder.AudioEncoder.AAC)  //音频编码格式
                //使用MPEG_4_SP格式在华为P20 pro上停止录制时会出现
                //MediaRecorder: stop failed: -1007
                //java.lang.RuntimeException: stop failed.
                // at android.media.MediaRecorder.stop(Native Method)
                setVideoEncoder(MediaRecorder.VideoEncoder.H264)  //视频编码格式
                //设置最终出片分辨率
                setVideoSize(maxSizeWidth,maxSizeHeight)
                setVideoFrameRate(30)
                setVideoEncodingBitRate(3 * 1024 * 1024)
                setOrientationHint(90)
                //设置记录会话的最大持续时间（毫秒）
                setMaxDuration(30 * 1000)
            }
            path = BaseApplication.getVideoFolderPath()
            val dir = File(path)
            if (!dir.exists()) {
                dir.mkdir()
            }
            dirPath = dir.absolutePath
            path = dir.absolutePath + "/" + DateUtils.getDate() + ".mp4"
            Log.d(TAG, "文件路径： $path")
            mRecorder.apply {
                setOutputFile(path)
                prepare()
                start()
            }
            startTime = System.currentTimeMillis()  //记录开始拍摄时间
        }
    }

    // 播放录像
    private fun playRecord() {
        //修复录制时home键切出再次切回时无法播放的问题
        if (cameraReleaseEnable) {
            Log.d(TAG, "回收摄像头资源")
            mCamera.apply {
                lock()
                stopPreview()
                release()
            }
            cameraReleaseEnable = false
        }
        playerReleaseEnable = true
        mPlayFlag = true
        mBtnPlay.setVisible(false)

        mMediaPlayer.reset()
        val uri = Uri.parse(path)
        mMediaPlayer = MediaPlayer.create(this, uri)
        mMediaPlayer.apply {
            setAudioStreamType(AudioManager.STREAM_MUSIC)
            setDisplay(mSurfaceHolder)
            setOnCompletionListener {
                //播放解释后再次显示播放按钮
                mBtnPlay.setVisible(true)
            }
        }
        try {
            mMediaPlayer.prepare()
        } catch (e: Exception) {
            e.printStackTrace()
        }
        mMediaPlayer.start()
    }

    //停止播放录像
    private fun stopPlay() {
        if (mMediaPlayer.isPlaying) {
            mMediaPlayer.stop()
        }
    }

    // 结束录制
    private fun stopRecord() {
        if (mStartedFlag) {
            mStartedFlag = false
            mBtnPlay.setVisible(true)
            mBtnRecord.isEnabled = false
            mBtnRecord.isClickable = false
            mLlRecordBtn.visibility = View.INVISIBLE
            mProgressBar.visibility = View.INVISIBLE
            handler.removeCallbacks(runnable)
            stopTime = System.currentTimeMillis()
            //  方法1 ： 延时确保录制时间大于1s
            if (stopTime - startTime < 1100) {
                Thread.sleep(1100 + startTime - stopTime)
            }
            mRecorder.stop()
            mRecorder.reset()
            mRecorder.release()
            recorderReleaseEnable = false
            mCamera.lock()
            mCamera.stopPreview()
            mCamera.release()
            cameraReleaseEnable = false
            MediaUtils.getImageForVideo(path) {
                //获取到第一帧图片后再显示操作按钮
                Log.d(TAG, "获取到了第一帧")
                imgPath = it.absolutePath
                mLlRecordOp.visibility = View.VISIBLE
            }

//          方法2 ： 捕捉异常改为拍照
//            try {
//                mRecorder.apply {
//                    stop()
//                    reset()
//                    release()
//                }
//                recorderReleaseEnable = false
//                mCamera.apply {
//                    lock()
//                    stopPreview()
//                    release()
//                }
//                cameraReleaseEnable = false
//                mBtnPlay.visibility = View.VISIBLE
//                MediaUtils.getImageForVideo(path) {
//                    //获取到第一帧图片后再显示操作按钮
//                    Log.d(TAG, "获取到了第一帧")
//                    imgPath = it.absolutePath
//                    mLlRecordOp.visibility = View.VISIBLE
//                }
//            } catch (e: java.lang.RuntimeException) {
//                //当catch到RE时，说明是录制时间过短，此时将由录制改变为拍摄
//                mType = typeImage
//                Log.e("拍摄时间过短", e.message)
//                mRecorder.apply {
//                    reset()
//                    release()
//                }
//                recorderReleaseEnable = false
//                mCamera.takePicture(null, null, Camera.PictureCallback { data, camera ->
//                    data?.let {
//                        saveImage(it) { imagepath ->
//                            Log.d(TAG, "转为拍照，获取到图片数据 $imagepath")
//                            imgPath = imagepath
//                            mCamera.apply {
//                                lock()
//                                stopPreview()
//                                release()
//                            }
//                            cameraReleaseEnable = false
//                            runOnUiThread {
//                                mBtnPlay.visibility = View.INVISIBLE
//                                mLlRecordOp.visibility = View.VISIBLE
//                            }
//                        }
//                    }
//                })
//            }
        }
    }

    //从底层拿camera支持的previewSize，完了和屏幕分辨率做差，diff最小的就是最佳预览分辨率
    private fun getPreviewSize(): Pair<Int, Int> {
        var bestPreviewWidth = 1920
        var bestPreviewHeight = 1080
        var mCameraPreviewWidth: Int
        var mCameraPreviewHeight: Int
        var diffs = Integer.MAX_VALUE
        val windowManager = getSystemService(Context.WINDOW_SERVICE) as WindowManager
        val display = windowManager.defaultDisplay
        val screenResolution = Point(display.width, display.height)
        val availablePreviewSizes = mCamera.parameters.supportedPreviewSizes
        Log.e(TAG, "屏幕宽度 ${screenResolution.x}  屏幕高度${screenResolution.y}")
        for (previewSize in availablePreviewSizes) {
            Log.v(TAG, " PreviewSizes = $previewSize")
            mCameraPreviewWidth = previewSize.width
            mCameraPreviewHeight = previewSize.height
            val newDiffs =
                abs(mCameraPreviewWidth - screenResolution.y) + abs(mCameraPreviewHeight - screenResolution.x)
            Log.v(TAG, "newDiffs = $newDiffs")
            if (newDiffs == 0) {
                bestPreviewWidth = mCameraPreviewWidth
                bestPreviewHeight = mCameraPreviewHeight
                break
            }
            if (diffs > newDiffs) {
                bestPreviewWidth = mCameraPreviewWidth
                bestPreviewHeight = mCameraPreviewHeight
                diffs = newDiffs
            }
            Log.e(
                TAG,
                "${previewSize.width} ${previewSize.height}  宽度 $bestPreviewWidth 高度 $bestPreviewHeight"
            )
        }
        Log.e(TAG, "最佳宽度 $bestPreviewWidth 最佳高度 $bestPreviewHeight")
        maxSizeWidth = bestPreviewWidth
        maxSizeHeight = bestPreviewHeight
        return Pair(bestPreviewWidth, bestPreviewHeight)
    }

}