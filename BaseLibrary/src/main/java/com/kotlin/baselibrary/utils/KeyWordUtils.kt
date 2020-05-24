package com.kotlin.baselibrary.utils


import android.text.SpannableString
import android.text.Spanned
import android.text.TextUtils
import android.text.style.ForegroundColorSpan

import java.util.regex.Pattern

/**
 * 文字变色工具类
 */
object KeyWordUtils {

    /**
     * 关键字高亮变色
     *
     * @param color   变化的色值
     * @param text    文字
     * @param keyword 文字中的关键字
     * @return 结果SpannableString
     */
    fun matcherSearchTitle(color: Int, text: String, keyword: String): SpannableString {
        var keyText = text
        var keyWord = keyword
        val s = SpannableString(keyText)
        keyWord = escapeExprSpecialWord(keyWord)
        keyText = escapeExprSpecialWord(keyText)
        if (keyText.contains(keyWord) && !TextUtils.isEmpty(keyWord)) {
            try {
                val p = Pattern.compile(keyWord)
                val m = p.matcher(s)
                while (m.find()) {
                    val start = m.start()
                    val end = m.end()
                    s.setSpan(ForegroundColorSpan(color), start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
                }
            } catch (ignored: Exception) {
            }

        }
        return s
    }

    /**
     * 转义正则特殊字符 （$()*+.[]?\^{},|）
     *
     * @param keyword
     * @return keyword
     */
    private fun escapeExprSpecialWord(keyword: String): String {
        var keyword = keyword
        if (!TextUtils.isEmpty(keyword)) {
            val fbsArr = arrayOf("\\", "$", "(", ")", "*", "+", ".", "[", "]", "?", "^", "{", "}", "|")
            for (key in fbsArr) {
                if (keyword.contains(key)) {
                    keyword = keyword.replace(key, "\\" + key)
                }
            }
        }
        return keyword
    }
}
