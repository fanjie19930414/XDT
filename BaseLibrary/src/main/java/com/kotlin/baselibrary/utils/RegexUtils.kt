package com.kotlin.baselibrary.utils

/**
 * @desciption: 正则工具
 */
object RegexUtils {
    /**
     *  判断是否为手机号 正则判断
     */
    fun checkMobileNum(mobileNums: String): Boolean {
        //  "[1]"代表第1位为数字1，"[358]"代表第二位可以为3、5、8中的一个，"\\d{9}"代表后面是可以是0～9的数字，有9位。
        val telRegex = "^((13[0-9])|(14[5,7,9])|(15[^4])|(18[0-9])|(17[0,1,3,5,6,7,8]))\\d{8}$"
        return mobileNums.matches(telRegex.toRegex())
    }
    /**
     * 判断新密码 请输入新密码(8-16位，同时包含数字和字母)
     */
    fun checkPassWord(pwd: String) : Boolean {
        val pwdRegex = "^(?![0-9]+\$)(?![a-zA-Z]+\$)[0-9A-Za-z]{8,16}\$"
        return pwd.matches(pwdRegex.toRegex())
    }
    /**
     * 判断班级号(6位数字)
     */
    fun checkClassNum(num: String) : Boolean {
        val numRegex = "\\d{6}"
        return num.matches(numRegex.toRegex())
    }

}