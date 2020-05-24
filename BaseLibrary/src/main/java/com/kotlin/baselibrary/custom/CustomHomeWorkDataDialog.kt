package com.kotlin.baselibrary.custom

import android.annotation.SuppressLint
import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.view.Gravity
import com.contrarywind.adapter.WheelAdapter
import com.contrarywind.listener.OnItemSelectedListener
import com.kotlin.baselibrary.R
import com.kotlin.baselibrary.utils.DateUtils
import kotlinx.android.synthetic.main.dialog_homework_data_custom.*
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

/**
 * 底部弹窗工具类
 */
class CustomHomeWorkDataDialog constructor(context: Context, themeResId: Int) : Dialog(context, themeResId) {

    private lateinit var selectDataListener: SelectDataListener
    private lateinit var selectItem: String

    private var mSelectYear: String = ""
    private var mSelectMonth: String = ""
    private var mSelectDay: String = ""
    private var mSelectHour: String = ""
    private var mSelectMinute: String = ""

    private var currentItemYear: Int = 0
    private var currentItemMonth: Int = 0
    private var currentItemDay: Int = 0
    private var currentItemHour: Int = 0
    private var currentItemMinute: Int = 0

    interface SelectDataListener {
        fun selectData(year: String, month: String, day: String, hour: String, minute: String)
    }

    fun setOnselectDataListener(selectDataListener: SelectDataListener) {
        this.selectDataListener = selectDataListener
    }

    init {
        initView()
    }

    fun setTitle(title: String) {
        tv_bottomdialog_homework_title.text = title
    }

    private fun initView() {
        setContentView(R.layout.dialog_homework_data_custom)
        setProperty()
        initListener()
    }

    // 传入已选中的日期
    fun setSelectedTime(year: String, month: String, day: String,hour: String,minute: String) {
        mSelectYear = year
        mSelectMonth = month
        mSelectDay = day
        mSelectHour = hour
        mSelectMinute = minute

        mSelectHour = if (mSelectHour.toInt() > 9) {
            mSelectHour
        } else {
            "0$mSelectHour"
        }

        mSelectMonth = if (mSelectMonth.toInt() > 9) {
            mSelectMonth
        } else {
            "0$mSelectMonth"
        }

        mSelectDay = if (mSelectDay.toInt() > 9) {
            mSelectDay
        } else {
            "0$mSelectDay"
        }

        // 年
        for (i in 0 until getYearList().size) {
            if (mSelectYear == getYearList()[i].replace("年", "")) {
                currentItemYear = i
            }
        }
        // 月
        for (i in 0 until getMonthList().size) {
            if (mSelectMonth == getMonthList()[i].replace("月", "")) {
                currentItemMonth = i
            }
        }
        // 日
        for (i in 0 until getDayList("$mSelectYear-$mSelectMonth").size) {
            if (mSelectDay == getDayList("$mSelectYear-$mSelectMonth")[i].replace("日", "")) {
                currentItemDay = i
            }
        }
        // 小时
        for (i in 0 until getHour().size) {
            if (("$mSelectHour : ") == getHour()[i]) {
                currentItemHour = i
            }
        }

        val fiveMin = DateUtils.curTime + 5 * 60 *1000
        mSelectMinute = DateUtils.getGapTime(fiveMin)
        if (mSelectHour != DateUtils.convertTimeToString(fiveMin,"yyyy-MM-dd HH:mm:ss").substring(11,13)) {
            mSelectHour = DateUtils.convertTimeToString(fiveMin,"yyyy-MM-dd HH:mm:ss").substring(11,13)
            for (j in 0 until getHour().size) {
                if (("$mSelectHour : ") == getHour()[j]) {
                    currentItemHour = j
                }
            }
        }
        val currentMinute: String = if ((mSelectMinute.toInt()) > 9) {
            "${mSelectMinute.toInt()}"
        } else {
            "0${mSelectMinute.toInt()}"
        }

        // 分
        for (i in 0 until getOneHourMinute().size) {
            mSelectMinute = currentMinute
            if (currentMinute == getOneHourMinute()[i]) {
                currentItemMinute = i
            }
        }
        // wheelView
        initWheelView()
    }

    // 获取当前时间  年-月-日
    @SuppressLint("SimpleDateFormat")
    fun getCurrentTime() {
        val simpleDateFormat = SimpleDateFormat("yyyy-MM-dd HH-mm")
        val date = Date(System.currentTimeMillis())
        val currentTime = simpleDateFormat.format(date)
        val split = currentTime.split("-".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
        mSelectYear = split[0]
        mSelectMonth = split[1]
        mSelectDay = split[2]
        mSelectHour = split[3]
        mSelectMinute = split[4]

        // 年
        for (i in 0 until getYearList().size) {
            if (mSelectYear == getYearList()[i].replace("年", "")) {
                currentItemYear = i
            }
        }
        // 月
        for (i in 0 until getMonthList().size) {
            if (mSelectMonth == getMonthList()[i].replace("月", "")) {
                currentItemMonth = i
            }
        }
        // 日
        for (i in 0 until getDayList("$mSelectYear-$mSelectMonth").size) {
            if (mSelectDay == getDayList("$mSelectYear-$mSelectMonth")[i].replace("日", "")
            ) {
                currentItemDay = i
            }
        }
        // wheelView
        initWheelView()
    }

    private fun initWheelView() {
        wheel0HomeWork.setCyclic(false)
        wheel1HomeWork.setCyclic(false)
        wheel2HomeWork.setCyclic(false)
        wheel3HomeWork.setCyclic(false)
        wheel4HomeWork.setCyclic(false)
        wheel0HomeWork.setTextColorCenter(Color.parseColor("#146EFF"))
        wheel1HomeWork.setTextColorCenter(Color.parseColor("#146EFF"))
        wheel2HomeWork.setTextColorCenter(Color.parseColor("#146EFF"))
        wheel3HomeWork.setTextColorCenter(Color.parseColor("#146EFF"))
        wheel4HomeWork.setTextColorCenter(Color.parseColor("#146EFF"))
        // 年
        wheel0HomeWork.adapter = object : WheelAdapter<Any?> {
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
        wheel0HomeWork.currentItem = currentItemYear
        wheel0HomeWork.setOnItemSelectedListener { index ->
            val year = getYearList()[index].replace("年", "")
            mSelectYear = year
            setDayAllDataAndCurrentData()
        }

        // 月
        wheel1HomeWork.adapter = object : WheelAdapter<Any?> {
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
        wheel1HomeWork.currentItem = currentItemMonth
        wheel1HomeWork.setOnItemSelectedListener { index ->
            val month = getMonthList()[index].replace("月", "")
            mSelectMonth = month
            setDayAllDataAndCurrentData()
        }

        // 日
        wheel2HomeWork.adapter = object : WheelAdapter<Any?> {
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
        wheel2HomeWork.currentItem = currentItemDay
        wheel2HomeWork.setOnItemSelectedListener { index ->
            val day = getDayList("$mSelectYear-$mSelectMonth")[index].replace("日", "")
            mSelectDay = day
        }

        // 时
        wheel3HomeWork.adapter = object : WheelAdapter<Any?> {
            override fun indexOf(o: Any?): Int {
                return -1
            }

            override fun getItemsCount(): Int {
                return getHour().size
            }

            override fun getItem(index: Int): Any? {
                return getHour()[index]
            }
        }
        wheel3HomeWork.currentItem = currentItemHour
        wheel3HomeWork.setOnItemSelectedListener { index ->
            val hour = getHour()[index].replace(" : ","")
            mSelectHour = hour
        }

        // 分
        wheel4HomeWork.adapter = object : WheelAdapter<Any?> {
            override fun indexOf(o: Any?): Int {
                return -1
            }

            override fun getItemsCount(): Int {
                return getOneHourMinute().size
            }

            override fun getItem(index: Int): Any? {
                return getOneHourMinute()[index]
            }
        }
        wheel4HomeWork.currentItem = currentItemMinute
        wheel4HomeWork.setOnItemSelectedListener(object : OnItemSelectedListener {
            override fun onItemSelected(index: Int) {
                val minute = getOneHourMinute()[index]
                mSelectMinute = minute
            }
        })
    }

    private fun initListener() {
        // 取消
        tv_bottomdialog_homework_cancel.setOnClickListener {
            dismiss()
        }
        // 确认
        tv_bottomdialog_homework_confirm.setOnClickListener {
            selectDataListener.selectData(
                mSelectYear,
                mSelectMonth,
                mSelectDay,
                mSelectHour,
                mSelectMinute
            )
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
                wheel2HomeWork.currentItem = i
            }
        }
    }

    private fun checkDayIsExist(dayList: List<String>) {
        val selectDayStr = mSelectDay.toString() + "日"
        if (!dayList.contains(selectDayStr)) {
            mSelectDay = dayList[dayList.size - 1].replace("日", "")
        }
    }

    private fun getYearList(): List<String> {
        val dataList = ArrayList<String>()
        for (i in DateUtils.getYear()..2034) {
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

    private fun getHour(): List<String> {
        val hourList = ArrayList<String>()
        for (i in 0..23) {
            hourList.add(if (i > 9) "$i : " else "0$i : ")
        }
        return hourList
    }

    // 获取1小时有多少分
    fun getOneHourMinute(): ArrayList<String> {
        val minuteList = arrayListOf<String>()
        for (i in 0..59 step 1) {
            minuteList.add((if (i > 9) i else "0$i").toString())
        }
        return minuteList
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