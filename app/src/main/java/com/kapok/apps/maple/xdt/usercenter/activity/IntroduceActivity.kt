package com.kapok.apps.maple.xdt.usercenter.activity

import android.content.Intent
import android.os.Bundle
import com.kapok.apps.maple.xdt.R
import com.kapok.apps.maple.xdt.usercenter.activity.introduce_student.EditInfoActivity
import com.kapok.apps.maple.xdt.usercenter.activity.introduce_teacher.TeacherEditInfoActivity
import com.kotlin.baselibrary.activity.BaseActivity
import com.kotlin.baselibrary.commen.AppManager
import com.kotlin.baselibrary.ex.setVisible
import kotlinx.android.synthetic.main.activity_introduce.*

/**
 *  引导页(返回跳转登陆页)
 *  fanjie
 */
class IntroduceActivity : BaseActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_introduce)
        headerBarIdentity.getLeftView().setVisible(false)
        initListener()
    }

    private fun initListener() {
        // 跳转到 家长引导页
        ll_parent.setOnClickListener {
            startActivity(Intent(this@IntroduceActivity, EditInfoActivity::class.java))
        }
        // 跳转到 老师引导页
        ll_teacher.setOnClickListener {
            startActivity(Intent(this@IntroduceActivity, TeacherEditInfoActivity::class.java))
        }
    }

    override fun onBackPressed() {
        AppManager.instance.finishAllActivity()
    }
}