package com.kotlin.baselibrary.fragment

import com.gyf.immersionbar.ImmersionBar
import com.gyf.immersionbar.components.ImmersionFragment

/**
 * @desciption: Fragment基类，业务无关
 */
open class BaseFragment : ImmersionFragment() {
    override fun initImmersionBar() {
        ImmersionBar.with(this)
            .statusBarDarkFont(true)   //状态栏字体是深色，不写默认为亮色
            .navigationBarDarkIcon(true) //导航栏图标是深色，不写默认为亮色
            .init()
    }
}