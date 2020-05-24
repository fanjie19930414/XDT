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
import android.support.v7.widget.helper.ItemTouchHelper
import android.text.Editable
import android.text.SpannableStringBuilder
import android.text.TextUtils
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import com.iceteck.silicompressorr.SiliCompressor
import com.kapok.apps.maple.xdt.R
import com.kapok.apps.maple.xdt.home.bean.EducationOrProfessionBean
import com.kapok.apps.maple.xdt.homework.adapter.SendHomeWorkImgAdapter
import com.kapok.apps.maple.xdt.homework.bean.*
import com.kapok.apps.maple.xdt.homework.presenter.SendHomeWorkPresenter
import com.kapok.apps.maple.xdt.homework.presenter.view.SendHomeWorkView
import com.kapok.apps.maple.xdt.usercenter.bean.SubjectListBean
import com.kapok.apps.maple.xdt.utils.*
import com.kotlin.baselibrary.activity.BaseMVPActivity
import com.kotlin.baselibrary.commen.AppManager
import com.kotlin.baselibrary.commen.BaseApplication
import com.kotlin.baselibrary.commen.BaseConstant
import com.kotlin.baselibrary.custom.CancelConfirmDialog
import com.kotlin.baselibrary.custom.CustomCancelBottomDialog
import com.kotlin.baselibrary.custom.CustomHomeWorkDataDialog
import com.kotlin.baselibrary.custom.DefaultTextWatcher
import com.kotlin.baselibrary.ex.setVisible
import com.kotlin.baselibrary.utils.*
import com.luck.picture.lib.PictureSelector
import com.luck.picture.lib.config.PictureConfig
import com.luck.picture.lib.config.PictureMimeType
import kotlinx.android.synthetic.main.activity_send_homework.*
import kotlinx.android.synthetic.main.activity_video_record.*
import java.io.File

/**
 * 发布作业页面
 */
@SuppressLint("SetTextI18n")
class SendHomeWorkActivity : BaseMVPActivity<SendHomeWorkPresenter>(), SendHomeWorkView,
    MyItemTouchCallback.OnDragListener {
    // 选中的班级信息列表
    private var classListInfo: ArrayList<TeacherInClasses> = arrayListOf()
    // 已选的班级信息列表
    private var choosedClassListInfo: ArrayList<TeacherInClasses> = arrayListOf()
    // 选择班级Code
    private val chooseClassRequestCode = 101
    // 跳转班级下学生页面Code
    private val chooseStudentCodeFromSend = 103
    // 类型Dialog
    private lateinit var classDetailSettingDialog: CustomCancelBottomDialog
    // 类型List
    private lateinit var homeWorkTypeList: MutableList<EducationOrProfessionBean>
    // 类型Id (1、线上作业 2、课后练习 3、课后实践)
    private var workType = -1
    // 学科Dialog
    private lateinit var subjectSettingDialog: CustomCancelBottomDialog
    // 学科List
    private lateinit var sendHomeWorkSubjectList: MutableList<SubjectListBean>
    // 学科Id
    private var subjectType = -1
    // 时间Dialog
    private lateinit var homeWorkTimeDialog: CustomHomeWorkDataDialog
    // 选中的月/日/时/分
    private var selectMonth: String = ""
    private var selectDay: String = ""
    private var selectHour: String = ""
    private var selectMinute: String = ""
    // 图片集合
    private var homeWorkImgBean: MutableList<HomeWorkImgBean> = mutableListOf()
    // adapter
    private lateinit var imgAdapter: SendHomeWorkImgAdapter
    // 拖拽辅助类
    private lateinit var itemTouchHelper: ItemTouchHelper
    // 拍照保存路径
    private lateinit var photoPath: String
    // 上传图片
    private lateinit var ossService: OssService
    // OSS上传进度UI
    private lateinit var ossUIDisplayer: OssUIDisplayer
    // 创建作业拼接的参数
    // 上传成功后图片的地址Url集合
    private var upLoadPicUrlList: MutableList<String> = arrayListOf()
    // 状态（1:未发布（草稿）；2 已发布
    private var state = 1
    // 学生的课程列表
    private var studentDetailList: MutableList<HomeWorkStudentDetailBean> = arrayListOf()
    // 选择的时间String
    private var selectTime: String = ""
    // 推出Dialog
    private lateinit var sendHomeWorkDialog: CancelConfirmDialog
    // 查看作业传参
    private var fromCheckHomeWork = false
    private lateinit var homeWorkInfoBean: CheckHomeWorkTeacherBean
    private lateinit var checkHomeWorkPicList: MutableList<String>
    // 拍摄视频/视频封面保存路径
    private lateinit var videoPath: String
    private lateinit var videoImgPath: String
    // 图片1 or 视频2 初始化0
    private var cameraTag = 0
    // 视频压缩Handler
    private val mHandler: Handler = Handler()
    // 视频阿里云地址
    private var videoUri: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_send_homework)
        initData()
        initListener()
    }

    private fun initData() {
        mPresenter = SendHomeWorkPresenter(this)
        mPresenter.mView = this
        tvSendHomeWorkTitle.text = "发作业"
        // 从查看作业页面传入的数据
        fromCheckHomeWork = intent.getBooleanExtra("fromCheckHomeWork", false)
        if (fromCheckHomeWork) {
            checkHomeWorkPicList = intent.getStringArrayListExtra("homeWorkImage")
            homeWorkInfoBean = intent.getParcelableExtra("homeWorkInfo")
            tvHomeWorkType.text = homeWorkInfoBean.workTypeDesc
            etSendHomeWorkChooseEditTitle.text = SpannableStringBuilder(homeWorkInfoBean.title)
            etSendHomeWorkChooseEditContent.text = SpannableStringBuilder(homeWorkInfoBean.content)
            titleTextLimit()
            contentTextLimit()
            for (item in checkHomeWorkPicList) {
                homeWorkImgBean.add(HomeWorkImgBean(false, item))
            }
        }
        // 配置类型
        homeWorkTypeList = arrayListOf()
        // 配置学科
        sendHomeWorkSubjectList = arrayListOf()
        // 配置图片Rv
        ossUIDisplayer = OssUIDisplayer(ImageView(this), ProgressBar(this), TextView(this), this)
        ossService = UploadPicUtils.initOSS(
            applicationContext,
            BaseConstant.endPoint,
            BaseConstant.bucketName,
            ossUIDisplayer
        )
        rvSendHomeWorkImg.layoutManager = GridLayoutManager(this, 3)
        imgAdapter = SendHomeWorkImgAdapter(this, 9, homeWorkImgBean)
        rvSendHomeWorkImg.adapter = imgAdapter
        itemTouchHelper = ItemTouchHelper(MyItemTouchCallback(imgAdapter).setOnDragListener(this))
        itemTouchHelper.attachToRecyclerView(rvSendHomeWorkImg)
        // 调用获取作业类型接口
        mPresenter.getHomeWorkType()
    }

    private fun initListener() {
        // 返回
        ivSendHomeWorkBack.setOnClickListener {
            backRemind()
        }
        // 三个点
//        ivSendHomeWorkSetting.setOnClickListener {
//            homeWorkSettingDialog =
//                CustomCancelBottomDialog(this@SendHomeWorkActivity, R.style.BottomDialog)
//            homeWorkSettingDialog.addItem("保存草稿", R.color.text_xdt, View.OnClickListener {
//                // 已发布
//                state = 1
//                createHomeWork()
//                homeWorkSettingDialog.dismiss()
//            })
//            homeWorkSettingDialog.addItem("删除", R.color.xdt_exit_text, View.OnClickListener {
//                // 选择班级 数据清空
//                clearHomeWorkSetting()
//                homeWorkSettingDialog.dismiss()
//            })
//            homeWorkSettingDialog.show()
//        }
        // 选择班级
        rlSendHomeWorkChooseClass.setOnClickListener {
            if (choosedClassListInfo.size == 0) {
                val intent = Intent()
                intent.putExtra("classListInfo", classListInfo)
                intent.setClass(this, HomeWorkChooseClassActivity::class.java)
                startActivityForResult(intent, chooseClassRequestCode)
            } else {
                val intent = Intent(this, HomeWorkClassStudentsActivity::class.java)
                intent.putExtra("from", "Send")
                intent.putExtra("classInfo", choosedClassListInfo[0])
                startActivityForResult(intent, chooseStudentCodeFromSend)
            }
        }
        // 删除选中班级事件
        ivDeleteChooseClass.setOnClickListener {
            if (classListInfo.size > 0) {
                for (item in classListInfo) {
                    if (item.classId == choosedClassListInfo[0].classId) {
                        item.isChoose = false
                    }
                }
            }
            choosedClassListInfo.clear()
            // 筛选 选中的班级状态信息
            for (item in classListInfo) {
                if (item.isChoose) {
                    choosedClassListInfo.add(item)
                }
            }
            llHaveChooseClass.setVisible(false)
            tvHomeWorkSubject.text = ""
            subjectType = -1
            if (tvSendHomeWorkTime.text.isEmpty()) {
                etSendHomeWorkChooseEditTitle.text =
                    SpannableStringBuilder(DateUtils.getMonth().toString() + "月" + DateUtils.getDay().toString() + "日" + "作业")
            } else {
                etSendHomeWorkChooseEditTitle.text =
                    SpannableStringBuilder(selectMonth + "月" + selectDay + "日" + selectHour + "：" + selectMinute + "前提交")
            }
            rlSendHomeWorkChooseSubject.setVisible(false)
        }
        // 选择类型
        rlSendHomeWorkChooseType.setOnClickListener {
            // dialog
            classDetailSettingDialog =
                CustomCancelBottomDialog(this@SendHomeWorkActivity, R.style.BottomDialog)
            classDetailSettingDialog.addTitle("选择类型", R.color.text_xdt)
            for (item in homeWorkTypeList) {
                classDetailSettingDialog.addItem(
                    item.name,
                    R.color.text_xdt,
                    View.OnClickListener {
                        workType = item.id.toInt()
                        tvHomeWorkType.text = item.name
                        classDetailSettingDialog.dismiss()
                    }
                )
            }
            classDetailSettingDialog.show()
        }
        // 选择科目
        rlSendHomeWorkChooseSubject.setOnClickListener {
            subjectSettingDialog =
                CustomCancelBottomDialog(this@SendHomeWorkActivity, R.style.BottomDialog)
            subjectSettingDialog.addTitle("选择学科", R.color.text_xdt)
            for (item in sendHomeWorkSubjectList) {
                subjectSettingDialog.addItem(
                    item.subjectName,
                    R.color.text_xdt,
                    View.OnClickListener {
                        subjectType = item.subjectId
                        tvHomeWorkSubject.text = item.subjectName
                        if (tvSendHomeWorkTime.text.isEmpty()) {
                            etSendHomeWorkChooseEditTitle.text =
                                SpannableStringBuilder("【" + tvHomeWorkSubject.text.toString() + "】" + DateUtils.getMonth().toString() + "月" + DateUtils.getDay().toString() + "日" + "作业")
                        } else {
                            etSendHomeWorkChooseEditTitle.text =
                                SpannableStringBuilder("【" + tvHomeWorkSubject.text.toString() + "】" + selectMonth + "月" + selectDay + "日" + selectHour + "：" + selectMinute + "前提交")
                        }
                        subjectSettingDialog.dismiss()
                    }
                )
            }
            subjectSettingDialog.show()
        }
        // 标题
        etSendHomeWorkChooseEditTitle.addTextChangedListener(object : DefaultTextWatcher() {
            override fun afterTextChanged(s: Editable?) {
                super.afterTextChanged(s)
                titleTextLimit()
            }
        })
        // 内容
        etSendHomeWorkChooseEditContent.addTextChangedListener(object : DefaultTextWatcher() {
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
        // 选择时间
        rlSendHomeWorkTime.setOnClickListener {
            homeWorkTimeDialog =
                CustomHomeWorkDataDialog(this@SendHomeWorkActivity, R.style.BottomDialog)
            homeWorkTimeDialog.setTitle("选择日期和时间")
            homeWorkTimeDialog.setSelectedTime(
                DateUtils.getYear().toString(),
                DateUtils.getMonth().toString(),
                DateUtils.getDay().toString(),
                DateUtils.getHour().toString(),
                DateUtils.getMinute().toString()
            )
            homeWorkTimeDialog.show()
            homeWorkTimeDialog.setOnselectDataListener(object :
                CustomHomeWorkDataDialog.SelectDataListener {
                override fun selectData(
                    year: String,
                    month: String,
                    day: String,
                    hour: String,
                    minute: String
                ) {
                    selectMonth = month
                    selectDay = day
                    selectHour = hour
                    selectMinute = minute
                    if (DateUtils.getYear() == year.toInt() && DateUtils.getMonth() == month.toInt() && DateUtils.getDay() == day.toInt()) {
                        tvSendHomeWorkTime.text =
                            "今日" + hour + "时" + minute + "分"
                    } else {
                        tvSendHomeWorkTime.text =
                            year + "年" + month + "月" + day + "日" + hour + "时" + minute + "分"
                    }
                    selectTime = "$year-$month-$day $hour:$minute"
                    if (tvHomeWorkSubject.text.isEmpty()) {
                        etSendHomeWorkChooseEditTitle.text =
                            SpannableStringBuilder(selectMonth + "月" + selectDay + "日" + selectHour + "：" + selectMinute + "前提交")
                    } else {
                        etSendHomeWorkChooseEditTitle.text =
                            SpannableStringBuilder("【" + tvHomeWorkSubject.text.toString() + "】" + selectMonth + "月" + selectDay + "日" + selectHour + "：" + selectMinute + "前提交")
                    }
                }
            })
        }
        // 创建作业
        btSendHomeWork.setOnClickListener {
            upLoadPicUrlList.clear()
            // 判断必填项目
            if (tvChooseClassName.text.isNotEmpty()
                && tvHomeWorkType.text.isNotEmpty()
                && tvHomeWorkSubject.text.isNotEmpty()
                && etSendHomeWorkChooseEditTitle.text.isNotEmpty()
                && etSendHomeWorkChooseEditContent.text.isNotEmpty()
                && homeWorkImgBean.size > 0
                && tvSendHomeWorkTime.text.isNotEmpty()
            ) {
                compareTime()
            } else {
                when {
                    tvChooseClassName.text.isEmpty() -> ToastUtils.showMsg(this, "请先选择班级")
                    tvHomeWorkType.text.isEmpty() -> ToastUtils.showMsg(this, "请选择作业类型")
                    tvHomeWorkSubject.text.isEmpty() -> ToastUtils.showMsg(this, "请选择科目")
                    etSendHomeWorkChooseEditTitle.text.isEmpty() -> ToastUtils.showMsg(
                        this,
                        "请填写作业标题"
                    )
                    etSendHomeWorkChooseEditContent.text.isEmpty() -> ToastUtils.showMsg(
                        this,
                        "请填写作业内容"
                    )
                    tvSendHomeWorkTime.text.isEmpty() -> ToastUtils.showMsg(this, "请选择截止日期")
                    else -> // 先压缩图片
                        compareTime()
                }
            }
        }
    }

    private fun compareTime() {
        val current = DateUtils.curTime + 5 * 60 * 1000
        val selectedTime = DateUtils.paseDateTomillise(selectTime)
        if (selectedTime >= current) {
            // 判断是传的 图片 还是 视频
            if (cameraTag == 2) {
                ProgressUtils.showLoadDialog(
                    this,
                    "正在上传视频",
                    true
                )
                uploadVideoImage(homeWorkImgBean[0].path,object : OnUploadListener {
                    override fun onSuccess() {
                        state = 2
                        createHomeWork()
                    }

                    override fun onFail(error: String) {
                        ToastUtils.showMsg(this@SendHomeWorkActivity, "视频上传失败:$error")
                    }
                })
            } else {
                compressImg()
            }
        } else {
            ToastUtils.showMsg(this, "截止时间至少需大于当前时间5分钟")
        }
    }

    private fun compressImg() {
        if (!fromCheckHomeWork) {
            compressAndUploadImages(0, object : OnUploadListener {
                override fun onSuccess() {
                    // 已发布
                    state = 2
                    createHomeWork()
                }

                override fun onFail(error: String) {
                    ToastUtils.showMsg(this@SendHomeWorkActivity, "图片上传失败:$error")
                }
            })
        } else {
            state = 2
            createHomeWork()
        }
    }

    private fun backRemind() {
        if (tvChooseClassName.text.isNotEmpty()
            || tvHomeWorkType.text.isNotEmpty()
            || tvHomeWorkSubject.text.isNotEmpty()
            || etSendHomeWorkChooseEditTitle.text.isNotEmpty()
            || etSendHomeWorkChooseEditContent.text.isNotEmpty()
            || homeWorkImgBean.size > 0
            || tvSendHomeWorkTime.text.isNotEmpty()
        ) {
            sendHomeWorkDialog = CancelConfirmDialog(
                this@SendHomeWorkActivity, R.style.BottomDialog
                , "已编辑内容不会保存，确认退出？", ""
            )
            sendHomeWorkDialog.show()
            sendHomeWorkDialog.setOnClickConfirmListener(object :
                CancelConfirmDialog.ClickConfirmListener {
                override fun confirm() {
                    // 调用转移班主任权限接口
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
        choosedClassListInfo.clear()
        llHaveChooseClass.setVisible(false)
        tvChooseClassName.text = ""
        // 类型
        tvHomeWorkType.text = ""
        workType = -1
        // 学科
        rlSendHomeWorkChooseSubject.setVisible(false)
        tvHomeWorkSubject.text = ""
        subjectType = -1
        // 标题
        etSendHomeWorkChooseEditTitle.text = SpannableStringBuilder("")
        // 内容
        etSendHomeWorkChooseEditContent.text = SpannableStringBuilder("")
        // 图片集合
        homeWorkImgBean.clear()
        upLoadPicUrlList.clear()
        imgAdapter.notifyDataSetChanged()
        // 选中时间
        selectTime = ""
        selectMinute = ""
        selectHour = ""
        selectMonth = ""
        selectDay = ""
        tvSendHomeWorkTime.text = ""
    }

    // 调用 发布作业 接口
    private fun createHomeWork() {
        studentDetailList.clear()
        // 拼图片参数
        var images = ""
        if (fromCheckHomeWork) {
            images = homeWorkInfoBean.images
            subjectType = homeWorkInfoBean.subjectId
            workType = homeWorkInfoBean.workType
        } else {
            for (item in upLoadPicUrlList) {
                images += item
            }
            if (upLoadPicUrlList.size > 0) {
                images = images.substring(0, images.length - 1)
            }
        }
        // 拼列表
        for (item in choosedClassListInfo[0].chooseStudentInfo) {
            if (item.studentAvatar != null) {
                studentDetailList.add(
                    HomeWorkStudentDetailBean(
                        item.classId, 0,
                        item.studentAvatar, item.studentId, item.studentName
                    )
                )
            } else {
                studentDetailList.add(
                    HomeWorkStudentDetailBean(
                        item.classId, 0,
                        "", item.studentId, item.studentName
                    )
                )
            }
        }
        mPresenter.createHomeWork(
            etSendHomeWorkChooseEditContent.text.toString(),
            "$selectTime:00",
            images,
            state,
            studentDetailList,
            subjectType,
            tvHomeWorkSubject.text.toString(),
            AppPrefsUtils.getInt("userId").toString().toInt(),
            etSendHomeWorkChooseEditTitle.text.toString(),
            videoUri,
            workType
        )
    }

    private interface OnUploadListener {
        fun onSuccess()

        fun onFail(error: String)
    }

    private fun showPhotoDialog(cameraTag: Int) {
        val iconBottomDialog =
            CustomCancelBottomDialog(this@SendHomeWorkActivity, R.style.BottomDialog)
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
                        PictureSelector.create(this@SendHomeWorkActivity)
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
                    ToastUtils.showMsg(this@SendHomeWorkActivity, "无法获取拍照权限")
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

    // 拍视频
    private fun takeVideo() {
        PermissionUtils.checkPermission(
            this,
            arrayOf(Manifest.permission.CAMERA,Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.RECORD_AUDIO),
            object : PermissionUtils.CheckResultListener {
                override fun checkFailure(permissions: Array<out String>?) {
                    ToastUtils.showMsg(this@SendHomeWorkActivity, "无法获取拍照权限")
                }

                override fun checkSuccess() {
                    startActivityForResult(Intent(this@SendHomeWorkActivity, VideoRecordActivity::class.java),
                        BaseConstant.TAKE_A_VIDEO
                    )
                }
            })
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            when (requestCode) {
                // 选择班级
                chooseClassRequestCode -> {
                    if (data != null && data.getParcelableArrayListExtra<TeacherInClasses>("classInfo").size > 0) {
                        choosedClassListInfo.clear()
                        classListInfo =
                            data.getParcelableArrayListExtra<TeacherInClasses>("classInfo")
                        Log.e("info", classListInfo.toString())
                        // 筛选 选中的班级状态信息
                        for (item in classListInfo) {
                            if (item.isChoose) {
                                choosedClassListInfo.add(item)
                            }
                        }
                        tvChooseClassName.text =
                            choosedClassListInfo[0].grade + choosedClassListInfo[0].className + "班" + choosedClassListInfo[0].chooseStudentNum + "人"
                        mPresenter.getSubjectList(
                            "",
                            "",
                            choosedClassListInfo[0].schoolId.toString()
                        )
                        llHaveChooseClass.setVisible(true)
                        rlSendHomeWorkChooseSubject.setVisible(true)
                    } else {
                        llHaveChooseClass.setVisible(false)
                        tvHomeWorkSubject.text = ""
                        if (tvSendHomeWorkTime.text.isEmpty()) {
                            etSendHomeWorkChooseEditTitle.text =
                                SpannableStringBuilder(DateUtils.getMonth().toString() + "月" + DateUtils.getDay().toString() + "日" + "作业")
                        } else {
                            etSendHomeWorkChooseEditTitle.text =
                                SpannableStringBuilder(selectMonth + "月" + selectDay + "日" + selectHour + "：" + selectMinute + "前提交")
                        }
                        subjectType = -1
                        rlSendHomeWorkChooseSubject.setVisible(false)
                    }
                }
                // 学生列表返回
                chooseStudentCodeFromSend -> {
                    if (data != null) {
                        val studentListInfo: java.util.ArrayList<StudentInClasses> =
                            data.getParcelableArrayListExtra<StudentInClasses>("studentInfo")
                        // 数量
                        var count = 0
                        for (item in studentListInfo) {
                            if (item.isChoose) {
                                count += 1
                            }
                        }
                        // 赋值
                        for (item in choosedClassListInfo) {
                            if (item.classId == studentListInfo[0].classId) {
                                item.chooseStudentNum = count
                                item.chooseStudentInfo = studentListInfo
                            }
                        }
                        tvChooseClassName.text =
                            choosedClassListInfo[0].grade + choosedClassListInfo[0].className + "班" + choosedClassListInfo[0].chooseStudentNum + "人"
                    }
                }
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

    private fun contentTextLimit() {
        if (etSendHomeWorkChooseEditContent.text.length >= 1000) {
            tvSendHomeWorkChooseEditContent.setTextColor(resources.getColor(R.color.select_red))
            tvSendHomeWorkChooseEditContent.text =
                etSendHomeWorkChooseEditContent.text.length.toString() + "/" + 1000
        } else {
            tvSendHomeWorkChooseEditContent.setTextColor(resources.getColor(R.color.text_xdt_hint))
            tvSendHomeWorkChooseEditContent.text =
                etSendHomeWorkChooseEditContent.text.length.toString() + "/" + 1000
        }
    }

    private fun titleTextLimit() {
        if (etSendHomeWorkChooseEditTitle.text.length >= 20) {
            tvSendHomeWorkChooseEditTitle.setTextColor(resources.getColor(R.color.select_red))
            tvSendHomeWorkChooseEditTitle.text =
                etSendHomeWorkChooseEditTitle.text.length.toString() + "/" + 20
        } else {
            tvSendHomeWorkChooseEditTitle.setTextColor(resources.getColor(R.color.text_xdt_hint))
            tvSendHomeWorkChooseEditTitle.text =
                etSendHomeWorkChooseEditTitle.text.length.toString() + "/" + 20
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

    // 获取作业类型回调
    override fun getWorkType(list: MutableList<EducationOrProfessionBean>?) {
        homeWorkTypeList.clear()
        if (list != null && list.size > 0) {
            homeWorkTypeList.addAll(list)
        }
    }

    // 获取学科列表回调
    override fun getSubjectList(subjectList: MutableList<SubjectListBean>?) {
        sendHomeWorkSubjectList.clear()
        if (subjectList != null && subjectList.size > 0) {
            sendHomeWorkSubjectList.addAll(subjectList)
        }
    }

    // 创建班级后的回调
    override fun createHomeWork(msg: String) {
//        clearHomeWorkSetting()
        ToastUtils.showMsg(this, msg)
        // 创建之后 直接跳转列表页 (没有选择班级 所以需要展示班级)
        val intent = Intent(this, HomeWorkTeacherListActivity::class.java)
        intent.putExtra("classId", 0)
        intent.putExtra("isHeaderTeacher", false)
        intent.putExtra("from", true)
        startActivity(intent)
        AppManager.instance.finishActivity(this)
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
}