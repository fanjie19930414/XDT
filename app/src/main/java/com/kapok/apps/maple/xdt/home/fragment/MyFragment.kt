package com.kapok.apps.maple.xdt.home.fragment

import android.os.Bundle
import android.support.v4.app.FragmentManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.kapok.apps.maple.xdt.R
import com.kapok.apps.maple.xdt.home.fragment.fragment_parent.MyFragmentParent
import com.kapok.apps.maple.xdt.home.fragment.fragment_teacher.MyFragmentTeacher
import com.kotlin.baselibrary.fragment.BaseFragment

/**
 * 我的Fragment 用于切换家长和老师不同的Fragment
 */
class MyFragment : BaseFragment() {
    private var identity: Int = 0
    private var myFragmentParent: MyFragmentParent? = null
    private var myFragmentTeacher: MyFragmentTeacher? = null

    companion object {
        fun instance(identity: Int): MyFragment {
            val myFragment = MyFragment()
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
        return inflater.inflate(R.layout.fragment_my, null, false)
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
        myFragmentParent = MyFragmentParent()
        myFragmentTeacher = MyFragmentTeacher()
        // 1 家长 2老师
        if (identity == 1) {
            beginTransaction.add(R.id.flMyFragment, myFragmentParent!!).commit()
        } else {
            beginTransaction.add(R.id.flMyFragment, myFragmentTeacher!!).commit()
        }
    }

    fun switchFragment(identity: Int, supportFragmentManager: FragmentManager) {
        val beginTransaction = supportFragmentManager.beginTransaction()
        // 1 家长 2老师
        when (identity) {
            1 -> {
                if (myFragmentParent == null) {
                    myFragmentParent = MyFragmentParent()
                }
                beginTransaction.replace(R.id.flMyFragment, myFragmentParent!!)
            }
            2 -> {
                if (myFragmentTeacher == null) {
                    myFragmentTeacher = MyFragmentTeacher()
                }
                beginTransaction.replace(R.id.flMyFragment, myFragmentTeacher!!)
            }
        }
        beginTransaction.commitAllowingStateLoss()
    }
}