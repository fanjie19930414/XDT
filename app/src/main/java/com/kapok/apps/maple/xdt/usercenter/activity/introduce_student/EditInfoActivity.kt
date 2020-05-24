package com.kapok.apps.maple.xdt.usercenter.activity.introduce_student

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import com.kapok.apps.maple.xdt.R
import com.kapok.apps.maple.xdt.usercenter.bean.RelationListBean
import com.kapok.apps.maple.xdt.usercenter.bean.SaveStudentIdBean
import com.kapok.apps.maple.xdt.usercenter.presenter.EditInfoPresenter
import com.kapok.apps.maple.xdt.usercenter.presenter.view.EditInfoView
import com.kotlin.baselibrary.activity.BaseMVPActivity
import com.kotlin.baselibrary.commen.AppManager
import com.kotlin.baselibrary.commen.BaseUserInfo
import com.kotlin.baselibrary.custom.CustomBottomChildSexDialog
import com.kotlin.baselibrary.custom.CustomBottomDialog
import com.kotlin.baselibrary.custom.DefaultTextWatcher
import com.kotlin.baselibrary.utils.AppPrefsUtils
import kotlinx.android.synthetic.main.activity_editinfo.*

/**
 *  完善信息页
 *  fanjie
 */
class EditInfoActivity : BaseMVPActivity<EditInfoPresenter>(), EditInfoView {
    private var hasChildName: Boolean = false
    private var hasChildSex: Boolean = false
    private var hasParentName: Boolean = false
    private var hasParentRelation: Boolean = false
    // 底部弹窗
    private lateinit var bottomChildSexDialog: CustomBottomChildSexDialog
    private lateinit var bottomDialog: CustomBottomDialog
    // 家长和孩子的关系
    private var relationList: MutableList<RelationListBean> = arrayListOf()
    // 家长和孩子关系的id
    private var relationId: Int = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_editinfo)
        initView()
        initListener()
    }

    private fun initView() {
        mPresenter = EditInfoPresenter(this)
        mPresenter.mView = this
        initData()
    }

    private fun initData() {
        // 调用获取家长与孩子关系接口
        mPresenter.getRelationList()
    }

    private fun initListener() {
        // 孩子姓名监听
        et_editinfo_childname.addTextChangedListener(object : DefaultTextWatcher() {
            override fun afterTextChanged(s: Editable?) {
                hasChildName = s.toString().isNotEmpty()
                bt_editinfo.isEnabled = hasChildName && hasChildSex && hasParentName && hasParentRelation
            }
        })
        // 孩子性别弹窗
        rl_editinfo_childsex.setOnClickListener {
            bottomChildSexDialog = CustomBottomChildSexDialog(this@EditInfoActivity, R.style.BottomDialog)
            bottomChildSexDialog.show()
            bottomChildSexDialog.setIsBoyorGirl(
                object : CustomBottomChildSexDialog.IsBoyorGirl {
                    override fun chooseBoy(boolean: Boolean) {
                        if (boolean) {
                            tv_editinfo_sex.text = "男孩"
                        } else {
                            tv_editinfo_sex.text = "女孩"
                        }
                        hasChildSex = true
                        bt_editinfo.isEnabled = hasChildName && hasChildSex && hasParentName && hasParentRelation
                    }
                }
            )
        }
        // 家长姓名监听
        et_editinfo_parentname.addTextChangedListener(object : DefaultTextWatcher() {
            override fun afterTextChanged(s: Editable?) {
                hasParentName = s.toString().isNotEmpty()
                bt_editinfo.isEnabled = hasChildName && hasChildSex && hasParentName && hasParentRelation
            }
        })
        // 家长关系弹窗
        rl_editinfo_yourrelation.setOnClickListener {
            var relationListString = arrayListOf<String>()
            bottomDialog = CustomBottomDialog(this@EditInfoActivity, R.style.BottomDialog)
            bottomDialog.setTitle("选择关系")
            if (hasParentRelation) {
                for (item in relationList) {
                    relationListString.add(item.relationName)
                }
                bottomDialog.addItem(relationListString, tv_editinfo_relation.text.toString())
            } else {
                for (item in relationList) {
                    relationListString.add(item.relationName)
                }
                bottomDialog.addItem(relationListString, "")
            }
            bottomDialog.show()
            bottomDialog.setOnselectTextListener(object : CustomBottomDialog.SelectTextListener {
                override fun selectText(text: String) {
                    tv_editinfo_relation.text = text
                    for (item in relationList) {
                        if (text == item.relationName) {
                            // 记录选中的关系Id
                            relationId = item.relationId
                        }
                    }
                    hasParentRelation = true
                    bt_editinfo.isEnabled = hasChildName && hasChildSex && hasParentName && hasParentRelation
                }
            })
        }
        // 保存信息
        bt_editinfo.setOnClickListener {
            val userId = AppPrefsUtils.getInt("userId")
            val sexText = if (tv_editinfo_sex.text == "男孩") {
                "male"
            } else {
                "famale"
            }
            mPresenter.saveParentInfo(et_editinfo_parentname.text.toString(), sexText, userId)
        }
    }

    override fun saveChildSuccessful(studentId: SaveStudentIdBean) {
        // 家长 申请加入 传code 2
        // 保存身份
        AppPrefsUtils.putString("identity","1")
        BaseUserInfo.identity = 1
        val intent = Intent()
        intent.putExtra("code", 2)
        intent.putExtra("childName", et_editinfo_childname.text.toString())
        intent.putExtra("relation", tv_editinfo_relation.text.toString())
        intent.putExtra("studentId",studentId.userId)
        intent.putExtra("fromType","")
        intent.setClass(this@EditInfoActivity, JoinClassActivity::class.java)
        startActivity(intent)
        AppManager.instance.finishActivity(this)
    }

    override fun saveParentSuccessful(boolean: Boolean) {
        // 先调用家长信息接口 再调用孩子信息接口
        if (boolean) {
            val sexText = if (tv_editinfo_sex.text == "男孩") {
                "male"
            } else {
                "famale"
            }
            val userId = AppPrefsUtils.getInt("userId")
            mPresenter.saveChildInfo(
                tv_editinfo_relation.text.toString(),
                et_editinfo_childname.text.toString(),
                sexText,
                userId
            )
        }
        // 存储家长信息成功之后  本地存储一份家长信息
        AppPrefsUtils.putString("userName", et_editinfo_parentname.text.toString())
        BaseUserInfo.userName = et_editinfo_parentname.text.toString()
    }

    override fun getRelationList(dataList: MutableList<RelationListBean>?) {
        if (dataList != null) {
            if (dataList.isNotEmpty()) {
                relationList.addAll(dataList)
            }
        }
    }
}