package com.kapok.apps.maple.xdt.classlist.activity

import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import com.chad.library.adapter.base.BaseQuickAdapter
import com.kapok.apps.maple.xdt.R
import com.kapok.apps.maple.xdt.classlist.adapter.ClassSelectSubjectAdapter
import com.kapok.apps.maple.xdt.classlist.bean.SubjectByTeacherBean
import com.kapok.apps.maple.xdt.classlist.presenter.ClassSelectSubjectPresenter
import com.kapok.apps.maple.xdt.classlist.presenter.view.ClassSelectSubjectView
import com.kapok.apps.maple.xdt.timetable.bean.timetablechoosesubjectbean.ClassChooseSubjectBean
import com.kotlin.baselibrary.activity.BaseMVPActivity
import com.kotlin.baselibrary.commen.BaseApplication.Companion.context
import com.kotlin.baselibrary.ex.setVisible
import com.kotlin.baselibrary.utils.AppPrefsUtils
import com.kotlin.baselibrary.utils.ToastUtils
import kotlinx.android.synthetic.main.activity_class_select_subject.*
import kotlinx.android.synthetic.main.fragment_class_list_parent.*

/**
 * 班级列表选择课程页面
 * fanjie
 */
class ClassSelectSubjectActivity : BaseMVPActivity<ClassSelectSubjectPresenter>(), ClassSelectSubjectView {
    // 课程列表集合
    private lateinit var subjectListBean: MutableList<ClassChooseSubjectBean>
    private lateinit var subjectAdapter: ClassSelectSubjectAdapter
    // 班级Id
    private var classId: Int = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_class_select_subject)
        initView()
        initListener()
    }

    private fun initListener() {
        // 保存
        headerBarClassSelectSubject.getRightView().setOnClickListener {
            val selectSubjectBean = mutableListOf<SubjectByTeacherBean>()
            for (item in subjectListBean) {
                if (item.isChoose) {
                    selectSubjectBean.add(SubjectByTeacherBean(item.subjectId, item.subjectName))
                }
            }
            mPresenter.editSubjectByTeacher(classId, selectSubjectBean, AppPrefsUtils.getInt("userId"))
        }
        // Adapter
        subjectAdapter.onItemChildClickListener =
            BaseQuickAdapter.OnItemChildClickListener { _, view, position ->
                when (view.id) {
                    R.id.rlClassListSubject -> {
                        subjectListBean[position].isChoose = !subjectListBean[position].isChoose
                        subjectAdapter.notifyDataSetChanged()
                    }
                }
            }
    }

    private fun initView() {
        mPresenter = ClassSelectSubjectPresenter(this)
        mPresenter.mView = this
        // 获取传入的classId
        classId = intent.getIntExtra("classId", -1)
        // 添加
        headerBarClassSelectSubject.getRightView().setTextColor(resources.getColor(R.color.login_xdt_btn_color_able))
        headerBarClassSelectSubject.getRightView().text = "保存"
        headerBarClassSelectSubject.getRightView().setVisible(true)
        // 配置Rv
        subjectListBean = arrayListOf()
        subjectAdapter = ClassSelectSubjectAdapter(this, subjectListBean)
        rvClassSelectSubject.layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
        rvClassSelectSubject.adapter = subjectAdapter
        // emptyView
        val emptyView =
            LayoutInflater.from(context).inflate(R.layout.layout_class_list_empty2, rvClassListParent, false)
        subjectAdapter.emptyView = emptyView
        // 调用接口
        mPresenter.getClassSubjectList(classId)
    }

    // 获取班级对应课程回调
    override fun getClassSubjectList(list: MutableList<ClassChooseSubjectBean>?) {
        if (list != null && list.size > 0) {
            subjectListBean.addAll(list)
            subjectAdapter.notifyDataSetChanged()
        }
    }

    // 修改课程 保存回调
    override fun editSubjectByTeacher(msg: String) {
        ToastUtils.showMsg(this, msg)
    }
}