package com.kotlin.baselibrary.custom

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import com.kotlin.baselibrary.R

/**
 * Description:自定义EmptyView
 */
class CustomEmptyView : LinearLayout {
    private var iv: ImageView? = null
    private var contentTv: TextView? = null
    private var subContentTv: TextView? = null
    private var btnTv: TextView? = null

    constructor(context: Context) : super(context) {
        setView(context)
    }

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {
        setView(context)
    }

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        setView(context)
    }

    private fun setView(context: Context) {
        val view = View.inflate(context, R.layout.view_custom_empty, this)
        iv = view.findViewById(R.id.empty_view_iv) as ImageView
        contentTv = view.findViewById(R.id.empty_view_tv) as TextView
        subContentTv = view.findViewById(R.id.empty_view_sub_tv) as TextView
        btnTv = view.findViewById(R.id.empty_view_btn_tv) as TextView
    }

    fun setContent(content: String): CustomEmptyView {
        contentTv!!.visibility = View.VISIBLE
        contentTv!!.text = content
        return this
    }

    fun setSubContent(content: String): CustomEmptyView {
        subContentTv!!.visibility = View.VISIBLE
        subContentTv!!.text = content
        return this
    }

    @JvmOverloads
    fun setBtn(btnText: String, btnClickListener: View.OnClickListener? = null): CustomEmptyView {
        btnTv!!.visibility = View.VISIBLE
        btnTv!!.text = btnText
        btnTv!!.setOnClickListener(btnClickListener)
        return this
    }

    fun setBtnBackground(resId: Int): CustomEmptyView {
        btnTv!!.setBackgroundResource(resId)
        return this
    }

    fun setImgRes(imgRes: Int): CustomEmptyView {
        iv!!.visibility = View.VISIBLE
        iv!!.setImageResource(imgRes)
        return this
    }

    companion object {

        fun builder(context: Context): CustomEmptyView {
            return CustomEmptyView(context)
        }
    }
}
