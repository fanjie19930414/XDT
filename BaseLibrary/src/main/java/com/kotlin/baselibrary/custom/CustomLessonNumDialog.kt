package com.kotlin.baselibrary.custom

import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.view.Gravity
import com.contrarywind.adapter.WheelAdapter
import com.kotlin.baselibrary.R
import com.kotlin.baselibrary.utils.DateUtils
import kotlinx.android.synthetic.main.dialog_bottom_custom.tv_bottomdialog_cancel
import kotlinx.android.synthetic.main.dialog_bottom_custom.tv_bottomdialog_confirm
import kotlinx.android.synthetic.main.dialog_bottom_custom.tv_bottomdialog_title
import kotlinx.android.synthetic.main.dialog_time_custom.*

/**
 * 底部弹窗工具类(课程时长选择)
 * fanjie
 */
class CustomLessonNumDialog constructor(context: Context, themeResId: Int) : Dialog(context, themeResId) {

    private lateinit var selectTimeListener: SelectTimeListener

    private var mSelectHour: String = "06:"
    private var mSelectMinute : String = "00"
    private var mDurationMinute: String = "0分钟"
    private var currentHour: Int = 0
    private var currentMinute : Int = 0
    private var currentDuration: Int = 0

    private var hourList = arrayListOf<String>()
    private var minuteList = arrayListOf<String>()
    private var durationList = arrayListOf<String>()

    interface SelectTimeListener {
        fun selectTime(hour: String, minute: String)
    }

    fun setOnselectTimeListener(selectTimeListener: SelectTimeListener) {
        this.selectTimeListener = selectTimeListener
    }


    init {
        initView()
    }

    fun setTitle(title: String) {
        tv_bottomdialog_title.text = title
    }

    private fun initView() {
        setContentView(R.layout.dialog_time_custom)
        hourList.clear()
        minuteList.clear()
        durationList.clear()
        hourList.addAll(DateUtils.getOneDayHour())
        minuteList.addAll(DateUtils.getOneHourMineute())
        durationList.addAll(DateUtils.getDurationTime())
        setProperty()
        initListener()
        initWheelView()
    }

    private fun initWheelView() {
        wheelTime0.setCyclic(false)
        wheelTime1.setCyclic(false)
        wheelTime2.setCyclic(false)

        wheelTime0.setTextColorCenter(Color.parseColor("#146EFF"))
        wheelTime1.setTextColorCenter(Color.parseColor("#146EFF"))
        wheelTime2.setTextColorCenter(Color.parseColor("#146EFF"))

        // 小时
        wheelTime0.adapter = object : WheelAdapter<Any?> {
            override fun indexOf(o: Any?): Int {
                return -1
            }

            override fun getItemsCount(): Int {
                return hourList.size
            }

            override fun getItem(index: Int): Any? {
                return hourList[index]
            }
        }
        wheelTime0.currentItem = currentHour
        wheelTime0.setOnItemSelectedListener { index ->
            val year = hourList[index]
            mSelectHour = year
        }

        // 分钟
        wheelTime2.adapter = object : WheelAdapter<Any?> {
            override fun indexOf(o: Any?): Int {
                return -1
            }

            override fun getItemsCount(): Int {
                return minuteList.size
            }

            override fun getItem(index: Int): Any? {
                return minuteList[index]
            }
        }
        wheelTime2.currentItem = currentMinute
        wheelTime2.setOnItemSelectedListener { index ->
            val minute = minuteList[index]
            mSelectMinute = minute
        }

        // 分钟
        wheelTime1.adapter = object : WheelAdapter<Any?> {
            override fun indexOf(o: Any?): Int {
                return -1
            }

            override fun getItemsCount(): Int {
                return durationList.size
            }

            override fun getItem(index: Int): Any? {
                return durationList[index]
            }
        }
        wheelTime1.currentItem = currentDuration
        wheelTime1.setOnItemSelectedListener { index ->
            val month = durationList[index]
            mDurationMinute = month
        }
    }

    private fun initListener() {
        // 取消
        tv_bottomdialog_cancel.setOnClickListener {
            dismiss()
        }
        // 确认
        tv_bottomdialog_confirm.setOnClickListener {
            selectTimeListener.selectTime(mSelectHour + mSelectMinute, mDurationMinute)
            dismiss()
        }
    }

    private fun setProperty() {
        val window = window
        val lp = window!!.attributes
        val d = window.windowManager.defaultDisplay
        lp.dimAmount = 0.3f
        lp.width = d.width
        window.attributes = lp
        window.setGravity(Gravity.BOTTOM)
        // 设置点击外围消散
        this.setCanceledOnTouchOutside(true)
    }
}