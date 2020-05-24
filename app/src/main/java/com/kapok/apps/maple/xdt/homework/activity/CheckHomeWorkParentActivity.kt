package com.kapok.apps.maple.xdt.homework.activity

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.provider.MediaStore
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.helper.ItemTouchHelper
import android.text.Editable
import android.text.SpannableStringBuilder
import android.text.TextUtils
import android.util.Log
import android.view.View
import android.view.WindowManager
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import com.bumptech.glide.Glide
import com.chad.library.adapter.base.BaseQuickAdapter
import com.iceteck.silicompressorr.SiliCompressor
import com.kapok.apps.maple.xdt.R
import com.kapok.apps.maple.xdt.homework.adapter.CheckHomeWorkTeacherAdapter
import com.kapok.apps.maple.xdt.homework.adapter.SendHomeWorkImgAdapter
import com.kapok.apps.maple.xdt.homework.adapter.TeacherCommentAdapter
import com.kapok.apps.maple.xdt.homework.bean.*
import com.kapok.apps.maple.xdt.homework.presenter.CheckHomeWorkParentPresenter
import com.kapok.apps.maple.xdt.homework.presenter.view.CheckHomeWorkParentView
import com.kapok.apps.maple.xdt.utils.*
import com.kotlin.baselibrary.activity.BaseMVPActivity
import com.kotlin.baselibrary.commen.BaseApplication
import com.kotlin.baselibrary.commen.BaseConstant
import com.kotlin.baselibrary.custom.CancelConfirmDialog
import com.kotlin.baselibrary.custom.CustomCancelBottomDialog
import com.kotlin.baselibrary.custom.DefaultTextWatcher
import com.kotlin.baselibrary.ex.setVisible
import com.kotlin.baselibrary.utils.*
import com.luck.picture.lib.PictureSelector
import com.luck.picture.lib.config.PictureConfig
import com.luck.picture.lib.config.PictureMimeType
import kotlinx.android.synthetic.main.activity_check_homework_parent.*
import kotlinx.android.synthetic.main.activity_send_homework.*
import java.io.File
import java.io.Serializable

/**
 * 查看作业 教师端
 */
@SuppressLint("SetTextI18n")
class CheckHomeWorkParentActivity : BaseMVPActivity<CheckHomeWorkParentPresenter>(),
    CheckHomeWorkParentView, MyItemTouchCallback.OnDragListener {
    // 传入的列表信息
    private lateinit var homeWorkItemBean: HomeWorkListItemBean
    private var studentId = 0
    private var workId = 0
    private var teacherId = 0
    private var classId = 0
    // 接口返回的图片列表
    private lateinit var checkHomeWorkPicList: MutableList<String>
    private lateinit var checkHomeWorkParentAdapter: CheckHomeWorkTeacherAdapter
    // 上传图片
    private lateinit var ossService: OssService
    // OSS上传进度UI
    private lateinit var ossUIDisplayer: OssUIDisplayer
    // 提交作业成功后返回的图片列表
    private lateinit var commitHomeWorkPicList: MutableList<String>
    private lateinit var commitHomeWorkParentAdapter: CheckHomeWorkTeacherAdapter
    // 老师评论的列表
    private lateinit var teacherCommentList: MutableList<TeacherCommentParentBean>
    private lateinit var teacherCommentAdapter: TeacherCommentAdapter
    // 上传成功后图片的地址Url集合
    private var upLoadPicUrlList: MutableList<String> = arrayListOf()
    // 上传图片adapter
    private lateinit var imgAdapter: SendHomeWorkImgAdapter
    // 拖拽辅助类
    private lateinit var itemTouchHelper: ItemTouchHelper
    // 拍照保存路径
    private lateinit var photoPath: String
    // 图片集合
    private var homeWorkImgBean: MutableList<HomeWorkImgBean> = mutableListOf()
    // 记录一下提交状态
    private var submitState = 1
    // 记录一下已结束/进行中
    private var state = 1
    // 退出Dialog
    private lateinit var checkHomeWorkFinishDialog: CancelConfirmDialog
    // 拍摄视频/视频封面保存路径
    private lateinit var videoPath: String
    private lateinit var videoImgPath: String
    // 图片1 or 视频2 初始化0
    private var cameraTag = 0
    // 视频压缩Handler
    private val mHandler: Handler = Handler()

    // 图片上传自定义接口
    private interface OnUploadListener {
        fun onSuccess()

        fun onFail(error: String)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_check_homework_parent)
        initData()
        initListener()
    }

    private fun initData() {
        mPresenter = CheckHomeWorkParentPresenter(this)
        mPresenter.mView = this
        // 接收传参
        homeWorkItemBean = intent.getParcelableExtra("homeWorkItemInfo")
        classId = intent.getIntExtra("classId", 0)
        studentId = homeWorkItemBean.studentId
        teacherId = homeWorkItemBean.teacherId
        workId = homeWorkItemBean.workId
        // 配置作业图片Rv
        checkHomeWorkPicList = arrayListOf()
        rvCheckHomeWorkPicParent.layoutManager = GridLayoutManager(this, 3)
        checkHomeWorkParentAdapter = CheckHomeWorkTeacherAdapter(this, checkHomeWorkPicList)
        rvCheckHomeWorkPicParent.adapter = checkHomeWorkParentAdapter
        // 配置提交作业图片Rv
        commitHomeWorkPicList = arrayListOf()
        rvCheckHomeWorkImgParent.layoutManager = GridLayoutManager(this, 3)
        commitHomeWorkParentAdapter = CheckHomeWorkTeacherAdapter(this, commitHomeWorkPicList)
        rvCheckHomeWorkImgParent.adapter = commitHomeWorkParentAdapter
        // 配置教师评论Rv
        teacherCommentList = arrayListOf()
        rvCheckHomeWorkTeacherComment.layoutManager = LinearLayoutManager(this)
        teacherCommentAdapter = TeacherCommentAdapter(this, teacherCommentList)
        rvCheckHomeWorkTeacherComment.adapter = teacherCommentAdapter
        // 配置提交作业Rv
        ossUIDisplayer = OssUIDisplayer(ImageView(this), ProgressBar(this), TextView(this), this)
        ossService = UploadPicUtils.initOSS(
            applicationContext,
            BaseConstant.endPoint,
            BaseConstant.bucketName,
            ossUIDisplayer
        )
        rvSendHomeWorkImgParent.layoutManager = GridLayoutManager(this, 3)
        imgAdapter = SendHomeWorkImgAdapter(this, 9, homeWorkImgBean)
        rvSendHomeWorkImgParent.adapter = imgAdapter
        itemTouchHelper = ItemTouchHelper(MyItemTouchCallback(imgAdapter).setOnDragListener(this))
        itemTouchHelper.attachToRecyclerView(rvSendHomeWorkImgParent)
        // 获取查看作业详情接口(未提交的)
        mPresenter.checkHomeWorkParent(classId, AppPrefsUtils.getInt("userId"), workId)
    }

    private fun initListener() {
        // 返回
        ivCheckHomeWorkBackParent.setOnClickListener { backRemind() }
        // 字数监听
        etCheckHomeWorkEditContentParent.addTextChangedListener(object : DefaultTextWatcher() {
            override fun afterTextChanged(s: Editable?) {
                super.afterTextChanged(s)
                contentTextLimit()
            }
        })
        // 图片点击查看大图
        checkHomeWorkParentAdapter.onItemClickListener =
            BaseQuickAdapter.OnItemClickListener { _, _, position ->
                val intent = Intent(this, PhotoShowActivity::class.java)
                intent.putExtra("showUrlList", checkHomeWorkPicList as Serializable)
                intent.putExtra("isUrlList", true)
                intent.putExtra("index", position)
                startActivity(intent)
            }
        commitHomeWorkParentAdapter.onItemClickListener =
            BaseQuickAdapter.OnItemClickListener { _, _, position ->
                val intent = Intent(this, PhotoShowActivity::class.java)
                intent.putExtra("showUrlList", commitHomeWorkPicList as Serializable)
                intent.putExtra("isUrlList", true)
                intent.putExtra("index", position)
                startActivity(intent)
            }
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
        // 保存
        btSave.setOnClickListener {
            // 保存的时候仅保留本地图片的地址 (不做上传处理)
            saveHomeWork()
        }
        // 提交
        btPublish.setOnClickListener {
            if (etCheckHomeWorkEditContentParent.text.isNotEmpty()) {
                upLoadPicUrlList.clear()
                compressImgPublish()
            } else {
                ToastUtils.showMsg(this, "请添加作业答案")
            }
        }
    }

    private fun contentTextLimit() {
        if (etCheckHomeWorkEditContentParent.text.length >= 1000) {
            tvCheckHomeWorkEditContentParent.setTextColor(resources.getColor(R.color.select_red))
            tvCheckHomeWorkEditContentParent.text =
                etCheckHomeWorkEditContentParent.text.length.toString() + "/" + 1000
        } else {
            tvCheckHomeWorkEditContentParent.setTextColor(resources.getColor(R.color.text_xdt_hint))
            tvCheckHomeWorkEditContentParent.text =
                etCheckHomeWorkEditContentParent.text.length.toString() + "/" + 1000
        }
    }

    private fun showPhotoDialog(cameraTag: Int) {
        val iconBottomDialog =
            CustomCancelBottomDialog(this, R.style.BottomDialog)
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
                        PictureSelector.create(this)
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
                        PictureSelector.create(this)
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
                        PictureSelector.create(this)
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
                    ToastUtils.showMsg(this@CheckHomeWorkParentActivity, "无法获取拍照权限")
                }

                override fun checkSuccess() {
                    photoPath =
                        BaseApplication.getImageFolderPath() + System.currentTimeMillis() + ".jpg"
                    val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
                    intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(File(photoPath)))
                    startActivityForResult(intent, BaseConstant.TAKE_A_PHOTO)
                }
            })
    }

    private fun initView(bean: CheckHomeWorkTeacherBean) {
        var createTime = bean.gmtCreate
        val subjectName = bean.subjectName
        createTime = createTime.substring(5, 10).replace("-", "月")
        // 标题
        tvCheckHomeWorkTitleParent.text = createTime + "日" + subjectName + "作业"
        // 班级信息
        tvCheckHomeWorkClassNameParent.text =
            bean.grade + bean.className + "班(" + bean.startYear + "级)"
        // 头像
        if (homeWorkItemBean.teacherAvatar != null && homeWorkItemBean.teacherAvatar!!.isNotEmpty()) {
            GlideUtils.loadImage(
                this,
                homeWorkItemBean.teacherAvatar!!,
                civCheckHomeWorkParentIcon
            )
        } else {
            Glide.with(this).load(R.mipmap.def_head_boy).into(civCheckHomeWorkParentIcon)
        }
        // 姓名
        if (homeWorkItemBean.isTeacherLeader) {
            tvCheckHomeWorkTeacherNameParent.text =
                homeWorkItemBean.teacherName + "(班主任)"
        } else {
            tvCheckHomeWorkTeacherNameParent.text =
                homeWorkItemBean.teacherName
        }
        // 标题
        tvCheckHomeWorkContentParent.text = homeWorkItemBean.title
        // 开始时间
        tvCheckHomeWorkStartTimeParent.text = bean.gmtCreate.substring(0, 16)
        // 标签
        tvCheckHomeWorkTypeParent.text = bean.workTypeDesc
        // 进行中/已结束
        if (bean.state == 1) {
            state = 1
            tvCheckHomeWorkStatusParent.text = "进行中"
            tvCheckHomeWorkStatusParent.setTextColor(resources.getColor(R.color.login_xdt_btn_color_able))
            tvCheckHomeWorkStatusParent.setBackgroundResource(R.drawable.shape_background_corner_blue_fill)
        } else {
            state = 2
            // 已结束 默认展示 已提交的View
            submitState2View()
            tvCheckHomeWorkStatusParent.text = "已结束"
            tvCheckHomeWorkStatusParent.setTextColor(resources.getColor(R.color.text_xdt_hint))
            tvCheckHomeWorkStatusParent.setBackgroundResource(R.drawable.shape_background_corner_hint_fill)
        }
        // 截止日期
        tvCheckHomeWorkEndTimeParent.text =
            "截至时间：" + bean.deadline.substring(0, bean.deadline.length - 3)
        // 内容
        checkHomeWorkMoreParent.setText(bean.content)
        // 图片
        if (bean.images.isNotEmpty()) {
            val picList = bean.images.split(",")
            checkHomeWorkPicList.clear()
            for (item in picList) {
                checkHomeWorkPicList.add(item)
            }
        }
        // 剩余时间
        tvCheckHomeWorkLeftTimeParent.text = bean.remainTime
        checkHomeWorkParentAdapter.notifyDataSetChanged()
        // 判断状态 submitStatus 1 未提及 2 已提交 3 其他
        when (homeWorkItemBean.submitStatus) {
            1 -> {
                if (state == 1) {
                    submitState = 1
                    submitState1View()
                    // 查看之前是否有保存的内容
                    mPresenter.checkHomeWorkParentCommited(studentId, teacherId, workId)
                } else {
                    // 已提交并且老师已评论
                    if (homeWorkItemBean.replyStatus == 2) {
                        submitState = 3
                        submitState3View()
                        // 查看之前是否有保存的内容
                        mPresenter.checkHomeWorkParentCommited(studentId, teacherId, workId)
                    } else {
                        // 已提交未评论
                        submitState = 2
                        submitState2View()
                        // 查看之前是否有保存的内容
                        mPresenter.checkHomeWorkParentCommited(studentId, teacherId, workId)
                    }
                }
            }
            2 -> {
                // 已提交并且老师已评论
                if (homeWorkItemBean.replyStatus == 2) {
                    submitState = 3
                    submitState3View()
                    // 查看之前是否有保存的内容
                    mPresenter.checkHomeWorkParentCommited(studentId, teacherId, workId)
                } else {
                    // 已提交未评论
                    submitState = 2
                    submitState2View()
                    // 查看之前是否有保存的内容
                    mPresenter.checkHomeWorkParentCommited(studentId, teacherId, workId)
                }
            }
        }
    }

    private fun submitState2View() {
        tvCheckHomeWorkLeftTimeParent.text = "我的作答"
        rlCheckHomeWorkEditContentParent.setVisible(false)
        rlCheckHomeWorkTextContentParent.setVisible(true)
        rvSendHomeWorkImgParent.setVisible(false)
        rvCheckHomeWorkImgParent.setVisible(true)
        rlCheckHomeWorkTeacherCommentParent.setVisible(false)
        llCheckHomeWorkParentButton.setVisible(false)
    }

    private fun submitState3View() {
        tvCheckHomeWorkLeftTimeParent.text = "我的作答"
        rlCheckHomeWorkEditContentParent.setVisible(false)
        rlCheckHomeWorkTextContentParent.setVisible(true)
        rvSendHomeWorkImgParent.setVisible(false)
        rvCheckHomeWorkImgParent.setVisible(true)
        rlCheckHomeWorkTeacherCommentParent.setVisible(true)
        llCheckHomeWorkParentButton.setVisible(false)
    }

    private fun submitState1View() {
        rlCheckHomeWorkEditContentParent.setVisible(true)
        rlCheckHomeWorkTextContentParent.setVisible(false)
        rvSendHomeWorkImgParent.setVisible(true)
        rvCheckHomeWorkImgParent.setVisible(false)
        rlCheckHomeWorkTeacherCommentParent.setVisible(false)
        llCheckHomeWorkParentButton.setVisible(true)
    }

    // 拍视频
    private fun takeVideo() {
        PermissionUtils.checkPermission(
            this,
            arrayOf(Manifest.permission.CAMERA,Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.RECORD_AUDIO),
            object : PermissionUtils.CheckResultListener {
                override fun checkFailure(permissions: Array<out String>?) {
                    ToastUtils.showMsg(this@CheckHomeWorkParentActivity, "无法获取拍照权限")
                }

                override fun checkSuccess() {
                    startActivityForResult(Intent(this@CheckHomeWorkParentActivity, VideoRecordActivity::class.java),
                        BaseConstant.TAKE_A_VIDEO
                    )
                }
            })
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            when (requestCode) {
                // 拍照后回调 (没有裁剪 原图)
                BaseConstant.TAKE_A_PHOTO -> {
                    homeWorkImgBean.add(HomeWorkImgBean(false, photoPath))
                    imgAdapter.notifyDataSetChanged()
                }
                // 拍视频后回调
                BaseConstant.TAKE_A_VIDEO -> {
                    ProgressUtils.showLoadDialog(this,"压缩中...",true)
                    videoPath = data?.getStringExtra("path").toString()
                    videoImgPath = data?.getStringExtra("imagePath").toString()
                    Thread(Runnable {
                        val compressPath= SiliCompressor.with(this).compressVideo(videoPath ,
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

    private fun compressImgPublish() {
        compressAndUploadImages(0, object : OnUploadListener {
            override fun onSuccess() {
                publishHomeWork()
            }

            override fun onFail(error: String) {
                ToastUtils.showMsg(this@CheckHomeWorkParentActivity, "图片上传失败:$error")
            }
        })
    }

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
            listener.onFail("您还未添加图片")
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

    // 调用 保存作业 接口
    private fun saveHomeWork() {
        // 拼图片参数
        var images = ""
        if (homeWorkImgBean.size > 0) {
            for (item in homeWorkImgBean) {
                images += item.path + ","
            }
            images = images.substring(0, images.length - 1)
        }
        mPresenter.saveHomeWorkParent(
            etCheckHomeWorkEditContentParent.text.toString(),
            images,
            AppPrefsUtils.getInt("userId").toString().toInt(),
            studentId,
            workId
        )
    }

    // 调用 提交作业 接口
    private fun publishHomeWork() {
        // 拼图片参数
        var images = ""
        for (item in upLoadPicUrlList) {
            images += item
        }
        images = images.substring(0, images.length - 1)
        mPresenter.publishHomeWorkParent(
            etCheckHomeWorkEditContentParent.text.toString(),
            images,
            AppPrefsUtils.getInt("userId").toString().toInt(),
            studentId,
            workId
        )
    }

    private fun backRemind() {
        if (submitState == 1) {
            if (etCheckHomeWorkEditContentParent.text.isNotEmpty() || homeWorkImgBean.size > 0) {
                checkHomeWorkFinishDialog = CancelConfirmDialog(
                    this, R.style.BottomDialog
                    , "已编辑内容不会保存，确认退出？", ""
                )
                checkHomeWorkFinishDialog.show()
                checkHomeWorkFinishDialog.setOnClickConfirmListener(object :
                    CancelConfirmDialog.ClickConfirmListener {
                    override fun confirm() {
                        // 调用转移班主任权限接口
                        finish()
                        checkHomeWorkFinishDialog.dismiss()
                    }
                })
            } else {
                finish()
            }
        } else {
            finish()
        }
    }


    // 获取作业家长详情
    override fun getHomeWorkParentInfo(bean: CheckHomeWorkTeacherBean) {
        // 配置作业
        initView(bean)
    }

    // 保存回调
    override fun saveHomeWorkResult(msg: String) {
        submitState = 1
        ToastUtils.showMsg(this, msg)
    }

    // 提交回调
    override fun publishHomeWorkResult(msg: String) {
        submitState = 2
        ToastUtils.showMsg(this, msg)
        // 调用已提交的接口 并 刷新当前界面
        mPresenter.checkHomeWorkParentCommited(studentId, teacherId, workId)
    }

    // 获取已提交的作业
    override fun getHomeWorkParentCommit(bean: CheckHomeWorkParentBean?) {
        // 判断状态 submitState 1 未提及 2 已提交 3 已回复
        when (submitState) {
            1 -> {
                if (bean != null) {
                    submitState1View()
                    // 查看之前是否有保存的内容
                    if (bean.content != null) {
                        etCheckHomeWorkEditContentParent.text = SpannableStringBuilder(bean.content)
                    } else {
                        etCheckHomeWorkEditContentParent.text = SpannableStringBuilder("")
                    }
                    tvCheckHomeWorkEditContentParent.text =
                        etCheckHomeWorkEditContentParent.text.length.toString() + "/" + 1000
                    // 图片
                    if (bean.images != null) {
                        if (bean.images.isNotEmpty()) {
                            val picList = bean.images.split(",")
                            homeWorkImgBean.clear()
                            for (item in picList) {
                                homeWorkImgBean.add(HomeWorkImgBean(false, item))
                            }
                            imgAdapter.notifyDataSetChanged()
                        }
                    }

                }
            }
            2 -> {
                if (bean != null) {
                    submitState2View()
                    if (bean.content != null && bean.images != null) {
                        if (bean.content.isEmpty() && bean.images.isEmpty()) {
                            rlCheckHomeWorkTextContentParent.setVisible(false)
                            rlCheckHomeWorkNoContentParent.setVisible(true)
                        } else {
                            etCheckHomeWorkTextContentParent.text = bean.content
                            // 图片
                            if (bean.images.isNotEmpty()) {
                                val picList = bean.images.split(",")
                                commitHomeWorkPicList.clear()
                                for (item in picList) {
                                    commitHomeWorkPicList.add(item)
                                }
                                commitHomeWorkParentAdapter.notifyDataSetChanged()
                            }
                        }
                    } else {
                        rlCheckHomeWorkTextContentParent.setVisible(false)
                        rlCheckHomeWorkNoContentParent.setVisible(true)
                    }
                } else {
                    rlCheckHomeWorkTextContentParent.setVisible(false)
                    rlCheckHomeWorkNoContentParent.setVisible(true)
                }
            }
            3 -> {
                if (bean != null) {
                    submitState3View()
                    if (bean.content != null && bean.images != null) {
                        if (bean.content.isEmpty() && bean.images.isEmpty()) {
                            rlCheckHomeWorkTextContentParent.setVisible(false)
                            rlCheckHomeWorkNoContentParent.setVisible(true)
                        } else {
                            etCheckHomeWorkTextContentParent.text = bean.content
                            // 图片
                            if (bean.images.isNotEmpty()) {
                                val picList = bean.images.split(",")
                                commitHomeWorkPicList.clear()
                                for (item in picList) {
                                    commitHomeWorkPicList.add(item)
                                }
                                commitHomeWorkParentAdapter.notifyDataSetChanged()
                            }
                        }
                    } else {
                        rlCheckHomeWorkTextContentParent.setVisible(false)
                        rlCheckHomeWorkNoContentParent.setVisible(true)
                    }
                } else {
                    rlCheckHomeWorkTextContentParent.setVisible(false)
                    rlCheckHomeWorkNoContentParent.setVisible(true)
                }
                mPresenter.getTeacherComment(AppPrefsUtils.getInt("userId"), studentId, workId)
            }
        }
    }

    // 获取老师评论回调
    override fun getTeacherCommentParent(list: MutableList<TeacherCommentParentBean>?) {
        teacherCommentList.clear()
        if (list != null && list.size > 0) {
            teacherCommentList.addAll(list)
            teacherCommentAdapter.notifyDataSetChanged()
        }
    }

    override fun onFinishDrag() {

    }

    override fun onBackPressed() {
        backRemind()
    }
}