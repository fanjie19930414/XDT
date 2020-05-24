package com.kapok.apps.maple.xdt.classlist.activity

import android.Manifest
import android.annotation.SuppressLint
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
import android.widget.ProgressBar
import android.widget.TextView
import com.bumptech.glide.Glide
import com.kapok.apps.maple.xdt.R
import com.kapok.apps.maple.xdt.classlist.bean.ClassInfoBean
import com.kapok.apps.maple.xdt.classlist.presenter.ClassInfoTeacherPresenter
import com.kapok.apps.maple.xdt.classlist.presenter.view.ClassInfoTeacherView
import com.kapok.apps.maple.xdt.utils.OssService
import com.kapok.apps.maple.xdt.utils.OssUIDisplayer
import com.kapok.apps.maple.xdt.utils.PermissionUtils
import com.kapok.apps.maple.xdt.utils.UploadPicUtils
import com.kotlin.baselibrary.activity.BaseMVPActivity
import com.kotlin.baselibrary.commen.BaseApplication
import com.kotlin.baselibrary.commen.BaseConstant
import com.kotlin.baselibrary.custom.CancelConfirmDialog
import com.kotlin.baselibrary.custom.CustomCancelBottomDialog
import com.kotlin.baselibrary.utils.AppPrefsUtils
import com.kotlin.baselibrary.utils.DateUtils
import com.kotlin.baselibrary.utils.GlideUtils
import com.kotlin.baselibrary.utils.ToastUtils
import com.luck.picture.lib.PictureSelector
import com.luck.picture.lib.config.PictureConfig
import com.luck.picture.lib.config.PictureMimeType
import kotlinx.android.synthetic.main.activity_user_info.*
import kotlinx.android.synthetic.main.class_info_activity.*
import java.io.File
import java.util.*

/**
 * 班级资料教师端
 */
@SuppressLint("SetTextI18n")
class ClassInfoTeacherActivity : BaseMVPActivity<ClassInfoTeacherPresenter>(),
    ClassInfoTeacherView {
    private var classId: Int = -1
    private var searchType: Int = 2
    private var userId: Int = -1
    // 判断是否是班主任
    private var isHeaderTeacher: Boolean = false
    // 三个点弹窗
    private lateinit var classInfoSettingDialog: CustomCancelBottomDialog
    private val updateClassName = 200

    // 底部更换头像弹窗
    private lateinit var iconBottomDialog: CustomCancelBottomDialog
    // 拍照保存路径
    private lateinit var photoPath: String
    // 相册压缩后返回路径
    private lateinit var albumPath: String
    // 目前用的上传方式
    private lateinit var ossService: OssService
    // OSS上传进度UI
    private lateinit var ossUIDisplayer: OssUIDisplayer
    // 上传成功后返回的图片地址
    private var upLoadPicUrl: String = ""
    private var objectName: String = ""
    // 班级名称
    private var newClassName: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.class_info_activity)
        iniView()
        initListener()
    }

    private fun iniView() {
        mPresenter = ClassInfoTeacherPresenter(this)
        mPresenter.mView = this
        tvClassInfoTitleTeacher.text = "班级资料"
        // 获取传入的classId searchType
        val intent = intent
        classId = intent.getIntExtra("classId", -1)
        searchType = intent.getIntExtra("searchType", 2)
        userId = AppPrefsUtils.getInt("userId")
        // OSS UI
        ossUIDisplayer = OssUIDisplayer(ImageView(this), ProgressBar(this), TextView(this),this)
    }

    private fun initListener() {
        // 返回
        ivClassInfoBackTeacher.setOnClickListener { finish() }
        // 三个点
        ivClassInfoSetting.setOnClickListener {
            // 判断是否是班主任
            if (isHeaderTeacher) {
                // dialog
                classInfoSettingDialog =
                    CustomCancelBottomDialog(this@ClassInfoTeacherActivity, R.style.BottomDialog)
                classInfoSettingDialog.addItem(
                    "修改班级头像",
                    R.color.text_xdt,
                    View.OnClickListener {
                        // 修改头像
                        iconBottomDialog =
                            CustomCancelBottomDialog(this, R.style.BottomDialog)
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
                                PictureSelector.create(this)
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
                        classInfoSettingDialog.dismiss()
                    })
                classInfoSettingDialog.addItem(
                    "修改班级名称",
                    R.color.text_xdt,
                    View.OnClickListener {
                        // 修改班级名称
                        val intent = Intent(this, ClassNameActivity::class.java)
                        startActivityForResult(intent, updateClassName)
                        classInfoSettingDialog.dismiss()
                    })
                classInfoSettingDialog.addItem(
                    "修改我在本班的教学课程",
                    R.color.text_xdt,
                    View.OnClickListener {
                        // 修改本班教学课程
                        val intent = Intent(this, ClassSelectSubjectActivity::class.java)
                        intent.putExtra("classId", classId)
                        startActivity(intent)
                        classInfoSettingDialog.dismiss()
                    })
                classInfoSettingDialog.show()
            } else {
                // dialog
                classInfoSettingDialog =
                    CustomCancelBottomDialog(this@ClassInfoTeacherActivity, R.style.BottomDialog)
                classInfoSettingDialog.addItem(
                    "修改我在本班的教学课程",
                    R.color.text_xdt,
                    View.OnClickListener {
                        // 修改本班教学课程
                        val intent = Intent(this, ClassSelectSubjectActivity::class.java)
                        intent.putExtra("classId", classId)
                        startActivity(intent)
                        classInfoSettingDialog.dismiss()
                    })
                classInfoSettingDialog.addItem(
                    "退出此班级",
                    R.color.xdt_exit_text,
                    View.OnClickListener {
                        // 退出此班级
                        val confirmDialog = CancelConfirmDialog(
                            this@ClassInfoTeacherActivity, R.style.BottomDialog
                            , "请确认是否退出此班级", ""
                        )
                        confirmDialog.show()
                        confirmDialog.setOnClickConfirmListener(object :
                            CancelConfirmDialog.ClickConfirmListener {
                            override fun confirm() {
                                // 调用退出班级接口
                                mPresenter.exitClass(
                                    classId,
                                    AppPrefsUtils.getInt("userId"),
                                    searchType,
                                    AppPrefsUtils.getInt("userId")
                                )
                                confirmDialog.dismiss()
                            }
                        })
                        classInfoSettingDialog.dismiss()
                    })
                classInfoSettingDialog.show()
            }
        }
        // 上传班级头像监听
        ossUIDisplayer.setOnUpLoadResultListener(object : OssUIDisplayer.UpLoadResult{
            override fun upLoadOK() {
                upLoadPicUrl = BaseConstant.ossPicUrl + objectName
                mPresenter.updateClassInfo(
                    upLoadPicUrl,
                    classId,
                    newClassName,
                    AppPrefsUtils.getInt("userId")
                )
            }

            override fun upLoadFAIL(info: String?) {
                mPresenter.mView.onDismissDialog()
                info?.let { ToastUtils.showMsg(this@ClassInfoTeacherActivity, it) }
            }
        })
    }

    override fun onResume() {
        super.onResume()
        mPresenter.classUpdate(classId, searchType, userId)
    }

    // 班级资料回调
    override fun getClassInfoTeacherBean(bean: ClassInfoBean) {
        // 头像
        if (bean.avatar != null && bean.avatar.isNotEmpty()) {
            GlideUtils.loadImage(this, bean.avatar, ivClassInfoTeacher)
        } else {
            Glide.with(this).load(R.mipmap.def_head_class).into(ivClassInfoTeacher)
        }
        // 班级名称
        newClassName = bean.className
        tvClassInfoName.text = bean.className
        // 班级年级
        tvClassInfoGrade.text = bean.grade
        // 入学年份
        tvClassInfoYear.text = bean.startYear.toString() + "年"
        // 所属学校
        tvClassInfoSchool.text = bean.schoolName
        // 班主任
        tvClassInfoHeaderTeacher.text = bean.headerTeacher
        // 任教课程
        if (bean.searchSubjectOutputVOS != null && bean.searchSubjectOutputVOS.isNotEmpty()) {
            var subject = ""
            for (item in bean.searchSubjectOutputVOS) {
                subject += item.subjectName + ","
            }
            tvClassInfoSubject.text = subject.substring(0, subject.length - 1)
        }
        // 是否是班主任
        isHeaderTeacher = AppPrefsUtils.getInt("userId") == bean.headerTeacherId
    }

    // 退出班级回调
    override fun exitClass(msg: String) {
        ToastUtils.showMsg(this, msg)
        setResult(Activity.RESULT_OK)
        finish()
    }

    // 编辑班级资料接口（班主任）
    override fun updateClassDetail(msg: String) {
        ToastUtils.showMsg(this, msg)
        mPresenter.classUpdate(classId, searchType, userId)
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
                    ToastUtils.showMsg(this@ClassInfoTeacherActivity, "无法获取拍照权限")
                }

                override fun checkSuccess() {
                    photoPath = BaseApplication.getImageFolderPath() + DateFormat.format("yyyy-MM-dd-hh-mm-ss", Date()) + ".jpg"
                    val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
                    intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(File(photoPath)))
                    startActivityForResult(intent, BaseConstant.TAKE_A_PHOTO)
                }
            })
    }

    /**
     * 裁剪图片
     */
    private fun cropImageUri(uri: Uri, outUri: Uri, requestCode: Int) {
        val intent = Intent("com.android.camera.action.CROP")
        intent.setDataAndType(uri, "image/*")
        intent.putExtra("crop", "true")
        intent.putExtra("aspectX", 8)
        intent.putExtra("aspectY", 5)
        intent.putExtra("outputX", 800)
        intent.putExtra("outputY", 500)
        intent.putExtra("scale", true)
        intent.putExtra(MediaStore.EXTRA_OUTPUT, outUri)
        intent.putExtra("return-data", false)
        intent.putExtra("outputFormat", Bitmap.CompressFormat.JPEG)
        intent.putExtra("noFaceDetection", true)
        startActivityForResult(intent, requestCode)
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            when (requestCode) {
                // 拍照后回调
                BaseConstant.TAKE_A_PHOTO -> {
                    val uri = Uri.fromFile(File(photoPath))
                    cropImageUri(uri, uri, BaseConstant.CROP_PICTURE)
                }
                // 相册选择回调
                PictureConfig.CHOOSE_REQUEST -> {
                    // 例如 LocalMedia 里面返回三种path
                    // 1.media.getPath(); 为原图path
                    // 2.media.getCutPath();为裁剪后path，需判断media.isCut();是否为true
                    // 3.media.getCompressPath();为压缩后path，需判断media.isCompressed();是否为true
                    // 如果裁剪并压缩了，以取压缩路径为准，因为是先裁剪后压缩的
                    val images = PictureSelector.obtainMultipleResult(data)
                    val resultUri = Uri.fromFile(File(images[0].path))
                    albumPath = BaseApplication.getImageFolderPath() + DateFormat.format("yyyy-MM-dd-hh-mm-ss", Date()) + ".jpg"
                    val outAlbumUri = Uri.fromFile(File(albumPath))
                    cropImageUri(resultUri, outAlbumUri, BaseConstant.ALBUM_PICTURE)
                }
                // 拍照后裁剪之后的图片
                BaseConstant.CROP_PICTURE -> {
                    // 测试上传图片至OSS
                    ossService = UploadPicUtils.initOSS(applicationContext,BaseConstant.endPoint,BaseConstant.bucketName,ossUIDisplayer)
                    objectName = DateUtils.getOSSObjectName()
                    ossService.asyncPutImage(objectName,photoPath)
                    mPresenter.mView.onShowDialog()
                    Log.w("OSSObjectName",objectName)
                    Log.w("OSSObjectPicUrl",BaseConstant.ossPicUrl + objectName)
                }
                // 相册裁剪后的图片
                BaseConstant.ALBUM_PICTURE -> {
                    // 测试上传图片至OSS
                    ossService = UploadPicUtils.initOSS(applicationContext,BaseConstant.endPoint,BaseConstant.bucketName,ossUIDisplayer)
                    objectName = DateUtils.getOSSObjectName()
                    ossService.asyncPutImage(objectName,albumPath)
                    mPresenter.mView.onShowDialog()
                    Log.w("OSSObjectName",objectName)
                    Log.w("OSSObjectPicUrl",BaseConstant.ossPicUrl + objectName)
                }
                updateClassName -> {
                    newClassName = data!!.getStringExtra("className")
                    mPresenter.updateClassInfo(
                        upLoadPicUrl,
                        classId,
                        newClassName,
                        AppPrefsUtils.getInt("userId")
                    )
                }
            }
        }
    }
}