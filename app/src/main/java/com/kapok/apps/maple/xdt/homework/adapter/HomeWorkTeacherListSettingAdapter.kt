package com.kapok.apps.maple.xdt.homework.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.widget.TextView
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder
import com.kapok.apps.maple.xdt.R
import com.kapok.apps.maple.xdt.homework.bean.HomeWorkListSettingBean

/**
 *  班级列表设置教师端Adapter
 */
class HomeWorkTeacherListSettingAdapter(private val context: Context, dataList: MutableList<HomeWorkListSettingBean>) :
    BaseQuickAdapter<HomeWorkListSettingBean, BaseViewHolder>(R.layout.item_homework_list_setting, dataList) {

    @SuppressLint("SetTextI18n")
    override fun convert(helper: BaseViewHolder, item: HomeWorkListSettingBean) {
        val tvHomeWorkSetting = helper.getView<TextView>(R.id.tvHomeWorkSetting)
        helper.addOnClickListener(R.id.tvHomeWorkSetting)
        tvHomeWorkSetting.text = item.name
        if (item.isChoose) {
            tvHomeWorkSetting.setTextColor(context.resources.getColor(R.color.login_xdt_btn_color_able))
            tvHomeWorkSetting.setBackgroundResource(R.drawable.house_blue_press_btn_corners_blue)
        } else {
            tvHomeWorkSetting.setTextColor(context.resources.getColor(R.color.text_xdt))
            tvHomeWorkSetting.setBackgroundResource(R.drawable.house_blue_press_btn_corners)
        }
    }
}