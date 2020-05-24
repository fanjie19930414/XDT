package com.kapok.apps.maple.xdt.home.activity

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.text.Editable
import android.view.View
import com.kapok.apps.maple.xdt.R
import com.kapok.apps.maple.xdt.home.presenter.AddChildPresenter
import com.kapok.apps.maple.xdt.home.presenter.view.AddChildView
import com.kapok.apps.maple.xdt.usercenter.bean.RelationListBean
import com.kapok.apps.maple.xdt.usercenter.bean.SaveStudentIdBean
import com.kapok.apps.maple.xdt.utils.PermissionUtils
import com.kotlin.baselibrary.activity.BaseMVPActivity
import com.kotlin.baselibrary.commen.AppManager
import com.kotlin.baselibrary.commen.BaseApplication
import com.kotlin.baselibrary.commen.BaseConstant
import com.kotlin.baselibrary.custom.*
import com.kotlin.baselibrary.utils.AppPrefsUtils
import com.kotlin.baselibrary.utils.DateUtils
import com.kotlin.baselibrary.utils.GlideUtils
import com.kotlin.baselibrary.utils.ToastUtils
import com.luck.picture.lib.PictureSelector
import com.luck.picture.lib.config.PictureConfig
import com.luck.picture.lib.config.PictureMimeType
import kotlinx.android.synthetic.main.activity_add_child.*
import java.io.File

/**
 *  我的-添加孩子页
 *  fanjie
 */
class AddChildActivity : BaseMVPActivity<AddChildPresenter>(), AddChildView {
    // 家长和孩子的关系
    private var relationList: MutableList<RelationListBean> = arrayListOf()
    // 家长和孩子关系的id
    private var relationId: Int = -1
    // 底部更换头像弹窗
    private lateinit var iconBottomDialog: CustomCancelBottomDialog
    // 底部性别弹窗
    private lateinit var bottomChildSexDialog: CustomBottomChildSexDialog
    // 底部关系弹窗
    private lateinit var bottomDialog: CustomBottomDialog
    // 生日底部弹窗
    private var hasBirthday: Boolean = false
    private lateinit var birthDay: String
    private lateinit var birthdayDataDialog: CustomDataDialog
    private lateinit var mSelectYear: String
    private lateinit var mSelectMonth: String
    private lateinit var mSelectDay: String
    // 拍照保存路径
    private lateinit var photoPath: String
    // 相册压缩后返回路径
    private lateinit var albumPath: String
    // 3个必选项
    private var hasChildName: Boolean = false
    private var hasChildSex: Boolean = false
    private var hasParentRelation = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_child)
        initView()
        initListener()
    }

    private fun initView() {
        mPresenter = AddChildPresenter(this@AddChildActivity)
        mPresenter.mView = this
        // 调用获取关系列表
        mPresenter.getRelationList()
    }

    private fun initListener() {
        // 头像
        rlAddChildIcon.setOnClickListener {
            iconBottomDialog =
                CustomCancelBottomDialog(this@AddChildActivity, R.style.BottomDialog)
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
                    PictureSelector.create(this@AddChildActivity)
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
        // 孩子姓名监听
        etAddChild.addTextChangedListener(object : DefaultTextWatcher() {
            override fun afterTextChanged(s: Editable?) {
                hasChildName = s.toString().isNotEmpty()
                btAddChild.isEnabled = hasChildName && hasChildSex && hasParentRelation
            }
        })
        // 性别
        rlAddChildSex.setOnClickListener {
            bottomChildSexDialog = CustomBottomChildSexDialog(this@AddChildActivity, R.style.BottomDialog)
            bottomChildSexDialog.show()
            bottomChildSexDialog.setIsBoyorGirl(
                object : CustomBottomChildSexDialog.IsBoyorGirl {
                    override fun chooseBoy(boolean: Boolean) {
                        if (boolean) {
                            tvAddChildSex.text = "男孩"
                        } else {
                            tvAddChildSex.text = "女孩"
                        }
                        hasChildSex = true
                        btAddChild.isEnabled = hasChildName && hasChildSex && hasParentRelation
                    }
                }
            )
        }
        // 生日
        rlAddChildBirthday.setOnClickListener {
            birthdayDataDialog = CustomDataDialog(this@AddChildActivity, R.style.BottomDialog)
            birthdayDataDialog.setTitle("请选择孩子的生日")
            if (hasBirthday) {
                birthdayDataDialog.setSelectedTime(mSelectYear, mSelectMonth, mSelectDay)
            } else {
                birthdayDataDialog.getCurrentTime()
            }
            birthdayDataDialog.show()
            birthdayDataDialog.setOnselectDataListener(object : CustomDataDialog.SelectDataListener {
                override fun selectData(year: String, month: String, day: String) {
                    hasBirthday = true
                    mSelectYear = year
                    mSelectMonth = (if (Integer.valueOf(month) > 9) month else "0$month").toString()
                    mSelectDay = (if (Integer.valueOf(day) > 9) day else "0$day").toString()
                    val birthdayData = "$mSelectYear-$mSelectMonth-$mSelectDay"
                    birthDay = birthdayData + "   " + DateUtils.getConstellation(birthdayData)
                    tvAddChildBirthday.text = birthDay
                }
            })
        }
        // 关系
        rlAddChildRelation.setOnClickListener {
            val relationListString = arrayListOf<String>()
            bottomDialog = CustomBottomDialog(this@AddChildActivity, R.style.BottomDialog)
            bottomDialog.setTitle("选择关系")
            if (hasParentRelation) {
                for (item in relationList) {
                    relationListString.add(item.relationName)
                }
                bottomDialog.addItem(relationListString, tvAddChildRelation.text.toString())
            } else {
                for (item in relationList) {
                    relationListString.add(item.relationName)
                }
                bottomDialog.addItem(relationListString, "")
            }
            bottomDialog.show()
            bottomDialog.setOnselectTextListener(object : CustomBottomDialog.SelectTextListener {
                override fun selectText(text: String) {
                    tvAddChildRelation.text = text
                    for (item in relationList) {
                        if (text == item.relationName) {
                            // 记录选中的关系Id
                            relationId = item.relationId
                        }
                    }
                    hasParentRelation = true
                    btAddChild.isEnabled = hasChildName && hasChildSex && hasParentRelation
                }
            })
        }
        // 提交
        btAddChild.setOnClickListener {
            val userId = AppPrefsUtils.getInt("userId")
            val sexText = if (tvAddChildSex.text == "男孩") {
                "male"
            } else {
                "famale"
            }
            mPresenter.saveParentInfo(AppPrefsUtils.getString("userName"), sexText, userId)
        }
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
                    ToastUtils.showMsg(this@AddChildActivity, "无法获取拍照权限")
                }

                override fun checkSuccess() {
                    photoPath = BaseApplication.getImageFolderPath() + System.currentTimeMillis() + ".jpg"
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
                    cropImageUri(resultUri, resultUri, BaseConstant.ALBUM_PICTURE, true)
                }
                // 拍照后裁剪之后的图片
                BaseConstant.CROP_PICTURE -> {
                    GlideUtils.loadImage(this, photoPath, civAddChildIcon)
                }
                // 相册裁剪后的图片
                BaseConstant.ALBUM_PICTURE -> {
                    GlideUtils.loadImage(this, albumPath, civAddChildIcon)
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

    // 获取家属关系列表回调
    override fun getRelationList(dataList: MutableList<RelationListBean>?) {
        relationList.clear()
        if (dataList != null) {
            if (dataList.isNotEmpty()) {
                relationList.addAll(dataList)
            }
        }
    }

    override fun saveChildSuccessful(studentId: SaveStudentIdBean) {
        ToastUtils.showMsg(this@AddChildActivity,"创建成功")
        setResult(Activity.RESULT_OK)
        AppManager.instance.finishActivity(this)
    }

    override fun saveParentSuccessful(boolean: Boolean) {
        // 先调用家长信息接口 再调用孩子信息接口
        if (boolean) {
            val sexText = if (tvAddChildSex.text == "男孩") {
                "male"
            } else {
                "famale"
            }
            val userId = AppPrefsUtils.getInt("userId")
            mPresenter.saveChildInfo(
                tvAddChildRelation.text.toString(),
                etAddChild.text.toString(),
                sexText,
                userId
            )
        }
    }
}