package com.kapok.apps.maple.xdt.timetable.adapter

import android.content.Context
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.RelativeLayout
import android.widget.TextView
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder
import com.kapok.apps.maple.xdt.R
import com.kapok.apps.maple.xdt.timetable.bean.timetablechoosesubjectbean.ClassChooseSubjectBean
import com.kotlin.baselibrary.ex.setVisible
import com.kotlin.baselibrary.utils.Dp2pxUtils

/**
 * 课程表科目Adapter
 * fanjie
 */
class TimeTableWeekendAdapter(
    private val context: Context,
    private val dataList: MutableList<Int>
) : BaseQuickAdapter<Int, BaseViewHolder>(R.layout.item_timetable_weekend, dataList) {
    // 判断当前是否为选中状态
    private var selectIndex: Int = -1
    // 当前周
    private var currentWeekend: Int = -1

    fun setCurrentWeekend(week: Int) {
        this.currentWeekend = week
        for (index in dataList.indices) {
            if (dataList[index] == currentWeekend) {
                selectIndex = index
            }
        }
    }

    // 选中的课程接口回调
    private lateinit var selectTimeTableWeekendInterface: SelectTimeTableWeekendInterface

    interface SelectTimeTableWeekendInterface {
        fun onSelectData(currentWeekend: Int)
    }

    fun setSelectLessonTimeListener(selectInterface: SelectTimeTableWeekendInterface) {
        this.selectTimeTableWeekendInterface = selectInterface
    }

    override fun convert(helper: BaseViewHolder, item: Int) {
        if (selectIndex == helper.layoutPosition) {
            helper.getView<TextView>(R.id.tvWeekend).setTextColor(context.resources.getColor(R.color.xdt_yellow))
            helper.getView<TextView>(R.id.tvWeekend).textSize = Dp2pxUtils.dp2px(context,10).toFloat()
        } else {
            helper.getView<TextView>(R.id.tvWeekend).setTextColor(context.resources.getColor(R.color.common_white))
            helper.getView<TextView>(R.id.tvWeekend).textSize = Dp2pxUtils.dp2px(context,8).toFloat()
        }
        helper.getView<TextView>(R.id.tvWeekend).text = item.toString()

        // 点击切换周
        helper.getView<RelativeLayout>(R.id.rlWeekend).setOnClickListener {
            selectIndex = helper.layoutPosition
            selectTimeTableWeekendInterface.onSelectData(item)
            notifyDataSetChanged()
        }
    }
}