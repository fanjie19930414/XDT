package com.kotlin.baselibrary.utils

import android.annotation.SuppressLint
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList
import android.widget.TextView

/**
 * 日期工具类 默认使用 "yyyy-MM-dd HH:mm:ss" 格式化日期

 */
object DateUtils {
    /**
     * 1天的小时（隔5min）
     */
    var hourList = arrayListOf<String>()
    /**
     * 1小时的Min(隔5min)
     */
    var minuteList = arrayListOf<String>()
    /**
     * 课程间隔(隔5min)
     */
    var durationList = arrayListOf<String>()
    /**
     * 英文简写（默认）如：12-01
     */
    var FORMAT_MONTH_DAY = "MM-dd"
    /**
     * 英文简写（默认）如：2010-12-01
     */
    var FORMAT_SHORT = "yyyy-MM-dd"
    /**
     * 英文全称 如：2010-12-01 23:15:06
     */
    /**
     * 获得默认的 date pattern
     */
    var datePattern = "yyyy-MM-dd HH:mm:ss"

    var FORMAT_LONG_NEW = "yyyy-MM-dd HH:mm"
    /**
     * 精确到毫秒的完整时间 如：yyyy-MM-dd HH:mm:ss.S
     */
    var FORMAT_FULL = "yyyy-MM-dd HH:mm:ss.S"
    /**
     * 中文简写 如：2010年12月01日
     */
    var FORMAT_SHORT_CN_MINI = "MM月dd日 HH:mm"
    /**
     * 中文简写 如：2010年12月01日
     */
    var FORMAT_SHORT_CN = "yyyy年MM月dd日"
    /**
     * 中文全称 如：2010年12月01日 23时15分06秒
     */
    var FORMAT_LONG_CN = "yyyy年MM月dd日  HH时mm分ss秒"
    /**
     * 精确到毫秒的完整中文时间
     */
    var FORMAT_FULL_CN = "yyyy年MM月dd日  HH时mm分ss秒SSS毫秒"
    /**
     * 精确到毫秒的完整中文时间
     */
    var FORMAT_SPEFULL_CN = "yyyy年MM月dd日  HH:mm"
    /**
     * 英文简写（默认）如：2010-12-01
     */
    var FORMAT_SHORT_SPE = "yyyyMMdd"
    var FORMAT_SHORT_SPE_ = "HH:mm"

    var TIMEZONE = "Asia/Shanghai"

    /**
     * 根据预设格式返回当前日期

     * @return
     */
    val now: String
        get() = format(Date())

    /**
     * 根据用户格式返回当前日期

     * @param format
     * *
     * @return
     */
    fun getNow(format: String): String {
        return format(Date(), format)
    }


    val defTimeZone: TimeZone
        get() = TimeZone.getTimeZone(TIMEZONE)

    /**
     * 使用用户格式格式化日期

     * @param date
     * *            日期
     * *
     * @param pattern
     * *            日期格式
     * *
     * @return
     */
    @JvmOverloads
    fun format(date: Date?, pattern: String = datePattern): String {
        var returnValue = ""
        if (date != null) {
            val df = SimpleDateFormat(pattern)
            df.timeZone = defTimeZone
            returnValue = df.format(date)
        }
        return returnValue
    }

    /**
     * 使用用户格式提取字符串日期

     * @param strDate
     * *            日期字符串
     * *
     * @param pattern
     * *            日期格式
     * *
     * @return
     */
    @JvmOverloads
    fun parse(strDate: String, pattern: String = datePattern): Date? {
        val df = SimpleDateFormat(pattern)
        df.timeZone = defTimeZone
        try {
            return df.parse(strDate)
        } catch (e: ParseException) {
            e.printStackTrace()
            return null
        }

    }

    /**
     * 时间戳转date str

     */

    fun convertTimeToString(time: Long, format: String): String {
        val sdf = SimpleDateFormat(format)
        sdf.timeZone = defTimeZone
        return sdf.format(time)
    }

    /**
     * 获取当前时间的前一天时间
     * @param cl
     * *
     * @return
     */
    fun getBeforeDay(cl: Calendar): Calendar {
        val day = cl.get(Calendar.DATE)
        cl.set(Calendar.DATE, day - 1)
        return cl
    }

    /**
     * 获取当前时间的后一天时间
     * @param cl
     * *
     * @return
     */
    fun getAfterDay(cl: Calendar): Calendar {
        val day = cl.get(Calendar.DATE)
        cl.set(Calendar.DATE, day + 1)
        return cl
    }

    fun getWeek(c: Calendar): String {
        var Week = ""

        if (c.get(Calendar.DAY_OF_WEEK) == Calendar.SUNDAY) {
            Week += "周天"
        }
        if (c.get(Calendar.DAY_OF_WEEK) == Calendar.MONDAY) {
            Week += "周一"
        }
        if (c.get(Calendar.DAY_OF_WEEK) == Calendar.TUESDAY) {
            Week += "周二"
        }
        if (c.get(Calendar.DAY_OF_WEEK) == Calendar.WEDNESDAY) {
            Week += "周三"
        }
        if (c.get(Calendar.DAY_OF_WEEK) == Calendar.THURSDAY) {
            Week += "周四"
        }
        if (c.get(Calendar.DAY_OF_WEEK) == Calendar.FRIDAY) {
            Week += "周五"
        }
        if (c.get(Calendar.DAY_OF_WEEK) == Calendar.SATURDAY) {
            Week += "周六"
        }
        return Week
    }

    // date类型转换为String类型
    // formatType格式为yyyy-MM-dd HH:mm:ss//yyyy年MM月dd日 HH时mm分ss秒
    // data Date类型的时间
    fun dateToString(date: Date, formatType: String): String {
        val sdf = SimpleDateFormat(formatType)
        sdf.timeZone = defTimeZone
        return sdf.format(date)
    }

    // long类型转换为String类型
    // currentTime要转换的long类型的时间
    // formatType要转换的string类型的时间格式
    @Throws(ParseException::class)
    fun longToString(currentTime: Long, formatType: String): String {
        val date = longToDate(currentTime, formatType) // long类型转成Date类型
        val strTime = dateToString(date, formatType) // date类型转成String
        return strTime
    }

    // string类型转换为date类型
    // strTime要转换的string类型的时间，formatType要转换的格式yyyy-MM-dd HH:mm:ss//yyyy年MM月dd日
    // HH时mm分ss秒，
    // strTime的时间格式必须要与formatType的时间格式相同
    @Throws(ParseException::class)
    fun stringToDate(strTime: String, formatType: String): Date {
        val formatter = SimpleDateFormat(formatType)
        formatter.timeZone = defTimeZone
        var date: Date? = null
        date = formatter.parse(strTime)
        return date
    }

    // long转换为Date类型
    // currentTime要转换的long类型的时间
    // formatType要转换的时间格式yyyy-MM-dd HH:mm:ss//yyyy年MM月dd日 HH时mm分ss秒
    @Throws(ParseException::class)
    fun longToDate(currentTime: Long, formatType: String): Date {
        val dateOld = Date(currentTime) // 根据long类型的毫秒数生命一个date类型的时间
        val sDateTime = dateToString(dateOld, formatType) // 把date类型的时间转换为string
        val date = stringToDate(sDateTime, formatType) // 把String类型转换为Date类型
        return date
    }

    // string类型转换为long类型
    // strTime要转换的String类型的时间
    // formatType时间格式
    // strTime的时间格式和formatType的时间格式必须相同
    fun stringToLong(strTime: String, formatType: String): Long {
        var date: Date? = null // String类型转成date类型
        try {
            date = stringToDate(strTime, formatType)
        } catch (e: ParseException) {
            e.printStackTrace()
        }

        return if (date == null) {
            0
        } else {
            dateToLong(date)
        }
    }

    // date类型转换为long类型
    // date要转换的date类型的时间
    fun dateToLong(date: Date): Long {
        return date.time
    }

    //当前时间毫秒数
    val curTime: Long
        get() {
            val c = Calendar.getInstance(defTimeZone)
            return c.timeInMillis
        }

    // 获取1天有多少个小时(6-22)
    fun getOneDayHour(): ArrayList<String> {
        hourList.clear()
        for (i in 6..22) {
            hourList.add((if (i > 9) i else "0$i").toString() + ":")
        }
        return hourList
    }

    // 获取1小时有多少分
    fun getOneHourMineute(): ArrayList<String> {
        minuteList.clear()
        for (i in 0..55 step 5) {
            minuteList.add((if (i > 9) i else "0$i").toString())
        }
        return minuteList
    }

    // 1小时的时间间隔
    fun getDurationTime(): ArrayList<String> {
        durationList.clear()
        for (j in 0..60 step 5) {
            durationList.add(j.toString() + "分钟")
        }
        return durationList
    }

    // 将 时/分 转化为秒
    fun formatTurnSecond(time: String): Long {
        val s = time
        val timeSplit = s.split(":")

        val hour = timeSplit[0].trim()
        val minute = timeSplit[1].trim()
        return (hour.toInt() * 60 * 60 + minute.toInt() * 60).toLong()
    }

    // 将 秒 转化为 时/分
    fun changeToTime(second: Int): String {
        var h = 0
        var m = 0
        var s = 0
        val temp = (second % 3600)
        if (second > 3600) {
            h = (second / 3600)
            if (temp != 0) {
                if (temp > 60) {
                    m = temp / 60
                    if (temp % 60 != 0) {
                        s = temp % 60
                    }
                } else {
                    s = temp
                }
            }
        } else {
            m = second / 60
            if (second % 60 != 0) {
                s = second % 60
            }
        }
        return (if (Integer.valueOf(h) > 9) h else "0$h").toString() + ":" + (if (Integer.valueOf(m) > 9) m else "0$m").toString()
    }

    /**
     * 获取分
     * */
    fun getMinute(): Int {
        val cd = Calendar.getInstance()
        val minute =  cd.get(Calendar.MINUTE)
        return if (Integer.valueOf(minute) > 9) minute else ("0$minute").toInt()
    }

    /**
     * 获取时
     * */
    fun getHour(): Int {
        val cd = Calendar.getInstance()
        return cd.get(Calendar.HOUR_OF_DAY)
    }

    /**
     * 获取日
     * */
    fun getDay(): Int{
        val cd = Calendar.getInstance()
        return cd.get(Calendar.DAY_OF_MONTH)
    }

    /**
     * 获取月
     */
    fun getMonth(): Int {
        val cd = Calendar.getInstance()
        return cd.get(Calendar.MONTH) + 1
    }

    /**
     * 获取年
     */
    fun getYear(): Int {
        val cd = Calendar.getInstance()
        return cd.get(Calendar.YEAR)
    }

    /**
     * 获取周几 1为周日  以此类推
     */
    fun getWeek() : Int {
        val cal = Calendar.getInstance()
        return cal.get(Calendar.DAY_OF_WEEK)
    }

    /**
     * 把"yyyy-MM-dd HH:mm:ss"格式日期转换成毫秒
     * @param strDate
     * @return 转换后毫秒的值
     * @author hongj
     */
    fun paseDateTomillise(strDate: String): Long {
        var year: String? = null
        var month: String? = null
        var day = ""
        var hms = ""
        if (strDate.contains(" ") && !strDate.endsWith(" ")) {
            val s = strDate.split(" ".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
            hms = s[1]
        }
        val getYear = strDate.split("-".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
        year = getYear[0].substring(2, 4)
        month = getYear[1]
        if ("1".equals(month, ignoreCase = true) || "01".equals(month, ignoreCase = true)) {
            month = "JAN"
        } else if ("2".equals(month, ignoreCase = true) || "02".equals(month, ignoreCase = true)) {
            month = "FEB"
        } else if ("3".equals(month, ignoreCase = true) || "03".equals(month, ignoreCase = true)) {
            month = "MAR"
        } else if ("4".equals(month, ignoreCase = true) || "04".equals(month, ignoreCase = true)) {
            month = "APR"
        } else if ("5".equals(month, ignoreCase = true) || "05".equals(month, ignoreCase = true)) {
            month = "MAY"
        } else if ("6".equals(month, ignoreCase = true) || "06".equals(month, ignoreCase = true)) {
            month = "JUN"
        } else if ("7".equals(month, ignoreCase = true) || "07".equals(month, ignoreCase = true)) {
            month = "JUL"
        } else if ("8".equals(month, ignoreCase = true) || "08".equals(month, ignoreCase = true)) {
            month = "AUG"
        } else if ("9".equals(month, ignoreCase = true) || "09".equals(month, ignoreCase = true)) {
            month = "SEPT"
        } else if ("10".equals(month, ignoreCase = true)) {
            month = "OCT"
        } else if ("11".equals(month, ignoreCase = true)) {
            month = "NOV"
        } else if ("12".equals(month, ignoreCase = true)) {
            month = "DEC"
        }
        if (getYear[2].contains(" ")) {
            day = getYear[2].split(" ".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()[0]
        } else {
            day = getYear[2]
        }
        val datees = "$day-$month-$year $hms"
        val datee = Date(datees)
        return datee.time
    }

    /**
     * 根据日期获取是星期几
     */
    fun getWeekFromTime(time : String) : Int {
        var week = -1
        val format = SimpleDateFormat("yy-MM-dd")
        val c = Calendar.getInstance()
        c.time = format.parse(time)
        when(c.get(Calendar.DAY_OF_WEEK)) {
            1 -> week = 7
            2 -> week = 1
            3 -> week = 2
            4 -> week = 3
            5 -> week = 4
            6 -> week = 5
            7 -> week = 6
        }
        return week
    }

    /**
     * 根据日期获取是星期几
     */
    fun getWeekFullFromTime(time : String) : Int {
        var week = -1
        val format = SimpleDateFormat("yyyy-MM-dd")
        val c = Calendar.getInstance()
        c.time = format.parse(time)
        when(c.get(Calendar.DAY_OF_WEEK)) {
            1 -> week = 7
            2 -> week = 1
            3 -> week = 2
            4 -> week = 3
            5 -> week = 4
            6 -> week = 5
            7 -> week = 6
        }
        return week
    }

    fun getMorningOrNoon(tv: TextView) {
        val c = Calendar.getInstance()
        val d = c.get(Calendar.HOUR_OF_DAY)
        when {
            d < 11 -> tv.text = "Hi~早上好"
            d < 13 -> tv.text = "Hi~中午好"
            d < 18 -> tv.text = "Hi~下午好"
            d < 24 -> tv.text = "Hi~晚上好"
        }
    }

    /**
     * 传入日期，返回星座
     */
    fun getConstellation(str: String): String {
        var constellation = ""
        if (constellationList.isEmpty()) {
            fillData()
        }
        val format = SimpleDateFormat("yyyy-MM-dd")
        val birthday = Calendar.getInstance()
        birthday.time = format.parse(str)
        val month = birthday.get(Calendar.MONTH) + 1
        val day = birthday.get(Calendar.DAY_OF_MONTH)
        when (month) {
            1 ->
                //Capricorn 摩羯座（12月22日～1月20日）
                constellation = if (day <= 20) constellationList[11] else constellationList[0]
            2 ->
                //Aquarius 水瓶座（1月21日～2月19日）
                constellation = if (day <= 19) constellationList[0] else constellationList[1]
            3 ->
                //Pisces 双鱼座（2月20日～3月20日）
                constellation = if (day <= 20) constellationList[1] else constellationList[2]
            4 ->
                //白羊座 3月21日～4月20日
                constellation = if (day <= 20) constellationList[2] else constellationList[3]
            5 ->
                //金牛座 4月21～5月21日
                constellation = if (day <= 21) constellationList[3] else constellationList[4]
            6 ->
                //双子座 5月22日～6月21日
                constellation = if (day <= 21) constellationList[4] else constellationList[5]
            7 ->
                //Cancer 巨蟹座（6月22日～7月22日）
                constellation = if (day <= 22) constellationList[5] else constellationList[6]
            8 ->
                //Leo 狮子座（7月23日～8月23日）
                constellation = if (day <= 23) constellationList[6] else constellationList[7]
            9 ->
                //Virgo 处女座（8月24日～9月23日）
                constellation = if (day <= 23) constellationList[7] else constellationList[8]
            10 ->
                //Libra 天秤座（9月24日～10月23日）
                constellation = if (day <= 23) constellationList[8] else constellationList[9]
            11 ->
                //Scorpio 天蝎座（10月24日～11月22日）
                constellation = if (day <= 22) constellationList[9] else constellationList[10]
            12 ->
                //Sagittarius 射手座（11月23日～12月21日）
                constellation = if (day <= 21) constellationList[10] else constellationList[11]
        }
        return constellation
    }

    private var constellationList = arrayListOf<String>()//存放星座的集合

    private fun fillData() {
        constellationList.add(0, "水瓶座")
        constellationList.add(1, "双鱼座")
        constellationList.add(2, "白羊座")
        constellationList.add(3, "金牛座")
        constellationList.add(4, "双子座")
        constellationList.add(5, "巨蟹座")
        constellationList.add(6, "狮子座")
        constellationList.add(7, "处女座")
        constellationList.add(8, "天秤座")
        constellationList.add(9, "天蝎座")
        constellationList.add(10, "射手座")
        constellationList.add(11, "魔羯座")
    }

    /**
     * 获取OSS图片的ObjectName
     */
    fun getOSSObjectName(): String {
        val uuid = UUID.randomUUID().toString()
        return "xdt" + "/" + getYear() + "/" + getMonth() + "/" + getDay() + "/" + uuid + ".jpg"
    }

    /**
     * 获取OSS视频的ObjectName
     */
    fun getOSSVideoObjectName(): String {
        val uuid = UUID.randomUUID().toString()
        return "xdt" + "/" + getYear() + "/" + getMonth() + "/" + getDay() + "/" + uuid + ".mp4"
    }

    /**
     * 根据毫秒数获取当前分钟
     */
    fun getGapTime(time: Long): String {
        val hours = time / (1000 * 60 *60)
        val minutes = (time - hours * (1000 * 60 *60)) / (1000 * 60)
        return minutes.toString()
    }

    // 根据当前日期获得所在周的日期区间（周一和周日日期）
    @SuppressLint("SimpleDateFormat")
    fun getTimeInterval(date: Date): String {
        //格式化日期
        val sdf = SimpleDateFormat("yyyy-MM-dd")
        val cal = Calendar.getInstance()
        cal.time = date
        // 判断要计算的日期是否是周日，如果是则减一天计算周六的，否则会出问题，计算到下一周去了
        val dayWeek = cal.get(Calendar.DAY_OF_WEEK)// 获得当前日期是一个星期的第几天
        if (1 == dayWeek) {
            cal.add(Calendar.DAY_OF_MONTH, -1)
        }
        // System.out.println("要计算日期为:" + sdf.format(cal.getTime())); // 输出要计算日期
        // 设置一个星期的第一天，按中国的习惯一个星期的第一天是星期一
        cal.firstDayOfWeek = Calendar.MONDAY
        // 获得当前日期是一个星期的第几天
        val day = cal.get(Calendar.DAY_OF_WEEK)
        // 根据日历的规则，给当前日期减去星期几与一个星期第一天的差值
        cal.add(Calendar.DATE, cal.firstDayOfWeek - day)
        val imptimeBegin = sdf.format(cal.time)
        // System.out.println("所在周星期一的日期：" + imptimeBegin);
        cal.add(Calendar.DATE, 6)
        val imptimeEnd = sdf.format(cal.time)
        // System.out.println("所在周星期日的日期：" + imptimeEnd);
        return "$imptimeBegin,$imptimeEnd"
    }

    // 根据当前日期获得上周的日期区间（上周周一和周日日期）
    @SuppressLint("SimpleDateFormat")
    fun getLastTimeInterval(): String {
        //格式化日期
        val sdf = SimpleDateFormat("yyyy-MM-dd")
        val calendar1 = Calendar.getInstance()
        val calendar2 = Calendar.getInstance()
        val dayOfWeek = calendar1.get(Calendar.DAY_OF_WEEK) - 1
        val offset1 = 1 - dayOfWeek
        val offset2 = 7 - dayOfWeek
        calendar1.add(Calendar.DATE, offset1 - 7)
        calendar2.add(Calendar.DATE, offset2 - 7)
        // System.out.println(sdf.format(calendar1.getTime()));// last Monday
        val lastBeginDate = sdf.format(calendar1.time)
        // System.out.println(sdf.format(calendar2.getTime()));// last Sunday
        val lastEndDate = sdf.format(calendar2.time)
        return "$lastBeginDate,$lastEndDate"
    }

    // 获取一周开始到结束的list集合
    fun findDates(dBegin: Date, dEnd: Date): List<Date> {
        val lDate = arrayListOf<Date>()
        lDate.add(dBegin)
        val calBegin = Calendar.getInstance()
        // 使用给定的 Date 设置此 Calendar 的时间
        calBegin.time = dBegin
        val calEnd = Calendar.getInstance()
        // 使用给定的 Date 设置此 Calendar 的时间
        calEnd.time = dEnd
        // 测试此日期是否在指定日期之后
        while (dEnd.after(calBegin.time)) {
            // 根据日历的规则，为给定的日历字段添加或减去指定的时间量
            calBegin.add(Calendar.DAY_OF_MONTH, 1)
            lDate.add(calBegin.time)
        }
        return lDate
    }

    // 获取昨天时间
    @SuppressLint("SimpleDateFormat")
    fun getLastDay(): String {
        val dateFormat = SimpleDateFormat("yyyy-MM-dd")
        val calendar = Calendar.getInstance()
        calendar.set(Calendar.HOUR_OF_DAY, -24)
        return dateFormat.format(calendar.time)
    }

    /**
     * 获取系统时间
     * @return
     */
    fun getDate(): String {
        val ca = Calendar.getInstance()
        val year = ca.get(Calendar.YEAR)           // 获取年份
        val month = ca.get(Calendar.MONTH)         // 获取月份
        val day = ca.get(Calendar.DATE)            // 获取日
        val minute = ca.get(Calendar.MINUTE)       // 分
        val hour = ca.get(Calendar.HOUR)           // 小时
        return "" + year + (month + 1) + day + hour + minute
    }
}
