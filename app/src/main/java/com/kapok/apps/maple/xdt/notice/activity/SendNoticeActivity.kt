package com.kapok.apps.maple.xdt.notice.activity

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.os.Handler
import android.provider.MediaStore
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.helper.ItemTouchHelper
import android.text.Editable
import android.text.SpannableStringBuilder
import android.text.TextUtils
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import com.chad.library.adapter.base.BaseQuickAdapter
import com.iceteck.silicompressorr.SiliCompressor
import com.kapok.apps.maple.xdt.R
import com.kapok.apps.maple.xdt.homework.adapter.SendHomeWorkImgAdapter
import com.kapok.apps.maple.xdt.homework.bean.*
import com.kapok.apps.maple.xdt.notice.adapter.NoticeReceiptAdapter
import com.kapok.apps.maple.xdt.notice.adapter.TagAdapter
import com.kapok.apps.maple.xdt.notice.bean.NoticeTeacherDetailBean
import com.kapok.apps.maple.xdt.notice.bean.ReceiptItemBean
import com.kapok.apps.maple.xdt.notice.presenter.SendNoticePresenter
import com.kapok.apps.maple.xdt.notice.presenter.view.SendNoticeView
import com.kapok.apps.maple.xdt.utils.*
import com.kotlin.baselibrary.activity.BaseMVPActivity
import com.kotlin.baselibrary.commen.AppManager
import com.kotlin.baselibrary.commen.BaseApplication
import com.kotlin.baselibrary.commen.BaseConstant
import com.kotlin.baselibrary.commen.BaseConstant.Companion.TAKE_A_PHOTO
import com.kotlin.baselibrary.commen.BaseConstant.Companion.TAKE_A_VIDEO
import com.kotlin.baselibrary.custom.CancelConfirmDialog
import com.kotlin.baselibrary.custom.CustomCancelBottomDialog
import com.kotlin.baselibrary.custom.DefaultTextWatcher
import com.kotlin.baselibrary.ex.setVisible
import com.kotlin.baselibrary.utils.*
import com.luck.picture.lib.PictureSelector
import com.luck.picture.lib.config.PictureConfig
import com.luck.picture.lib.config.PictureMimeType
import kotlinx.android.synthetic.main.activity_send_notice.*
import kotlinx.android.synthetic.main.activity_send_notice.tagFlowLayout
import java.io.File

/**
 * 发布通知页面
 */
@SuppressLint("SetTextI18n")
class SendNoticeActivity : BaseMVPActivity<SendNoticePresenter>(), SendNoticeView,
    MyItemTouchCallback.OnDragListener {
    // 选中的班级信息列表
    private var classListInfo: ArrayList<TeacherInClasses> = arrayListOf()
    // 已选的班级信息列表
    private var chooseClassListInfo: ArrayList<TeacherInClasses> = arrayListOf()
    // 选择班级Code
    private val chooseClassRequestCode = 101
    // TagAdapter
    private lateinit var tagAdapter: TagAdapter
    // 图片集合
    private var homeWorkImgBean: MutableList<HomeWorkImgBean> = mutableListOf()
    // adapter
    private lateinit var imgAdapter: SendHomeWorkImgAdapter
    // 拖拽辅助类
    private lateinit var itemTouchHelper: ItemTouchHelper
    // 拍照保存路径
    private lateinit var photoPath: String
    // 拍摄视频/视频封面保存路径
    private lateinit var videoPath: String
    private lateinit var videoImgPath: String
    // 上传图片
    private lateinit var ossService: OssService
    // OSS上传进度UI
    private lateinit var ossUIDisplayer: OssUIDisplayer
    // 创建作业拼接的参数
    // 上传成功后图片的地址Url集合
    private var upLoadPicUrlList: MutableList<String> = arrayListOf()
    // 推出Dialog
    private lateinit var sendHomeWorkDialog: CancelConfirmDialog
    // 是否需要回执
    private var isReceipt: Boolean = false
    // 回执Rv
    private lateinit var receiptList: MutableList<ReceiptItemBean>
    private lateinit var receiptAdapter: NoticeReceiptAdapter
    // 回执状态 1: 单选 2: 多选 3: 文本(本期固定单选)
    private var receiptType = 1
    // 图片1 or 视频2 初始化0
    private var cameraTag = 0
    // 视频压缩Handler
    private val mHandler: Handler = Handler()
    // 视频阿里云地址
    private var videoUri: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_send_notice)
        initData()
        initListener()
    }

    private fun initData() {
        mPresenter = SendNoticePresenter(this)
        mPresenter.mView = this
        tvSendNoticeTitle.text = "发通知"
        // TagAdapter
        tagAdapter = TagAdapter(this, chooseClassListInfo)
        // 配置图片Rv
        ossUIDisplayer = OssUIDisplayer(ImageView(this), ProgressBar(this), TextView(this), this)
        ossService = UploadPicUtils.initOSS(
            applicationContext,
            BaseConstant.endPoint,
            BaseConstant.bucketName,
            ossUIDisplayer
        )
        rvSendNoticeImg.layoutManager = GridLayoutManager(this, 3)
        imgAdapter = SendHomeWorkImgAdapter(this, 9, homeWorkImgBean)
        rvSendNoticeImg.adapter = imgAdapter
        itemTouchHelper = ItemTouchHelper(MyItemTouchCallback(imgAdapter).setOnDragListener(this))
        itemTouchHelper.attachToRecyclerView(rvSendNoticeImg)
        // 配置回执Rv
        receiptList = arrayListOf()
        rvReceipt.layoutManager = LinearLayoutManager(this)
        receiptAdapter = NoticeReceiptAdapter(this, receiptList)
        rvReceipt.adapter = receiptAdapter
        receiptAdapter.addFooterView(
            LayoutInflater.from(this).inflate(
                R.layout.item_receipt_footer,
                null
            )
        )
    }

    private fun initListener() {
        // 返回
        ivSendNoticeBack.setOnClickListener {
            backRemind()
        }
        // 选择班级
        rlSendNoticeChooseClass.setOnClickListener {
            val intent = Intent()
            intent.putExtra("classListInfo", chooseClassListInfo)
            intent.setClass(this, NoticeChooseClassActivity::class.java)
            startActivityForResult(intent, chooseClassRequestCode)
        }
        // Tag删除
        tagFlowLayout.setOnTagClickListener { _, position, _ ->
            if (classListInfo.size > 0) {
                for (item in classListInfo) {
                    if (item.classId == chooseClassListInfo[position].classId) {
                        item.isChoose = false
                    }
                }
            }
            chooseClassListInfo.clear()
            // 筛选 选中的班级状态信息
            for (item in classListInfo) {
                if (item.isChoose) {
                    chooseClassListInfo.add(item)
                }
            }
            tagFlowLayout.adapter = tagAdapter
            true
        }
        // 标题
        etSendNoticeChooseEditTitle.addTextChangedListener(object : DefaultTextWatcher() {
            override fun afterTextChanged(s: Editable?) {
                super.afterTextChanged(s)
                titleTextLimit()
            }
        })
        // 内容
        etSendNoticeChooseEditContent.addTextChangedListener(object : DefaultTextWatcher() {
            override fun afterTextChanged(s: Editable?) {
                super.afterTextChanged(s)
                contentTextLimit()
            }
        })
        // 添加图片
        imgAdapter.setOnClickAddLongPic(object : SendHomeWorkImgAdapter.OnClickAddLongPic {
            override fun onClick(v: View, position: Int) {
                when (v.id) {
                    // 点击图片 添加
                    R.id.ivSendHomeWorkImg -> {
                        if (v.getTag(R.id.image_key) as Boolean) {
                            if (homeWorkImgBean.size > 0) {
                                showPhotoDialog(cameraTag)
                            } else {
                                cameraTag = 0
                                showPhotoDialog(cameraTag)
                            }
                        }
                    }
                    // 点击删除
                    R.id.ivSendHomeWorkImgDel -> {
                        if (cameraTag == 2) {
                            cameraTag = 0
                        }
                        homeWorkImgBean.removeAt(position)
                        if (cameraTag == 1) {
                            cameraTag = if (homeWorkImgBean.size > 0) {
                                1
                            } else {
                                0
                            }
                        }
                        imgAdapter.notifyDataSetChanged()
                    }
                }
            }

            override fun onLongClick(
                v: View,
                viewHolder: SendHomeWorkImgAdapter.ViewHolder,
                adapterPosition: Int
            ): Boolean {
                return if (v.getTag(R.id.image_key) as Boolean) {
                    false
                } else {
                    itemTouchHelper.startDrag(viewHolder)
                    true
                }
            }
        })
        // 点击回执
        rlSendNoticeReceipt.setOnClickListener {
            isReceipt = !isReceipt
            if (isReceipt) {
                ivNoticeReceipt.setImageResource(R.mipmap.togglebutton_on)
                rvReceipt.setVisible(true)
            } else {
                ivNoticeReceipt.setImageResource(R.mipmap.togglebutton_off)
                rvReceipt.setVisible(false)
            }
        }
        // 回执Footer点击事件
        receiptAdapter.footerLayout.setOnClickListener {
            // footer
            receiptList.add(ReceiptItemBean("选项" + (receiptList.size + 1) + "：", ""))
            receiptAdapter.notifyItemChanged(receiptList.size)
        }
        // 回执删除点击事件
        receiptAdapter.onItemChildClickListener =
            BaseQuickAdapter.OnItemChildClickListener { _, view, _ ->
                when (view?.id) {
                    R.id.ivReceiptDel -> {
                        receiptAdapter.notifyItemRemoved(receiptList.size - 1)
                        receiptAdapter.notifyItemRangeRemoved(receiptList.size,1)
                        receiptList.removeAt(receiptList.size -1)
                    }
                }
            }
        // 创建通知
        btSendNotice.setOnClickListener {
            upLoadPicUrlList.clear()
            // 判断必填项目
            when {
                chooseClassListInfo.size == 0 -> ToastUtils.showMsg(this, "请先选择班级")
                etSendNoticeChooseEditTitle.text.isEmpty() -> ToastUtils.showMsg(
                    this,
                    "请填写通知标题"
                )
                etSendNoticeChooseEditContent.text.isEmpty() -> ToastUtils.showMsg(
                    this,
                    "请填写通知内容"
                )
                else -> // 先压缩图片
                    // 判断是传的 图片 还是 视频
                    if (cameraTag == 2) {
                        ProgressUtils.showLoadDialog(
                            this,
                            "正在上传视频",
                            true
                        )
                        uploadVideoImage(homeWorkImgBean[0].path,object : OnUploadListener {
                            override fun onSuccess() {
                                createHomeWork()
                            }

                            override fun onFail(error: String) {
                                ToastUtils.showMsg(this@SendNoticeActivity, "视频上传失败:$error")
                            }
                        })
                    } else {
                        compressImg()
                    }
            }
        }
    }

    private fun compressImg() {
        compressAndUploadImages(0, object : OnUploadListener {
            override fun onSuccess() {
                createHomeWork()
            }

            override fun onFail(error: String) {
                ToastUtils.showMsg(this@SendNoticeActivity, "图片上传失败:$error")
            }
        })
    }

    private fun backRemind() {
        if (chooseClassListInfo.size == 0
            || etSendNoticeChooseEditTitle.text.isNotEmpty()
            || etSendNoticeChooseEditContent.text.isNotEmpty()
            || homeWorkImgBean.size > 0
        ) {
            sendHomeWorkDialog = CancelConfirmDialog(
                this@SendNoticeActivity, R.style.BottomDialog
                , "已编辑内容不会保存，确认退出？", ""
            )
            sendHomeWorkDialog.show()
            sendHomeWorkDialog.setOnClickConfirmListener(object :
                CancelConfirmDialog.ClickConfirmListener {
                override fun confirm() {
                    finish()
                    sendHomeWorkDialog.dismiss()
                }
            })
        } else {
            finish()
        }
    }

    // 清空数据
    private fun clearHomeWorkSetting() {
        classListInfo.clear()
        chooseClassListInfo.clear()
        tagFlowLayout.adapter = tagAdapter
        // 标题
        etSendNoticeChooseEditTitle.text = SpannableStringBuilder("")
        // 内容
        etSendNoticeChooseEditContent.text = SpannableStringBuilder("")
        // 图片集合
        homeWorkImgBean.clear()
        upLoadPicUrlList.clear()
        imgAdapter.notifyDataSetChanged()
        // 回执
        receiptList.clear()
        receiptAdapter.notifyDataSetChanged()
        isReceipt = false
        if (isReceipt) {
            ivNoticeReceipt.setImageResource(R.mipmap.togglebutton_on)
            rvReceipt.setVisible(true)
        } else {
            ivNoticeReceipt.setImageResource(R.mipmap.togglebutton_off)
            rvReceipt.setVisible(false)
        }
    }

    // 调用 发布通知 接口
    private fun createHomeWork() {
        // 拼图片参数
        var images = ""
        for (item in upLoadPicUrlList) {
            images += item
        }
        if (upLoadPicUrlList.size > 0) {
            images = images.substring(0, images.length - 1)
        }
        // 拼回执列表
        val receiptContent = arrayListOf<String>()
        for (item in receiptList) {
            receiptContent.add(item.content)
        }
        // 拼学生信息列表
        val studentDetailList = arrayListOf<HomeWorkStudentDetailBean>()
        for (item in chooseClassListInfo) {
            for (studentItem in item.chooseStudentInfo) {
                if (studentItem.isChoose) {
                    studentDetailList.add(
                        HomeWorkStudentDetailBean(
                            studentItem.classId,
                            0,
                            studentItem.studentAvatar ?: "",
                            studentItem.studentId,
                            studentItem.studentName
                        )
                    )
                }
            }
        }
        // 拼老师信息列表
        val teacherDetailList = arrayListOf<NoticeTeacherDetailBean>()
        for (item in chooseClassListInfo) {
            for (teacherItem in item.chooseTeacherInfo) {
                if (teacherItem.isChoose) {
                    teacherDetailList.add(
                        NoticeTeacherDetailBean(
                            teacherItem.classId,
                            teacherItem.teacherAvatar ?: "",
                            teacherItem.teacherId,
                            teacherItem.teacherName
                        )
                    )
                }
            }
        }
        // 调接口
        mPresenter.createNotice(
            etSendNoticeChooseEditContent.text.toString(),
            images,
            isReceipt,
            AppPrefsUtils.getInt("userId"),
            receiptContent,
            receiptType,
            studentDetailList,
            teacherDetailList,
            etSendNoticeChooseEditTitle.text.toString()
        )
    }

    private interface OnUploadListener {
        fun onSuccess()

        fun onFail(error: String)
    }

    private fun showPhotoDialog(cameraTag: Int) {
        val iconBottomDialog =
            CustomCancelBottomDialog(this@SendNoticeActivity, R.style.BottomDialog)
        when (cameraTag) {
            0 -> {
                iconBottomDialog.addItem(
                    "拍照",
                    R.color.login_xdt_btn_color_able,
                    View.OnClickListener {
                        this.cameraTag = 1
                        imgAdapter.setMaxPic(9)
                        takePhoto()
                        iconBottomDialog.dismiss()
                    })
                iconBottomDialog.addItem(
                    "拍视频",
                    R.color.login_xdt_btn_color_able,
                    View.OnClickListener {
                        this.cameraTag = 2
                        imgAdapter.setMaxPic(1)
                        takeVideo()
                        iconBottomDialog.dismiss()
                    })
                iconBottomDialog.addItem(
                    "从相册选择图片",
                    R.color.login_xdt_btn_color_able,
                    View.OnClickListener {
                        this.cameraTag = 1
                        imgAdapter.setMaxPic(9)
                        PictureSelector.create(this@SendNoticeActivity)
                            .openGallery(PictureMimeType.ofImage())
                            .maxSelectNum(9 - homeWorkImgBean.size)    // 最大选择数量
                            .imageSpanCount(4)  //每行选择数量
                            .selectionMode(PictureConfig.MULTIPLE) // 多选还是单选
                            .previewImage(true) //是否可预览图片
                            .isCamera(false)    // 是否显示拍摄按钮
                            .forResult(PictureConfig.CHOOSE_REQUEST)
                        iconBottomDialog.dismiss()
                    })
                iconBottomDialog.addItem(
                    "从相册选择视频",
                    R.color.login_xdt_btn_color_able,
                    View.OnClickListener {
                        this.cameraTag = 2
                        imgAdapter.setMaxPic(1)
                        PictureSelector.create(this@SendNoticeActivity)
                            .openGallery(PictureMimeType.ofVideo())
                            .maxSelectNum(1)    // 最大选择数量
                            .imageSpanCount(3)  //每行选择数量
                            .selectionMode(PictureConfig.SINGLE) // 多选还是单选
                            .previewVideo(true)
                            .isCamera(false)
                            .forResult(PictureConfig.REQUEST_CAMERA)
                        iconBottomDialog.dismiss()
                    })
                iconBottomDialog.show()
            }
            1 -> {
                iconBottomDialog.addItem(
                    "拍照",
                    R.color.login_xdt_btn_color_able,
                    View.OnClickListener {
                        this.cameraTag = 1
                        takePhoto()
                        iconBottomDialog.dismiss()
                    })
                iconBottomDialog.addItem(
                    "从相册选择图片",
                    R.color.login_xdt_btn_color_able,
                    View.OnClickListener {
                        this.cameraTag = 1
                        PictureSelector.create(this@SendNoticeActivity)
                            .openGallery(PictureMimeType.ofImage())
                            .maxSelectNum(9 - homeWorkImgBean.size)    // 最大选择数量
                            .imageSpanCount(4)  //每行选择数量
                            .selectionMode(PictureConfig.MULTIPLE) // 多选还是单选
                            .previewImage(true) //是否可预览图片
                            .isCamera(false)    // 是否显示拍摄按钮
                            .forResult(PictureConfig.CHOOSE_REQUEST)
                        iconBottomDialog.dismiss()
                    })
                iconBottomDialog.show()
            }
        }
    }

    private fun takePhoto() {
        PermissionUtils.checkPermission(
            this,
            arrayOf(Manifest.permission.CAMERA),
            object : PermissionUtils.CheckResultListener {
                override fun checkFailure(permissions: Array<out String>?) {
                    ToastUtils.showMsg(this@SendNoticeActivity, "无法获取拍照权限")
                }

                override fun checkSuccess() {
                    photoPath =
                        BaseApplication.getImageFolderPath() + System.currentTimeMillis() + ".jpg"
                    val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
                    intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(File(photoPath)))
                    startActivityForResult(intent, TAKE_A_PHOTO)
                }
            })
    }

    // 拍视频
    private fun takeVideo() {
        PermissionUtils.checkPermission(
            this,
            arrayOf(Manifest.permission.CAMERA,Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.RECORD_AUDIO),
            object : PermissionUtils.CheckResultListener {
                override fun checkFailure(permissions: Array<out String>?) {
                    ToastUtils.showMsg(this@SendNoticeActivity, "无法获取拍照权限")
                }

                override fun checkSuccess() {
                    startActivityForResult(Intent(this@SendNoticeActivity, VideoRecordActivity::class.java), TAKE_A_VIDEO)
                }
            })
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            when (requestCode) {
                // 选择班级
                chooseClassRequestCode -> {
                    chooseClassListInfo.clear()
                    if (data != null && data.getParcelableArrayListExtra<TeacherInClasses>("classInfo").size > 0) {
                        classListInfo =
                            data.getParcelableArrayListExtra<TeacherInClasses>("classInfo")
                        Log.e("info", classListInfo.toString())
                        // 筛选 选中的班级状态信息
                        for (item in classListInfo) {
                            if (item.isChoose) {
                                chooseClassListInfo.add(item)
                            }
                        }
                        tagFlowLayout.adapter = tagAdapter
                    }
                }
                // 拍照后回调 (没有裁剪 原图)
                TAKE_A_PHOTO -> {
                    homeWorkImgBean.add(HomeWorkImgBean(false, photoPath))
                    imgAdapter.notifyDataSetChanged()
                }
                // 拍视频后回调
                TAKE_A_VIDEO -> {
                    ProgressUtils.showLoadDialog(this,"压缩中...",true)
                    videoPath = data?.getStringExtra("path").toString()
                    videoImgPath = data?.getStringExtra("imagePath").toString()
                    Thread(Runnable {
                        val compressPath= SiliCompressor.with(this@SendNoticeActivity).compressVideo(videoPath ,
                            File(BaseApplication.getVideoFolderPath()).absolutePath,0,0,1200000)
                        mHandler.post {
                            ProgressUtils.closeLoadDialog()
                            if (compressPath.isNotEmpty()) {
                                val videoFile = File(videoPath)
                                if (videoFile.exists() && videoFile.isFile) {
                                    videoFile.delete()
                                }
                                val size = FileSizeUtil.getFileOrFilesSize(compressPath,3)
                                Log.d("videoSize","视频大小" + size.toString() + "MB")
                                Log.d("videoPath", "视频路径: $compressPath")
                                videoPath = compressPath
                                homeWorkImgBean.add(HomeWorkImgBean(true,videoImgPath))
                                imgAdapter.notifyDataSetChanged()
                            } else {
                               ToastUtils.showMsg(this,"视频压缩失败")
                            }
                        }
                    }).start()
                }
                // 相册选择回调 (没有裁剪)
                PictureConfig.CHOOSE_REQUEST -> {
                    // 例如 LocalMedia 里面返回三种path
                    // 1.media.getPath(); 为原图path
                    // 2.media.getCutPath();为裁剪后path，需判断media.isCut();是否为true
                    // 3.media.getCompressPath();为压缩后path，需判断media.isCompressed();是否为true
                    // 如果裁剪并压缩了，以取压缩路径为准，因为是先裁剪后压缩的
                    val images = PictureSelector.obtainMultipleResult(data)
                    for (item in images) {
                        homeWorkImgBean.add(HomeWorkImgBean(false, item.path))
                    }
                    imgAdapter.notifyDataSetChanged()
                }
                // 视频选择回调
                PictureConfig.REQUEST_CAMERA -> {
                    ProgressUtils.showLoadDialog(this,"压缩中...",true)
                    val videoInfo = PictureSelector.obtainMultipleResult(data)
                    MediaUtils.getImageForVideo(videoInfo[0].path) {
                        videoImgPath = it.absolutePath
                        videoPath = videoInfo[0].path
                        Thread(Runnable {
                            val compressPath= SiliCompressor.with(this).compressVideo(videoPath ,
                                File(BaseApplication.getVideoFolderPath()).absolutePath,0,0,1200000)
                            mHandler.post {
                                ProgressUtils.closeLoadDialog()
                                if (compressPath.isNotEmpty()) {
                                    val size = FileSizeUtil.getFileOrFilesSize(compressPath,3)
                                    Log.d("videoSize","视频大小" + size.toString() + "MB")
                                    Log.d("videoPath", "视频路径: $compressPath")
                                    videoPath = compressPath
                                } else {
                                    ToastUtils.showMsg(this,"视频压缩失败")
                                }
                            }
                        }).start()
                        homeWorkImgBean.add(HomeWorkImgBean(true,videoImgPath))
                        imgAdapter.notifyDataSetChanged()
                    }
                }
            }
        }
    }

    private fun contentTextLimit() {
        if (etSendNoticeChooseEditContent.text.length >= 1000) {
            tvSendNoticeChooseEditContent.setTextColor(resources.getColor(R.color.select_red))
            tvSendNoticeChooseEditContent.text =
                etSendNoticeChooseEditContent.text.length.toString() + "/" + 1000
        } else {
            tvSendNoticeChooseEditContent.setTextColor(resources.getColor(R.color.text_xdt_hint))
            tvSendNoticeChooseEditContent.text =
                etSendNoticeChooseEditContent.text.length.toString() + "/" + 1000
        }
    }

    private fun titleTextLimit() {
        if (etSendNoticeChooseEditTitle.text.length >= 20) {
            tvSendNoticeChooseEditTitle.setTextColor(resources.getColor(R.color.select_red))
            tvSendNoticeChooseEditTitle.text =
                etSendNoticeChooseEditTitle.text.length.toString() + "/" + 20
        } else {
            tvSendNoticeChooseEditTitle.setTextColor(resources.getColor(R.color.text_xdt_hint))
            tvSendNoticeChooseEditTitle.text =
                etSendNoticeChooseEditTitle.text.length.toString() + "/" + 20
        }
    }

    // 先压缩 后上传OSS
    private fun compressAndUploadImages(index: Int, listener: OnUploadListener) {
        if (homeWorkImgBean.size > 0) {
            ProgressUtils.showLoadDialog(
                this,
                "正在上传第 " + (index + 1) + "/" + homeWorkImgBean.size + " 个图",
                true
            )
            if (!TextUtils.isEmpty(homeWorkImgBean[index].path)) {
                val file = File(homeWorkImgBean[index].path)
                if (file.exists()) {
                    ImageUtils.compressImage(homeWorkImgBean[index].path,
                        object : ImageUtils.OnCompressImageListener {
                            override fun onCompressSuccess(imageFile: File) {
                                uploadImage(imageFile.path, index, listener)
                            }

                            override fun onCompressFail() {
                                uploadImage(homeWorkImgBean[index].path, index, listener)
                            }
                        })
                } else {
                    listener.onFail("图片" + (index + 1) + "文件不存在")
                    ProgressUtils.closeLoadDialog()
                }
            } else {
                if (index < homeWorkImgBean.size - 1) {
                    compressAndUploadImages(index + 1, listener)
                } else {
                    listener.onSuccess()
                    ProgressUtils.closeLoadDialog()
                }
            }
        } else {
            listener.onSuccess()
        }
    }

    private fun uploadImage(filePath: String, index: Int, listener: OnUploadListener) {
        val objectName = DateUtils.getOSSObjectName()
        ossService.asyncPutImage(objectName, filePath)
        // 上传图片监听
        ossUIDisplayer.setOnUpLoadResultListener(object : OssUIDisplayer.UpLoadResult {
            override fun upLoadOK() {
                upLoadPicUrlList.add(BaseConstant.ossPicUrl + objectName + ",")
                Log.w("OSSObjectPicUrl", BaseConstant.ossPicUrl + objectName)
                if (index < homeWorkImgBean.size - 1) {
                    compressAndUploadImages(index + 1, listener)
                } else {
                    listener.onSuccess()
                    ProgressUtils.closeLoadDialog()
                }
            }

            override fun upLoadFAIL(info: String) {
                if (index < homeWorkImgBean.size - 1) {
                    compressAndUploadImages(index + 1, listener)
                } else {
                    listener.onFail("第" + (index + 1) + "张上传失败" + info)
                    ProgressUtils.closeLoadDialog()
                }
            }
        })
    }

    private fun uploadVideoImage(filePath: String, listener: OnUploadListener) {
        val objectName = DateUtils.getOSSObjectName()
        ossService.asyncPutImage(objectName, filePath)
        // 上传图片监听
        ossUIDisplayer.setOnUpLoadResultListener(object : OssUIDisplayer.UpLoadResult {
            override fun upLoadOK() {
                upLoadPicUrlList.add(BaseConstant.ossPicUrl + objectName + ",")
                Log.w("OSSObjectPicUrl", BaseConstant.ossPicUrl + objectName)
                uploadVideo(videoPath,listener)
            }

            override fun upLoadFAIL(info: String) {
                uploadVideo(videoPath,listener)
            }
        })
    }

    private fun uploadVideo(filePath: String, listener: OnUploadListener) {
        val objectName = DateUtils.getOSSVideoObjectName()
        ossService.asyncPutImage(objectName, filePath)
        // 上传视频监听
        ossUIDisplayer.setOnUpLoadResultListener(object : OssUIDisplayer.UpLoadResult {
            override fun upLoadOK() {
                Log.w("OSSObjectVideoUrl", BaseConstant.ossPicUrl + objectName)
                videoUri = BaseConstant.ossPicUrl + objectName
                listener.onSuccess()
                ProgressUtils.closeLoadDialog()
            }

            override fun upLoadFAIL(info: String) {
                listener.onFail("视频上传失败")
                ProgressUtils.closeLoadDialog()
            }
        })
    }

    // 创建通知后的回调
    override fun createNotice(msg: String) {
        ToastUtils.showMsg(this, msg)
        // 创建之后 直接跳转列表页 (没有选择班级 所以需要展示班级)
        val intent = Intent(this, NoticeTeacherListActivity::class.java)
        intent.putExtra("classId",0)
        intent.putExtra("isHeaderTeacher",false)
        intent.putExtra("from",true)
        startActivity(intent)
        AppManager.instance.finishActivity(this)
//        clearHomeWorkSetting()
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        PermissionUtils.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }

    override fun onBackPressed() {
        backRemind()
    }

    override fun onFinishDrag() {

    }

    override fun onDestroy() {
        super.onDestroy()
        mHandler.removeCallbacksAndMessages(null)
    }
}