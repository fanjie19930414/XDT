package com.kapok.apps.maple.xdt.home.activity

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.support.v7.widget.LinearLayoutManager
import android.text.format.DateFormat
import android.view.View
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import com.bumptech.glide.Glide
import com.chad.library.adapter.base.BaseQuickAdapter
import com.kapok.apps.maple.xdt.R
import com.kapok.apps.maple.xdt.home.adapter.ChildParentAdapter
import com.kapok.apps.maple.xdt.home.bean.ChildInfoBean
import com.kapok.apps.maple.xdt.home.bean.ChildParentBean
import com.kapok.apps.maple.xdt.home.presenter.ChildrenInfoPresenter
import com.kapok.apps.maple.xdt.home.presenter.view.ChildrenView
import com.kapok.apps.maple.xdt.utils.OssService
import com.kapok.apps.maple.xdt.utils.OssUIDisplayer
import com.kapok.apps.maple.xdt.utils.PermissionUtils
import com.kapok.apps.maple.xdt.utils.UploadPicUtils
import com.kotlin.baselibrary.activity.BaseMVPActivity
import com.kotlin.baselibrary.commen.BaseApplication
import com.kotlin.baselibrary.commen.BaseConstant
import com.kotlin.baselibrary.custom.*
import com.kotlin.baselibrary.ex.setVisible
import com.kotlin.baselibrary.rx.BaseRxBus
import com.kotlin.baselibrary.rx.event.EventChildrenUserInfoMsg
import com.kotlin.baselibrary.utils.*
import com.luck.picture.lib.PictureSelector
import com.luck.picture.lib.config.PictureConfig
import com.luck.picture.lib.config.PictureMimeType
import kotlinx.android.synthetic.main.activity_children_info.*
import kotlinx.android.synthetic.main.activity_user_info.*
import java.io.File
import java.util.*

/**
 * 孩子资料页
 * fanjie
 */
class ChildrenUserInfoActivity : BaseMVPActivity<ChildrenInfoPresenter>(), ChildrenView {
    // 学生Id
    private var studentId: Int = -1
    // 学生班级状态 (0:审核中 1：已通过)
    private var classState: Int = -1
    private var classId: Int = -1
    // 设置弹窗
    private lateinit var childrenSettingDialog: CustomCancelBottomDialog
    // 底部更换头像弹窗
    private lateinit var iconBottomDialog: CustomCancelBottomDialog
    // 拍照路径 / 相册路径
    private var photoPath = ""
    private var albumPath = ""
    // 姓名Request
    private val childrenName = 666
    // 性别底部弹窗
    private lateinit var sex: String
    private lateinit var bottomChildSexDialog: CustomBottomChildSexDialog
    // 生日底部弹窗
    private var hasBirthday: Boolean = false
    private lateinit var birthDay: String
    private lateinit var birthdayDataDialog: CustomDataDialog
    private lateinit var mSelectYear: String
    private lateinit var mSelectMonth: String
    private lateinit var mSelectDay: String
    // 班级信息
    private var schoolId = -1
    private lateinit var schoolName: String
    // 家长信息
    private lateinit var parentList: MutableList<ChildParentBean>
    private lateinit var childParentAdapter: ChildParentAdapter

    // OSS图片上传配置
    private var objectName: String = ""
    // 目前用的上传方式
    private lateinit var ossService: OssService
    // OSS上传进度UI
    private lateinit var ossUIDisplayer: OssUIDisplayer
    // 上传成功后返回的图片地址
    private var upLoadPicUrl: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_children_info)
        initView()
        initListener()
    }

    private fun initView() {
        mTitleTv.text = "孩子资料"
        // 获取传入的信息
        studentId = intent.getIntExtra("studentId", -1)
        classState = intent.getIntExtra("classState", -1)
        classId = intent.getIntExtra("classId", -1)
        sex = intent.getStringExtra("sex")
        if ("male" == sex) {
            tvChildrenInfoSex.text = "男孩"
        } else {
            tvChildrenInfoSex.text = "女孩"
        }
        if (classState == 1) {
            llChildrenClassInfo.setVisible(true)
        } else {
            llChildrenClassInfo.setVisible(false)
        }
        // Presenter
        mPresenter = ChildrenInfoPresenter(this)
        mPresenter.mView = this
        // OSS UI
        ossUIDisplayer = OssUIDisplayer(ImageView(this), ProgressBar(this), TextView(this), this)
        // 配置Rv
        parentList = arrayListOf()
        childParentAdapter = ChildParentAdapter(this, parentList)
        rvChildrenInfoList.layoutManager =
            LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        rvChildrenInfoList.adapter = childParentAdapter
        rvChildrenInfoList.addItemDecoration(
            RecycleViewDivider(
                this,
                RecycleViewDivider.VERTICAL,
                Dp2pxUtils.dp2px(this, 1)
            )
        )
        // 调用孩子信息接口
        mPresenter.getChildrenInfo(studentId, true)
    }

    private fun initListener() {
        // 返回
        mLeftIv.setOnClickListener { finish() }
        // 三个点
        mRightIv.setOnClickListener {
            childrenSettingDialog =
                CustomCancelBottomDialog(this@ChildrenUserInfoActivity, R.style.BottomDialog)
            // 判断是否有班级
            if (classState == 1) {
                childrenSettingDialog.addItem(
                    "退出班级",
                    R.color.text_xdt,
                    View.OnClickListener {
                        // 退出此班级
                        val confirmDialog = CancelConfirmDialog(
                            this@ChildrenUserInfoActivity, R.style.BottomDialog, "退出班级后将无法收到老师的消息" +
                                    "确认退出吗？", ""
                        )
                        confirmDialog.show()
                        confirmDialog.setOnClickConfirmListener(object :
                            CancelConfirmDialog.ClickConfirmListener {
                            override fun confirm() {
                                // 调用退出班级接口
                                mPresenter.exitClass(
                                    classId,
                                    studentId,
                                    1,
                                    AppPrefsUtils.getInt("userId")
                                )
                                confirmDialog.dismiss()
                            }
                        })
                        childrenSettingDialog.dismiss()
                    })
                childrenSettingDialog.addItem(
                    "解除绑定",
                    R.color.text_red,
                    View.OnClickListener {
                        // 解除绑定
                        val confirmDialog = CancelConfirmDialog(
                            this@ChildrenUserInfoActivity, R.style.BottomDialog, "解除绑定后将无法收到孩子的消息" +
                                    "确认解除绑定吗？", ""
                        )
                        confirmDialog.setConfirmContent("解绑")
                        confirmDialog.show()
                        confirmDialog.setOnClickConfirmListener(object :
                            CancelConfirmDialog.ClickConfirmListener {
                            override fun confirm() {
                                // 解除绑定接口
                                mPresenter.unBindChild(AppPrefsUtils.getInt("userId"), studentId)
                                confirmDialog.dismiss()
                            }
                        })
                        childrenSettingDialog.dismiss()
                    })
            } else {
                childrenSettingDialog.addItem(
                    "解除绑定",
                    R.color.text_red,
                    View.OnClickListener {
                        // 解除绑定
                        val confirmDialog = CancelConfirmDialog(
                            this@ChildrenUserInfoActivity,
                            R.style.BottomDialog,
                            "解除绑定后将无法收到孩子的消息\n" +
                                    "确认解除绑定吗？",
                            ""
                        )
                        confirmDialog.setConfirmContent("解绑")
                        confirmDialog.show()
                        confirmDialog.setOnClickConfirmListener(object :
                            CancelConfirmDialog.ClickConfirmListener {
                            override fun confirm() {
                                // 解除绑定接口
                                mPresenter.unBindChild(AppPrefsUtils.getInt("userId"), studentId)
                                confirmDialog.dismiss()
                            }
                        })
                        childrenSettingDialog.dismiss()
                    })
            }
            childrenSettingDialog.show()
        }
        // 头像
        rlChildrenInfoIcon.setOnClickListener {
            iconBottomDialog =
                CustomCancelBottomDialog(this@ChildrenUserInfoActivity, R.style.BottomDialog)
            iconBottomDialog.addItem(
                "拍照",
                R.color.login_xdt_btn_color_able,
                View.OnClickListener {
                    // （只要指定了 存储路径 裁剪就不起作用 目前已指定）
                    takePhoto()
                    iconBottomDialog.dismiss()
                })
            iconBottomDialog.addItem(
                "从相册选择",
                R.color.login_xdt_btn_color_able,
                View.OnClickListener {
                    PictureSelector.create(this@ChildrenUserInfoActivity)
                        .openGallery(PictureMimeType.ofImage())
                        .maxSelectNum(1)    // 最大选择数量
                        .imageSpanCount(4)  //每行选择数量
                        .selectionMode(PictureConfig.SINGLE) // 多选还是单选
                        .previewImage(true) //是否可预览图片
                        .isCamera(false)    // 是否显示拍摄按钮
                        .forResult(PictureConfig.CHOOSE_REQUEST)
                    iconBottomDialog.dismiss()
                })
            iconBottomDialog.show()
        }
        // 姓名
        rlChildrenInfoName.setOnClickListener {
            val intent = Intent(this@ChildrenUserInfoActivity, UserInfoEditActivity::class.java)
            intent.putExtra("isChild", true)
            startActivityForResult(intent, childrenName)
        }
        // 性别
        rlChildrenInfoSex.setOnClickListener {
            bottomChildSexDialog =
                CustomBottomChildSexDialog(this@ChildrenUserInfoActivity, R.style.BottomDialog)
            bottomChildSexDialog.show()
            bottomChildSexDialog.setIsBoyorGirl(
                object : CustomBottomChildSexDialog.IsBoyorGirl {
                    override fun chooseBoy(boolean: Boolean) {
                        if (boolean) {
                            sex = "male"
                            tvChildrenInfoSex.text = "男孩"
                        } else {
                            sex = "famale"
                            tvChildrenInfoSex.text = "女孩"
                        }
                        // 更新接口
                        mPresenter.updateUserInfo(
                            "",
                            birthDay,
                            "",
                            0,
                            "",
                            tvChildrenInfoName.text.toString(),
                            schoolId,
                            schoolName,
                            sex,
                            -1,
                            "",
                            "",
                            studentId
                        )
                    }
                }
            )
        }
        // 生日
        rlChildrenInfoBirthday.setOnClickListener {
            birthdayDataDialog =
                CustomDataDialog(this@ChildrenUserInfoActivity, R.style.BottomDialog)
            birthdayDataDialog.setTitle("请选择孩子的生日")
            if (hasBirthday) {
                birthdayDataDialog.setSelectedTime(mSelectYear, mSelectMonth, mSelectDay)
            } else {
                birthdayDataDialog.getCurrentTime()
            }
            birthdayDataDialog.show()
            birthdayDataDialog.setOnselectDataListener(object :
                CustomDataDialog.SelectDataListener {
                override fun selectData(year: String, month: String, day: String) {
                    hasBirthday = true
                    mSelectYear = year
                    mSelectMonth = (if (Integer.valueOf(month) > 9) month else "0$month").toString()
                    mSelectDay = (if (Integer.valueOf(day) > 9) day else "0$day").toString()
                    val birthdayData = "$mSelectYear-$mSelectMonth-$mSelectDay"
                    birthDay = birthdayData + "   " + DateUtils.getConstellation(birthdayData)
                    tvChildrenInfoBirthday.text = birthDay
                    // 更新接口
                    mPresenter.updateUserInfo(
                        "",
                        birthDay,
                        "",
                        0,
                        "",
                        tvChildrenInfoName.text.toString(),
                        schoolId,
                        schoolName,
                        sex,
                        -1,
                        "",
                        "",
                        studentId
                    )
                }
            })
        }
        // 家长信息
        childParentAdapter.onItemClickListener =
            BaseQuickAdapter.OnItemClickListener { adapter, view, position ->
                val intent =
                    Intent(this@ChildrenUserInfoActivity, ParentUserInfoActivity::class.java)
                intent.putExtra("parentUserId", parentList[position].userId)
                intent.putExtra("studentId", studentId)
                startActivity(intent)
            }
        // 上传头像监听
        ossUIDisplayer.setOnUpLoadResultListener(object : OssUIDisplayer.UpLoadResult {
            override fun upLoadOK() {
                upLoadPicUrl = BaseConstant.ossPicUrl + objectName
                // 更新接口
                mPresenter.updateUserInfo(
                    upLoadPicUrl,
                    birthDay,
                    "",
                    0,
                    "",
                    tvChildrenInfoName.text.toString(),
                    schoolId,
                    schoolName,
                    sex,
                    -1,
                    "",
                    "",
                    studentId
                )
            }

            override fun upLoadFAIL(info: String?) {
                mPresenter.mView.onDismissDialog()
                info?.let { ToastUtils.showMsg(this@ChildrenUserInfoActivity, it) }
            }
        })
    }

    //  拍照权限
    private fun takePhoto() {
        PermissionUtils.checkPermission(
            this,
            arrayOf(Manifest.permission.CAMERA),
            object : PermissionUtils.CheckResultListener {
                override fun checkFailure(permissions: Array<out String>?) {
                    ToastUtils.showMsg(this@ChildrenUserInfoActivity, "无法获取拍照权限")
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

    // 裁剪图片
    private fun cropImageUri(uri: Uri, outUri: Uri, requestCode: Int, isHead: Boolean) {
        val intent = Intent("com.android.camera.action.CROP")
        intent.setDataAndType(uri, "image/*")
        intent.putExtra("crop", "true")
        if (isHead) {
            intent.putExtra("aspectX", 3)
            intent.putExtra("aspectY", 4)
            intent.putExtra("outputX", 300)
            intent.putExtra("outputY", 400)
        } else {
            intent.putExtra("aspectX", 8)
            intent.putExtra("aspectY", 5)
            intent.putExtra("outputX", 800)
            intent.putExtra("outputY", 500)
        }
        intent.putExtra("scale", true)
        intent.putExtra(MediaStore.EXTRA_OUTPUT, outUri)
        intent.putExtra("return-data", false)
        intent.putExtra("outputFormat", Bitmap.CompressFormat.JPEG.toString())
        intent.putExtra("noFaceDetection", true)
        startActivityForResult(intent, requestCode)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            when (requestCode) {
                // 拍照
                BaseConstant.TAKE_A_PHOTO -> {
                    val uri = Uri.fromFile(File(photoPath))
                    cropImageUri(uri, uri, BaseConstant.CROP_PICTURE, true)
                }
                // 拍照后裁剪之后的图片
                BaseConstant.CROP_PICTURE -> {
                    ossService = UploadPicUtils.initOSS(applicationContext,BaseConstant.endPoint,BaseConstant.bucketName,ossUIDisplayer)
                    objectName = DateUtils.getOSSObjectName()
                    ossService.asyncPutImage(objectName,photoPath)
                    mPresenter.mView.onShowDialog()
                    // GlideUtils.loadImage(this, photoPath, civIconChildren)
                }
                // 相册
                PictureConfig.CHOOSE_REQUEST -> {
                    // 例如 LocalMedia 里面返回三种path
                    // 1.media.getPath(); 为原图path
                    // 2.media.getCutPath();为裁剪后path，需判断media.isCut();是否为true
                    // 3.media.getCompressPath();为压缩后path，需判断media.isCompressed();是否为true
                    // 如果裁剪并压缩了，以取压缩路径为准，因为是先裁剪后压缩的
                    val images = PictureSelector.obtainMultipleResult(data)
                    albumPath = images[0].path
                    val resultUri = Uri.fromFile(File(images[0].path))
                    val albumPath = BaseApplication.getImageFolderPath() + DateFormat.format("yyyy-MM-dd-hh-mm-ss", Date()) + ".jpg"
                    val outAlbumUri = Uri.fromFile(File(albumPath))
                    cropImageUri(resultUri, outAlbumUri, BaseConstant.ALBUM_PICTURE, true)
                }
                // 相册裁剪后的图片
                BaseConstant.ALBUM_PICTURE -> {
                    ossService = UploadPicUtils.initOSS(applicationContext,BaseConstant.endPoint,BaseConstant.bucketName,ossUIDisplayer)
                    objectName = DateUtils.getOSSObjectName()
                    ossService.asyncPutImage(objectName,albumPath)
                    mPresenter.mView.onShowDialog()
                    // GlideUtils.loadImage(this, albumPath, civIconChildren)
                }
                // 修改姓名
                childrenName -> {
                    val name = data!!.getStringExtra("EditName")
                    tvChildrenInfoName.text = name
                    // 更新接口
                    mPresenter.updateUserInfo(
                        "",
                        birthDay,
                        "",
                        0,
                        "",
                        tvChildrenInfoName.text.toString(),
                        schoolId,
                        schoolName,
                        sex,
                        -1,
                        "",
                        "",
                        studentId
                    )
                }
            }
        }
    }

    // 获取孩子信息回调
    override fun getChildInfo(bean: ChildInfoBean) {
        // 头像
        if (bean.avatar != null && bean.avatar.isNotEmpty()) {
            GlideUtils.loadImage(this, bean.avatar, civIconChildren)
        } else {
            Glide.with(this).load(R.mipmap.def_head_boy).into(civIconChildren)
        }
        // 姓名
        tvChildrenInfoName.text = bean.realName
        // 生日
        if (bean.birthday != null && bean.birthday.isNotEmpty()) {
            birthDay = bean.birthday
            tvChildrenInfoBirthday.text = bean.birthday
        } else {
            birthDay = ""
            tvChildrenInfoBirthday.text = ""
        }
        // 学校信息
        if (bean.schoolId != null) {
            schoolId = bean.schoolId
        }
        schoolName = if (bean.schoolName != null && bean.schoolName.isNotEmpty()) {
            bean.schoolName
        } else {
            ""
        }
        tvChildrenInfoSchool.text = schoolName
        // 班级信息
        tvChildrenInfoClass.text = bean.className
        tvChildrenInfoTeacher.text = bean.classTeacherName
        // 家长信息
        parentList.clear()
        if (bean.patriarchInfos != null && bean.patriarchInfos.size > 0) {
            parentList.addAll(bean.patriarchInfos)
            childParentAdapter.notifyDataSetChanged()
        }
    }

    // 退出班级回调
    override fun exitClass(msg: String) {
        ToastUtils.showMsg(this@ChildrenUserInfoActivity, msg)
        BaseRxBus.mBusInstance.post(EventChildrenUserInfoMsg("更新孩子信息"))
    }

    // 解除绑定回调
    override fun unBindChild(msg: String) {
        ToastUtils.showMsg(this@ChildrenUserInfoActivity, msg)
        BaseRxBus.mBusInstance.post(EventChildrenUserInfoMsg("更新孩子信息"))
        finish()
    }

    // 更新信息回调
    override fun updateUserInfoResult(msg: String) {
        mPresenter.mView.onDismissDialog()
        ToastUtils.showMsg(this@ChildrenUserInfoActivity, msg)
        // 刷新孩子信息
        mPresenter.getChildrenInfo(studentId,true)
        BaseRxBus.mBusInstance.post(EventChildrenUserInfoMsg("更新孩子信息"))
    }
}