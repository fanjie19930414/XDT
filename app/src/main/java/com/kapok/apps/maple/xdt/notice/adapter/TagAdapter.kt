package com.kapok.apps.maple.xdt.notice.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.widget.TextView
import com.kapok.apps.maple.xdt.R
import com.kapok.apps.maple.xdt.homework.bean.TeacherInClasses
import com.zhy.view.flowlayout.FlowLayout
import com.zhy.view.flowlayout.TagAdapter

class TagAdapter(val context: Context, chooseClassListInfo: ArrayList<TeacherInClasses>) :
    TagAdapter<TeacherInClasses>(chooseClassListInfo) {
    override fun getView(parent: FlowLayout?, position: Int, t: TeacherInClasses): View {
        val textView = LayoutInflater.from(context).inflate(
            R.layout.item_flowlayout_textview_notice,
            parent,
            false
        ) as TextView
        textView.text =
            t.grade + t.className + "班" + (t.chooseStudentNum + t.chooseTeacherNum) + "人"
        return textView
    }
}