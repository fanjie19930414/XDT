package com.kapok.apps.maple.xdt.timetable.adapter

import android.content.Context
import android.widget.RelativeLayout
import android.widget.TextView
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder
import com.kapok.apps.maple.xdt.R
import com.kotlin.baselibrary.custom.CustomLessonNumDialog
import com.kotlin.baselibrary.utils.DateUtils

/**
 * 课程选择ADAPTER(下午 和上午Adapter一样 主要是防止以后产品会  区分上下午时间 灵活性拓展)
 * fanjie
 */
class LessonsSettingNoonAdapter(val context: Context, dataList: ArrayList<String>) :
    BaseQuickAdapter<String, BaseViewHolder>(R.layout.item_lessons, dataList) {

    private lateinit var selectLessonTimeInterface: SelectLessonTimeInterface
    private lateinit var customTimeDialog: CustomLessonNumDialog
    // 存储课程集合
    private var lessonsMap: MutableMap<String, String> = mutableMapOf()

    interface SelectLessonTimeInterface {
        fun onShowSelectData(lessonMap: MutableMap<String, String>)
    }

    fun setSelectLessonTimeListener(selectInterface: SelectLessonTimeInterface) {
        this.selectLessonTimeInterface = selectInterface
    }

    fun setLessonsMap(map: MutableMap<String, String>) {
        if (map.isNotEmpty()) {
            lessonsMap.clear()
            lessonsMap.putAll(map)
        }
    }

    override fun convert(helper: BaseViewHolder, item: String) {
        helper.getView<TextView>(R.id.tv_lessons).text = item
        if (lessonsMap[item] != null) {
            val temp = lessonsMap[item]?.split("~")
            val temph = temp?.get(0)
            val tempm = temp?.get(1)

            val tempH = DateUtils.formatTurnSecond(temph!!)
            val tempM = (tempm?.split("分钟")?.get(0)!!.toInt() * 60).toLong()
            val result = DateUtils.changeToTime((tempH + tempM).toInt())
            // s = 23:55
            helper.getView<TextView>(R.id.tv_select_lessons_time).text = "$temph ~ $result"
        } else {
            helper.getView<TextView>(R.id.tv_select_lessons_time).text = ""
        }
        // 选择课程弹窗
        helper.getView<RelativeLayout>(R.id.rl_select_lessons).setOnClickListener {
            customTimeDialog = CustomLessonNumDialog(context, R.style.BottomDialog)
            customTimeDialog.setTitle("课程开始时间和时长")
            customTimeDialog.show()
            customTimeDialog.setOnselectTimeListener(object : CustomLessonNumDialog.SelectTimeListener {
                override fun selectTime(hour: String, minute: String) {
                    lessonsMap[item] = "$hour~$minute"
                    selectLessonTimeInterface.onShowSelectData(lessonsMap)

                    val tempH = DateUtils.formatTurnSecond(hour)
                    val tempM = (minute.split("分钟")[0].toInt() * 60).toLong()
                    val result = DateUtils.changeToTime((tempH + tempM).toInt())
                    // s = 23:55
                    helper.getView<TextView>(R.id.tv_select_lessons_time).text = "$hour ~ $result"
                }
            })
        }
    }
}