package com.kapok.apps.maple.xdt.home.fragment

import android.os.Bundle
import android.support.v4.app.FragmentManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.kapok.apps.maple.xdt.R
import com.kapok.apps.maple.xdt.classlist.fragment.ClassDetailParentFragment
import com.kapok.apps.maple.xdt.classlist.fragment.ClassDetailTeacherFragment
import com.kapok.apps.maple.xdt.classlist.fragment.ClassListParentFragment
import com.kapok.apps.maple.xdt.classlist.fragment.ClassListTeacherFragment
import com.kotlin.baselibrary.fragment.BaseFragment
import com.kotlin.baselibrary.utils.AppPrefsUtils

/**
 * 班级列表Fragment 用于切换家长和老师不同的Fragment
 */
class ClassFragment : BaseFragment() {
    private var identity: Int = 0
    // 班级列表 家长/老师端口
    private var classListFragmentParent: ClassListParentFragment? = null
    private var classListFragmentTeacher: ClassListTeacherFragment? = null

    // 班级详情 家长/老师端口
    private var classDetailParentFragment: ClassDetailParentFragment? = null
    private var classDetailTeacherFragment: ClassDetailTeacherFragment? = null

    companion object {
        fun instance(identity: Int): ClassFragment {
            val myFragment = ClassFragment()
            val bundle = Bundle()
            bundle.putInt("identity", identity)
            myFragment.arguments = bundle
            return myFragment
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_class_list, null, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView()
    }

    private fun initView() {
        if (arguments != null) {
            identity = arguments!!.getInt("identity")
        }
        val beginTransaction = activity!!.supportFragmentManager.beginTransaction()
        // 1家长  2老师
        if (identity == 1) {
            // 没有缓存 学生 和 班级Id 进入列表页
            if (AppPrefsUtils.getInt("ParentClassId") == -1 && AppPrefsUtils.getInt("ParentStudentId") == -1) {
                classListFragmentParent = ClassListParentFragment()
                beginTransaction.add(R.id.flClassListFragment, classListFragmentParent!!).commit()
            } else {
                classDetailParentFragment = ClassDetailParentFragment.instance(
                    AppPrefsUtils.getInt("ParentClassId"),
                    AppPrefsUtils.getInt("ParentStudentId")
                )
                beginTransaction.add(R.id.flClassListFragment, classDetailParentFragment!!).commit()
            }
        } else {
            if (AppPrefsUtils.getInt("TeacherClassId") == -1) {
                classListFragmentTeacher = ClassListTeacherFragment()
                beginTransaction.add(R.id.flClassListFragment, classListFragmentTeacher!!).commit()
            } else {
                classDetailTeacherFragment =
                    ClassDetailTeacherFragment.instance(AppPrefsUtils.getInt("TeacherClassId"))
                beginTransaction.add(R.id.flClassListFragment, classDetailTeacherFragment!!)
                    .commit()
            }
        }
    }

    fun switchClassListFragment(identity: Int, supportFragmentManager: FragmentManager) {
        val beginTransaction = supportFragmentManager.beginTransaction()
        // 1 家长 2老师
        when (identity) {
            1 -> {
                if (classListFragmentParent == null) {
                    classListFragmentParent = ClassListParentFragment()
                }
                beginTransaction.replace(R.id.flClassListFragment, classListFragmentParent!!)
            }
            2 -> {
                if (classListFragmentTeacher == null) {
                    classListFragmentTeacher = ClassListTeacherFragment()
                }
                beginTransaction.replace(R.id.flClassListFragment, classListFragmentTeacher!!)
            }
        }
        beginTransaction.commitAllowingStateLoss()
    }

    fun jumpParentClassDetailFragment(
        classId: Int,
        studentId: Int,
        supportFragmentManager: FragmentManager
    ) {
        val beginTransaction = supportFragmentManager.beginTransaction()
        // 1 家长 2老师
        classDetailParentFragment = null
        classDetailParentFragment = ClassDetailParentFragment.instance(classId, studentId)
        beginTransaction.replace(R.id.flClassListFragment, classDetailParentFragment!!)
        beginTransaction.commitAllowingStateLoss()
    }

    fun jumpTeacherClassDetailFragment(
        classId: Int,
        supportFragmentManager: FragmentManager
    ) {
        val beginTransaction = supportFragmentManager.beginTransaction()
        // 1 家长 2老师
        classDetailTeacherFragment = null
        classDetailTeacherFragment = ClassDetailTeacherFragment.instance(classId)
        beginTransaction.replace(R.id.flClassListFragment, classDetailTeacherFragment!!)
        beginTransaction.commitAllowingStateLoss()
    }
}