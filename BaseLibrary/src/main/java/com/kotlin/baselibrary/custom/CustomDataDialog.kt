package com.kotlin.baselibrary.custom

import android.annotation.SuppressLint
import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.view.Gravity
import com.contrarywind.adapter.WheelAdapter
import com.kotlin.baselibrary.R
import kotlinx.android.synthetic.main.dialog_bottom_custom.tv_bottomdialog_cancel
import kotlinx.android.synthetic.main.dialog_bottom_custom.tv_bottomdialog_confirm
import kotlinx.android.synthetic.main.dialog_bottom_custom.tv_bottomdialog_title
import kotlinx.android.synthetic.main.dialog_data_custom.*
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*

/**
 * 底部弹窗工具类
 */
class CustomDataDialog constructor(context: Context, themeResId: Int) : Dialog(context, themeResId) {

    private lateinit var selectDataListener: SelectDataListener
    private lateinit var selectItem: String

    private var mSelectYear: Int = 0
    private var mSelectMonth: Int = 0
    private var mSelectDay: Int = 0
    private var currentItemYear: Int = 0
    private var currentItemMonth: Int = 0
    private var currentItemDay: Int = 0

    interface SelectDataListener {
        fun selectData(year: String, month: String, day: String)
    }

    fun setOnselectDataListener(selectDataListener: SelectDataListener) {
        this.selectDataListener = selectDataListener
    }


    init {
        initView()
    }

    fun setTitle(title: String) {
        tv_bottomdialog_title.text = title
    }

    private fun initView() {
        setContentView(R.layout.dialog_data_custom)
        setProperty()
        initListener()
    }

    // 传入已选中的日期
    fun setSelectedTime(year: String,month: String,day : String) {
        mSelectYear = Integer.valueOf(year)
        mSelectMonth = Integer.valueOf(month)
        mSelectDay = Integer.valueOf(day)
        // 年
        for (i in 0 until getYearList().size) {
            if (mSelectYear == Integer.valueOf(getYearList()[i].replace("年", ""))) {
                currentItemYear = i
            }
        }
        // 月
        for (i in 0 until getMonthList().size) {
            if (mSelectMonth == Integer.valueOf(getMonthList()[i].replace("月", ""))) {
                currentItemMonth = i
            }
        }
        // 日
        for (i in 0 until getDayList("$mSelectYear-$mSelectMonth").size) {
            if (mSelectDay == Integer.valueOf(getDayList("$mSelectYear-$mSelectMonth")[i].replace("日", ""))) {
                currentItemDay = i
            }
        }
        // wheelView
        initWheelView()
    }

    // 获取当前时间  年-月-日
    @SuppressLint("SimpleDateFormat")
    fun getCurrentTime() {
        val simpleDateFormat = SimpleDateFormat("yyyy-MM-dd")
        val date = Date(System.currentTimeMillis())
        val currentTime = simpleDateFormat.format(date)
        val split = currentTime.split("-".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
        mSelectYear = Integer.valueOf(split[0])
        mSelectMonth = Integer.valueOf(split[1])
        mSelectDay = Integer.valueOf(split[2])
        // 年
        for (i in 0 until getYearList().size) {
            if (mSelectYear == Integer.valueOf(getYearList()[i].replace("年", ""))) {
                currentItemYear = i
            }
        }
        // 月
        for (i in 0 until getMonthList().size) {
            if (mSelectMonth == Integer.valueOf(getMonthList()[i].replace("月", ""))) {
                currentItemMonth = i
            }
        }
        // 日
        for (i in 0 until getDayList("$mSelectYear-$mSelectMonth").size) {
            if (mSelectDay == Integer.valueOf(getDayList("$mSelectYear-$mSelectMonth")[i].replace("日", ""))) {
                currentItemDay = i
            }
        }
        // wheelView
        initWheelView()
    }

    private fun initWheelView() {
        wheel0.setCyclic(false)
        wheel1.setCyclic(false)
        wheel2.setCyclic(false)

        wheel0.setTextColorCenter(Color.parseColor("#146EFF"))
        wheel1.setTextColorCenter(Color.parseColor("#146EFF"))
        wheel2.setTextColorCenter(Color.parseColor("#146EFF"))

        // 年
        wheel0.adapter = object : WheelAdapter<Any?> {
            override fun indexOf(o: Any?): Int {
                return -1
            }

            override fun getItemsCount(): Int {
                return getYearList().size
            }

            override fun getItem(index: Int): Any? {
                return getYearList()[index]
            }
        }
        wheel0.currentItem = currentItemYear
        wheel0.setOnItemSelectedListener { index ->
            val year = getYearList()[index].replace("年", "")
            mSelectYear = Integer.valueOf(year)
            setDayAllDataAndCurrentData()
        }

        // 月
        wheel1.adapter = object : WheelAdapter<Any?> {
            override fun indexOf(o: Any?): Int {
                return -1
            }

            override fun getItemsCount(): Int {
                return getMonthList().size
            }

            override fun getItem(index: Int): Any? {
                return getMonthList()[index]
            }
        }
        wheel1.currentItem = currentItemMonth
        wheel1.setOnItemSelectedListener { index ->
            val month = getMonthList()[index].replace("月", "")
            mSelectMonth = Integer.valueOf(month)
            setDayAllDataAndCurrentData()
        }

        // 日
        wheel2.adapter = object : WheelAdapter<Any?> {
            override fun indexOf(o: Any?): Int {
                return -1
            }

            override fun getItemsCount(): Int {
                return getDayList("$mSelectYear-$mSelectMonth").size
            }

            override fun getItem(index: Int): Any? {
                return getDayList("$mSelectYear-$mSelectMonth")[index]
            }
        }
        wheel2.currentItem = currentItemDay
        wheel2.setOnItemSelectedListener { index ->
            val day = getDayList("$mSelectYear-$mSelectMonth")[index].replace("日", "")
            mSelectDay = Integer.valueOf(day)
        }
    }

    private fun initListener() {
        // 取消
        tv_bottomdialog_cancel.setOnClickListener {
            dismiss()
        }
        // 确认
        tv_bottomdialog_confirm.setOnClickListener {
            selectDataListener.selectData(mSelectYear.toString(),mSelectMonth.toString(),mSelectDay.toString())
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

    private fun setDayAllDataAndCurrentData() {
        val dayList = getDayList("$mSelectYear-$mSelectMonth")
        checkDayIsExist(dayList)
        for (i in dayList.indices) {
            if (mSelectDay.toString() + "日" == dayList[i]) {
                wheel2.currentItem = i
            }
        }
    }

    private fun checkDayIsExist(dayList: List<String>) {
        val selectDayStr = mSelectDay.toString() + "日"
        if (!dayList.contains(selectDayStr)) {
            mSelectDay = Integer.valueOf(dayList[dayList.size - 1].replace("日", ""))
        }
    }

    private fun getYearList(): List<String> {
        val dataList = ArrayList<String>()
        for (i in 1990..2034) {
            dataList.add(i.toString() + "年")
        }
        return dataList
    }

    private fun getMonthList(): List<String> {
        val monthList = ArrayList<String>()
        for (i in 1..12) {
            monthList.add((if (i > 9) i else "0$i").toString() + "月")
        }
        return monthList
    }

    private fun getDayList(yearMonth: String): List<String> {
        val cal = Calendar.getInstance()
        cal.time = getDate(yearMonth, "yyyy-MM")!!
        val monthMaxDay = cal.getActualMaximum(Calendar.DAY_OF_MONTH)
        val dayList = ArrayList<String>()
        for (i in 1..monthMaxDay) {
            dayList.add((if (i > 9) i else "0$i").toString() + "日")
        }
        return dayList
    }

    @SuppressLint("SimpleDateFormat")
    private fun getDate(dateStr: String, datePattern: String): Date? {
        val sdf = SimpleDateFormat(datePattern)
        try {
            return sdf.parse(dateStr)
        } catch (e: ParseException) {
            e.printStackTrace()
        }
        return null
    }
}