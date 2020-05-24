package com.kapok.apps.maple.xdt.home.activity

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.text.format.DateFormat
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import com.alibaba.sdk.android.oss.ClientException
import com.alibaba.sdk.android.oss.ServiceException
import com.alibaba.sdk.android.oss.model.PutObjectRequest
import com.alibaba.sdk.android.oss.model.PutObjectResult
import com.bumptech.glide.Glide
import com.kapok.apps.maple.xdt.R
import com.kapok.apps.maple.xdt.home.bean.EducationOrProfessionBean
import com.kapok.apps.maple.xdt.home.bean.IdentityBean
import com.kapok.apps.maple.xdt.home.bean.UserInfoBean
import com.kapok.apps.maple.xdt.home.presenter.UserInfoPresenter
import com.kapok.apps.maple.xdt.home.presenter.view.UserInfoView
import com.kapok.apps.maple.xdt.usercenter.activity.introduce_teacher.SchoolLocationActivity
import com.kapok.apps.maple.xdt.usercenter.activity.introduce_teacher.TeacherEditInfoActivity
import com.kapok.apps.maple.xdt.usercenter.activity.login.LoginMobileActivity
import com.kapok.apps.maple.xdt.usercenter.bean.SubjectListBean
import com.kapok.apps.maple.xdt.utils.*
import com.kotlin.baselibrary.activity.BaseMVPActivity
import com.kotlin.baselibrary.commen.*
import com.kotlin.baselibrary.custom.CustomAlertMiddleDialog
import com.kotlin.baselibrary.custom.CustomBottomChildSexDialog
import com.kotlin.baselibrary.custom.CustomBottomDialog
import com.kotlin.baselibrary.custom.CustomCancelBottomDialog
import com.kotlin.baselibrary.rx.BaseRxBus
import com.kotlin.baselibrary.rx.event.EventChangeUserIdentity
import com.kotlin.baselibrary.rx.event.EventChildrenUserInfoMsg
import com.kotlin.baselibrary.utils.AppPrefsUtils
import com.kotlin.baselibrary.utils.DateUtils
import com.kotlin.baselibrary.utils.GlideUtils
import com.kotlin.baselibrary.utils.ToastUtils
import kotlinx.android.synthetic.main.activity_user_info.*
import java.io.File
import com.luck.picture.lib.PictureSelector
import com.luck.picture.lib.config.PictureConfig
import com.luck.picture.lib.config.PictureMimeType
import java.util.*
import kotlin.collections.ArrayList


/**
 *  个人资料页
 */
class UserInfoActivity : BaseMVPActivity<UserInfoPresenter>(), UserInfoView {
    // 身份Id （0 学生;1 家长;2 老师）
    private var identity: Int = -1
    // 姓名Request
    private val NAME = 500
    private val PHONE = 600
    // 底部性别弹窗
    private lateinit var bottomSexDialog: CustomBottomChildSexDialog
    private var sex: String = ""
    // 底部学历弹窗
    private lateinit var educationList: MutableList<EducationOrProfessionBean>
    private lateinit var bottomEducationDialog: CustomBottomDialog
    private var hasEducation = false
    private var educationId: String = ""
    private var educationName: String = ""
    // 底部职业弹窗
    private lateinit var perfessionList: MutableList<EducationOrProfessionBean>
    private lateinit var bottomPerfessionDialog: CustomBottomDialog
    private var hasPerfession = false
    private var perfessionId: String = ""
    private var perfessionName: String = ""
    // 身份弹窗
    private val identityList: ArrayList<String> = arrayListOf("家长", "老师")
    private lateinit var bottomIdentityDialog: CustomBottomDialog
    // 底部更换头像弹窗
    private lateinit var iconBottomDialog: CustomCancelBottomDialog
    // 退出登录弹窗
    private lateinit var exitMiddleDialog: CustomAlertMiddleDialog
    // 拍照保存路径
    private lateinit var photoPath: String
    // 相册压缩后返回路径
    private lateinit var albumPath: String
    // 返回的学校id 选中学科的ID
    private lateinit var bottomTeacherDialog: CustomBottomDialog
    private var schoolId: Int = -1
    private var subjectId: Int = -1
    private var subjectName: String = ""
    private var selectSchool: String = ""
    private val subjectList: ArrayList<SubjectListBean> = arrayListOf()
    private var subjectListString = arrayListOf<String>()
    private var hasSubject: Boolean = false
    // 学校RequestCode
    private val SCHOOL = 700
    // 用户身份列表
    private var identityUserList: MutableList<IdentityBean> = arrayListOf()

    // OSS Instance（他们说的上传方式 没有说明 看不懂 暂废弃）
    private lateinit var instance: OSSPicUploadFactory
    private var objectName: String = ""

    // 目前用的上传方式
    private lateinit var ossService: OssService
    // OSS上传进度UI
    private lateinit var ossUIDisplayer: OssUIDisplayer
    // 上传成功后返回的图片地址
    private var upLoadPicUrl: String = ""

    // 当前账号是否有2个身份
    private var hasTwoIdentity: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user_info)
        initView()
        initListener()
    }

    private fun initView() {
        mPresenter = UserInfoPresenter(this)
        mPresenter.mView = this
        // 调用用户信息接口
        identity = AppPrefsUtils.getString("identity").toInt()
        mPresenter.getUserInfo(AppPrefsUtils.getInt("userId"), identity)
        // 获取用户身份列表
        mPresenter.getIdentityInfo(AppPrefsUtils.getInt("userId"))
        // OSS UI
        ossUIDisplayer = OssUIDisplayer(ImageView(this), bar, TextView(this), this)
    }

    private fun initListener() {
        // 头像 (目前仅作了 本地的处理  上传压缩的处理还没写)
        rlUserIcon.setOnClickListener {
            iconBottomDialog =
                CustomCancelBottomDialog(this@UserInfoActivity, R.style.BottomDialog)
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
                    PictureSelector.create(this@UserInfoActivity)
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
        rlUserName.setOnClickListener {
            val intent = Intent(this@UserInfoActivity, UserInfoEditActivity::class.java)
            intent.putExtra("isChild", false)
            startActivityForResult(intent, NAME)
        }
        // 性别
        rlUserSex.setOnClickListener {
            bottomSexDialog =
                CustomBottomChildSexDialog(this@UserInfoActivity, R.style.BottomDialog, identity)
            bottomSexDialog.show()
            bottomSexDialog.setIsBoyorGirl(
                object : CustomBottomChildSexDialog.IsBoyorGirl {
                    override fun chooseBoy(boolean: Boolean) {
                        if (boolean) {
                            tvUserSex.text = "男"
                            sex = "male"
                        } else {
                            tvUserSex.text = "女"
                            sex = "famale"
                        }
                        mPresenter.updateUserInfo(
                            upLoadPicUrl,
                            "",
                            educationName,
                            identity,
                            perfessionName,
                            tvUserName.text.toString(),
                            schoolId,
                            selectSchool,
                            sex,
                            subjectId,
                            subjectName,
                            tvUserNumber.text.toString(),
                            AppPrefsUtils.getInt("userId")
                        )
                    }
                }
            )
        }
        // 联系方式
        rlUserNumber.setOnClickListener {
            startActivityForResult(
                Intent(
                    this@UserInfoActivity,
                    UserInfoEditPhoneActivity::class.java
                ), PHONE
            )
        }
        // 学历（学校）
        rlUserEducation.setOnClickListener {
            // 学历
            if (identity == 1) {
                val educationListString = arrayListOf<String>()
                bottomEducationDialog =
                    CustomBottomDialog(this@UserInfoActivity, R.style.BottomDialog)
                bottomEducationDialog.setTitle("我的学历")
                if (hasEducation) {
                    for (item in educationList) {
                        educationListString.add(item.name)
                    }
                    bottomEducationDialog.addItem(educationListString, tvUserRecord.text.toString())
                } else {
                    for (item in educationList) {
                        educationListString.add(item.name)
                    }
                    bottomEducationDialog.addItem(educationListString, "")
                }
                bottomEducationDialog.show()
                bottomEducationDialog.setOnselectTextListener(object :
                    CustomBottomDialog.SelectTextListener {
                    override fun selectText(text: String) {
                        tvUserRecord.text = text
                        educationName = text
                        for (item in educationList) {
                            if (text == item.name) {
                                // 记录选中的关系Id
                                educationId = item.id
                            }
                        }
                        hasEducation = true
                        mPresenter.updateUserInfo(
                            upLoadPicUrl,
                            "",
                            educationName,
                            identity,
                            perfessionName,
                            tvUserName.text.toString(),
                            schoolId,
                            selectSchool,
                            sex,
                            subjectId,
                            subjectName,
                            tvUserNumber.text.toString(),
                            AppPrefsUtils.getInt("userId")
                        )
                    }
                })
            } else if (identity == 2) {
                // 跳转选择学校页
                val intent = Intent()
                intent.putExtra("selectSchoolName", selectSchool)
                intent.putExtra("selectSchoolId", schoolId)
                intent.setClass(this@UserInfoActivity, SchoolLocationActivity::class.java)
                startActivityForResult(intent, SCHOOL)
            }
        }
        // 职业(学科)
        rlUserWork.setOnClickListener {
            // 学历
            if (identity == 1) {
                val workListString = arrayListOf<String>()
                bottomPerfessionDialog =
                    CustomBottomDialog(this@UserInfoActivity, R.style.BottomDialog)
                bottomPerfessionDialog.setTitle("我的职业")
                if (hasPerfession) {
                    for (item in perfessionList) {
                        workListString.add(item.name)
                    }
                    bottomPerfessionDialog.addItem(workListString, tvUserWork.text.toString())
                } else {
                    for (item in perfessionList) {
                        workListString.add(item.name)
                    }
                    bottomPerfessionDialog.addItem(workListString, "")
                }
                bottomPerfessionDialog.show()
                bottomPerfessionDialog.setOnselectTextListener(object :
                    CustomBottomDialog.SelectTextListener {
                    override fun selectText(text: String) {
                        tvUserWork.text = text
                        perfessionName = text
                        for (item in perfessionList) {
                            if (text == item.name) {
                                // 记录选中的关系Id
                                perfessionId = item.id
                            }
                        }
                        hasPerfession = true
                        mPresenter.updateUserInfo(
                            upLoadPicUrl,
                            "",
                            educationName,
                            identity,
                            perfessionName,
                            tvUserName.text.toString(),
                            schoolId,
                            selectSchool,
                            sex,
                            subjectId,
                            subjectName,
                            tvUserNumber.text.toString(),
                            AppPrefsUtils.getInt("userId")
                        )
                    }
                })
            } else if (identity == 2) {
                if (schoolId == -1) {
                    ToastUtils.showMsg(this, "请先选择学校")
                } else {
                    bottomTeacherDialog =
                        CustomBottomDialog(this@UserInfoActivity, R.style.BottomDialog)
                    bottomTeacherDialog.setTitle("选择学科")
                    if (hasSubject) {
                        bottomTeacherDialog.addItem(subjectListString, tvUserWork.text.toString())
                    } else {
                        bottomTeacherDialog.addItem(subjectListString, "")
                    }
                    bottomTeacherDialog.show()
                    bottomTeacherDialog.setOnselectTextListener(object :
                        CustomBottomDialog.SelectTextListener {
                        override fun selectText(text: String) {
                            tvUserWork.text = text
                            subjectName = text
                            for (item in subjectList) {
                                if (text == item.subjectName) {
                                    subjectId = item.subjectId
                                }
                            }
                            hasSubject = true
                            mPresenter.updateUserInfo(
                                upLoadPicUrl,
                                "",
                                educationName,
                                identity,
                                perfessionName,
                                tvUserName.text.toString(),
                                schoolId,
                                selectSchool,
                                sex,
                                subjectId,
                                subjectName,
                                tvUserNumber.text.toString(),
                                AppPrefsUtils.getInt("userId")
                            )
                        }
                    })
                }
            }
        }
        // 身份
        rlUserRelation.setOnClickListener {
            bottomIdentityDialog = CustomBottomDialog(this@UserInfoActivity, R.style.BottomDialog)
            bottomIdentityDialog.setTitle("选择身份")
            when (identity) {
                1 -> bottomIdentityDialog.addItem(identityList, "家长")
                2 -> bottomIdentityDialog.addItem(identityList, "老师")
                else -> bottomIdentityDialog.addItem(identityList, "")
            }
            bottomIdentityDialog.show()
            bottomIdentityDialog.setOnselectTextListener(object :
                CustomBottomDialog.SelectTextListener {
                override fun selectText(text: String) {
                    tvUserRelation.text = text
                    if (text == "家长") {
                        // 之前为老师身份
                        if (BaseUserInfo.identity != 1) {
                            // 老师身份切换家长身份直接切换  记录下老身份
                            val oldIdentity = BaseUserInfo.identity
                            // 本地再更记录下身份信息
                            identity = 1
                            BaseUserInfo.identity = 1
                            AppPrefsUtils.putString("identity", identity.toString())
                            // 更新用户信息
                            BaseRxBus.mBusInstance.post(EventChangeUserIdentity(identity))
                            mPresenter.changeIdentity(oldIdentity,AppPrefsUtils.getInt("userId"))
                        }
                    } else if (text == "老师") {
                        // 之前为家长身份
                        if (BaseUserInfo.identity != 2) {
                            // 家长身份切换老师 需要重新完善信息
                            if (hasTwoIdentity) {
                                identity = 2
                                BaseUserInfo.identity = 2
                                AppPrefsUtils.putString("identity", identity.toString())
                                // 更改用户身份
                                BaseRxBus.mBusInstance.post(EventChangeUserIdentity(identity))
                                mPresenter.updateUserInfo(
                                    upLoadPicUrl,
                                    "",
                                    educationName,
                                    identity,
                                    perfessionName,
                                    tvUserName.text.toString(),
                                    schoolId,
                                    selectSchool,
                                    sex,
                                    subjectId,
                                    subjectName,
                                    tvUserNumber.text.toString(),
                                    AppPrefsUtils.getInt("userId")
                                )
                            } else {
                                // 重新完善信息
                                startActivity(Intent(this@UserInfoActivity, TeacherEditInfoActivity::class.java))
                            }
                        }
                    }
                }
            })
        }
        // 退出登录
        rlUserExit.setOnClickListener {
            exitMiddleDialog = CustomAlertMiddleDialog(this@UserInfoActivity, R.style.BottomDialog)
            exitMiddleDialog.addItem(
                "确定",
                R.color.text_xdt,
                "退出后将无法再接收消息",
                "",
                View.OnClickListener {
                    AppPrefsUtils.remove("token")
                    AppPrefsUtils.remove("userId")
                    AppPrefsUtils.remove("identity")
                    AppPrefsUtils.remove("userName")
                    if (BaseUserInfo.identity == 1) {
                        AppPrefsUtils.remove("ParentStudentId")
                        AppPrefsUtils.remove("ParentClassId")
                    } else {
                        AppPrefsUtils.remove("TeacherClassId")
                    }
                    startActivity(Intent(this@UserInfoActivity, LoginMobileActivity::class.java))
                    AppManager.instance.finishAllActivity()
                    exitMiddleDialog.dismiss()
                })
            exitMiddleDialog.show()
        }
        // 上传头像监听
        ossUIDisplayer.setOnUpLoadResultListener(object : OssUIDisplayer.UpLoadResult {
            override fun upLoadOK() {
                upLoadPicUrl = BaseConstant.ossPicUrl + objectName
                mPresenter.updateUserInfo(
                    upLoadPicUrl,
                    "",
                    educationName,
                    identity,
                    perfessionName,
                    tvUserName.text.toString(),
                    schoolId,
                    selectSchool,
                    sex,
                    subjectId,
                    subjectName,
                    tvUserNumber.text.toString(),
                    AppPrefsUtils.getInt("userId")
                )

            }

            override fun upLoadFAIL(info: String?) {
                mPresenter.mView.onDismissDialog()
                info?.let { ToastUtils.showMsg(this@UserInfoActivity, it) }
            }
        })
    }

    /**
     * 拍照
     */
    private fun takePhoto() {
        PermissionUtils.checkPermission(
            this,
            arrayOf(Manifest.permission.CAMERA),
            object : PermissionUtils.CheckResultListener {
                override fun checkFailure(permissions: Array<out String>?) {
                    ToastUtils.showMsg(this@UserInfoActivity, "无法获取拍照权限")
                }

                override fun checkSuccess() {
                    photoPath = BaseApplication.getImageFolderPath() + DateFormat.format(
                        "yyyy-MM-dd-hh-mm-ss",
                        Date()
                    ) + ".jpg"
                    val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
                    intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(File(photoPath)))
                    startActivityForResult(intent, BaseConstant.TAKE_A_PHOTO)
                }
            })
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            when (requestCode) {
                // 拍照后回调
                BaseConstant.TAKE_A_PHOTO -> {
                    val uri = Uri.fromFile(File(photoPath))
                    cropImageUri(uri, uri, BaseConstant.CROP_PICTURE, true)
                }
                // 相册选择回调
                PictureConfig.CHOOSE_REQUEST -> {
                    // 例如 LocalMedia 里面返回三种path
                    // 1.media.getPath(); 为原图path
                    // 2.media.getCutPath();为裁剪后path，需判断media.isCut();是否为true
                    // 3.media.getCompressPath();为压缩后path，需判断media.isCompressed();是否为true
                    // 如果裁剪并压缩了，以取压缩路径为准，因为是先裁剪后压缩的
                    val images = PictureSelector.obtainMultipleResult(data)
                    albumPath = images[0].path
                    val resultUri = Uri.fromFile(File(images[0].path))
                    val albumPath = BaseApplication.getImageFolderPath() + DateFormat.format(
                        "yyyy-MM-dd-hh-mm-ss",
                        Date()
                    ) + ".jpg"
                    val outAlbumUri = Uri.fromFile(File(albumPath))
                    cropImageUri(resultUri, outAlbumUri, BaseConstant.ALBUM_PICTURE, true)
                }
                // 拍照后裁剪之后的图片
                BaseConstant.CROP_PICTURE -> {
                    // 测试上传图片至OSS
                    ossService = UploadPicUtils.initOSS(
                        applicationContext,
                        BaseConstant.endPoint,
                        BaseConstant.bucketName,
                        ossUIDisplayer
                    )
                    objectName = DateUtils.getOSSObjectName()
                    ossService.asyncPutImage(objectName, photoPath)
                    mPresenter.mView.onShowDialog()
                    Log.w("OSSObjectName", objectName)
                    Log.w("OSSObjectPicUrl", BaseConstant.ossPicUrl + objectName)
                }
                // 相册裁剪后的图片
                BaseConstant.ALBUM_PICTURE -> {
                    // 测试上传图片至OSS
                    ossService = UploadPicUtils.initOSS(
                        applicationContext,
                        BaseConstant.endPoint,
                        BaseConstant.bucketName,
                        ossUIDisplayer
                    )
                    objectName = DateUtils.getOSSObjectName()
                    ossService.asyncPutImage(objectName, albumPath)
                    mPresenter.mView.onShowDialog()
                    Log.w("OSSObjectName", objectName)
                    Log.w("OSSObjectPicUrl", BaseConstant.ossPicUrl + objectName)
                }
                NAME -> {
                    val name = data!!.getStringExtra("EditName")
                    tvUserName.text = name
                    mPresenter.updateUserInfo(
                        upLoadPicUrl,
                        "",
                        educationName,
                        identity,
                        perfessionName,
                        tvUserName.text.toString(),
                        schoolId,
                        selectSchool,
                        sex,
                        subjectId,
                        subjectName,
                        tvUserNumber.text.toString(),
                        AppPrefsUtils.getInt("userId")
                    )
                }
                PHONE -> {
                    val phone = data!!.getStringExtra("EditPhone")
                    tvUserNumber.text = phone
                    mPresenter.updateUserInfo(
                        upLoadPicUrl,
                        "",
                        educationName,
                        identity,
                        perfessionName,
                        tvUserName.text.toString(),
                        schoolId,
                        selectSchool,
                        sex,
                        subjectId,
                        subjectName,
                        tvUserNumber.text.toString(),
                        AppPrefsUtils.getInt("userId")
                    )
                }
                SCHOOL -> {
                    selectSchool = data!!.getStringExtra("selectSchool")
                    schoolId = data.getIntExtra("selectSchoolId", -1)
                    tvUserRecord.text = selectSchool
                    mPresenter.updateUserInfo(
                        upLoadPicUrl,
                        "",
                        educationName,
                        identity,
                        perfessionName,
                        tvUserName.text.toString(),
                        schoolId,
                        selectSchool,
                        sex,
                        subjectId,
                        subjectName,
                        tvUserNumber.text.toString(),
                        AppPrefsUtils.getInt("userId")
                    )
                }
            }
        }
    }

    // 裁剪图片
    private fun cropImageUri(uri: Uri, outUri: Uri, requestCode: Int, isHead: Boolean) {
        val intent = Intent("com.android.camera.action.CROP")
        intent.setDataAndType(uri, "image/*")
        intent.putExtra("crop", "true")
        if (isHead) {
            intent.putExtra("aspectX", 4)
            intent.putExtra("aspectY", 4)
            intent.putExtra("outputX", 400)
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
        intent.putExtra("outputFormat", Bitmap.CompressFormat.JPEG)
        intent.putExtra("noFaceDetection", true)
        startActivityForResult(intent, requestCode)
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        PermissionUtils.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }

    // 个人信息接口回调
    override fun getUserInfoBean(bean: UserInfoBean) {
        // 将接口返回的信息存到本地SP中
        AppPrefsUtils.putInt("userId", bean.userId)
        BaseUserInfo.userId = bean.userId
        if (bean.identityType != null) {
            AppPrefsUtils.putString("identity", bean.identityType.toString())
            BaseUserInfo.identity = bean.identityType
        } else {
            AppPrefsUtils.putString("identity", "")
        }
        if (bean.realName.isNotEmpty()) {
            AppPrefsUtils.putString("userName", bean.realName)
            BaseUserInfo.userName = bean.realName
        }
        // 判断身份
        identity = BaseUserInfo.identity
        if (identity == 1) {
            tvUserEducation.text = "学历"
            tvUserVocation.text = "职业"
            tvUserRelation.text = "家长"
            // 获取学历接口
            educationList = arrayListOf()
            mPresenter.getEducationList()
            // 获取职业接口
            perfessionList = arrayListOf()
            mPresenter.getPerfessionList()
        } else if (identity == 2) {
            tvUserEducation.text = "学校"
            tvUserVocation.text = "学科"
            tvUserRelation.text = "老师"
            if (bean.schoolId != null) {
                schoolId = bean.schoolId
                selectSchool = bean.schoolName
            }
            if (bean.subjectId != null) {
                hasSubject = true
                subjectId = bean.subjectId
                subjectName = bean.subjectName
            }
            // 获取学科列表
            mPresenter.getSubjectList("", "", bean.schoolId.toString())
        }
        // 头像
        if (bean.avatar != null && bean.avatar.isNotEmpty()) {
            GlideUtils.loadImage(this, bean.avatar, civIcon)
        } else {
            Glide.with(this).load(R.mipmap.def_head_boy).into(civIcon)
        }
        // 名字
        tvUserName.text = bean.realName
        // 性别
        if (bean.sex == "male") {
            tvUserSex.text = "男"
            sex = "male"
        } else {
            tvUserSex.text = "女"
            sex = "famale"
        }
        // 电话
        tvUserNumber.text = bean.telephone
        if (identity == 1) {
            tvUserRelation.text = "家长"
        } else if (identity == 2) {
            tvUserRelation.text = "老师"
        }
        // 学历
        if (identity == 1) {
            tvUserRecord.text = bean.education
            tvUserWork.text = bean.job
        } else {
            tvUserRecord.text = bean.schoolName
            tvUserWork.text = bean.subjectName
        }
        // 身份
        if (bean.identityType == 1) {
            tvUserRelation.text = "家长"
        } else {
            tvUserRelation.text = "老师"
        }
    }

    // 获取学历集合
    override fun getEducationList(bean: MutableList<EducationOrProfessionBean>?) {
        educationList.clear()
        if (bean != null && bean.size > 0) {
            educationList.addAll(bean)
        }
    }

    // 获取职业集合
    override fun getPerfessionList(bean: MutableList<EducationOrProfessionBean>?) {
        perfessionList.clear()
        if (bean != null && bean.size > 0) {
            perfessionList.addAll(bean)
        }
    }

    // 更新用户信息
    override fun updateUserInfoResult(msg: String) {
        mPresenter.mView.onDismissDialog()
        ToastUtils.showMsg(this, msg)
        mPresenter.getUserInfo(AppPrefsUtils.getInt("userId"), identity, false)
        // 更新用户信息
        BaseRxBus.mBusInstance.post(EventChildrenUserInfoMsg("更新用户信息"))
    }

    // 切换用户身份
    override fun changeIdentity(msg: String) {
        mPresenter.mView.onDismissDialog()
        ToastUtils.showMsg(this, msg)
        mPresenter.getUserInfo(AppPrefsUtils.getInt("userId"), identity, false)
        // 更新用户信息
        BaseRxBus.mBusInstance.post(EventChildrenUserInfoMsg("更新用户信息"))
    }

    // 获取学科列表接口
    override fun getSubjectList(bean: MutableList<SubjectListBean>?) {
        subjectList.clear()
        subjectListString.clear()
        if (bean != null) {
            if (bean.isNotEmpty()) {
                subjectList.addAll(bean)
                for (item in subjectList) {
                    subjectListString.add(item.subjectName)
                }
            }
        }
    }

    // 获取个人身份类
    override fun getIdentityInfo(bean: MutableList<IdentityBean>) {
        identityUserList.clear()
        if (bean.size > 0) {
            identityUserList.addAll(bean)
        }
        // 判断当前用户身份
        if (identityUserList.size == 2) {
            hasTwoIdentity = true
        } else if (identityUserList.size == 1) {
            hasTwoIdentity = false
        }
    }

    override fun getOSSToken(bean: BaseOSSBean, type: Int) {
        instance = OSSPicUploadFactory.getOSSPicUploadInstance(
            applicationContext,
            bean.accessId,
            bean.policy,
            bean.signature
        )
        if (objectName.isNotEmpty()) {
            if (type == BaseConstant.CROP_PICTURE) {
                instance.upLoadPicToOSS(photoPath, objectName)
            } else {
                instance.upLoadPicToOSS(albumPath, objectName)
            }
        } else {
            ToastUtils.showMsg(this, "ObjectName为空")
        }
        // 上传回调监听
        instance.setUpLoadListener(object : UpLoadListener {
            override fun upLoadSuccess(request: PutObjectRequest, result: PutObjectResult) {
                GlideUtils.loadUrlHead(
                    this@UserInfoActivity,
                    BaseConstant.ossPicUrl + objectName,
                    civIcon
                )
                Log.w("OSSObjectName", objectName)
                Log.w("OSSObjectPicUrl", BaseConstant.ossPicUrl + objectName)
            }

            override fun upLoadFailed(
                request: PutObjectRequest,
                clientExcepion: ClientException?,
                serviceException: ServiceException?
            ) {
                // 请求异常
                clientExcepion?.printStackTrace()
                if (serviceException != null) {
                    // 服务异常
                    Log.e("ErrorCode", serviceException.errorCode)
                    Log.e("RequestId", serviceException.requestId)
                    Log.e("HostId", serviceException.hostId)
                    Log.e("RawMessage", serviceException.rawMessage)
                }
            }

            override fun upLoadProgress(currentSize: Long, totalSize: Long) {

            }
        })
    }
}