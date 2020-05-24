package com.kotlin.baselibrary.activity

import android.content.Context
import android.graphics.ColorMatrix
import android.graphics.ColorMatrixColorFilter
import android.graphics.Paint
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.AttributeSet
import android.view.View
import com.gyf.immersionbar.ImmersionBar
import com.kotlin.baselibrary.commen.AppManager

/**
 * @desciption: Activity基类，业务无关
 */
open class BaseActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        ImmersionBar.with(this)
            .statusBarDarkFont(true)   //状态栏字体是深色，不写默认为亮色
            .navigationBarDarkIcon(true) //导航栏图标是深色，不写默认为亮色
            .init()

        AppManager.instance.addActivity(this)
    }

    override fun onCreateView(name: String?, context: Context?, attrs: AttributeSet?): View? {
//        // 设置灰度化 (4月4日黑色效果)
//        val paint = Paint()
//        val cm = ColorMatrix()
//        cm.setSaturation(0.toFloat())
//        paint.colorFilter = ColorMatrixColorFilter(cm)
//        // 硬件加速 paint
//        window.decorView.setLayerType(View.LAYER_TYPE_HARDWARE,paint)
        return super.onCreateView(name, context, attrs)
    }

    override fun onDestroy() {
        super.onDestroy()
        AppManager.instance.finishActivity(this)
    }
}