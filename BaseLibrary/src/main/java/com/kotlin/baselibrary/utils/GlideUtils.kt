package com.kotlin.baselibrary.utils

import android.content.Context
import android.widget.ImageView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.kotlin.baselibrary.R


/**
 * @desciption: Glide工具类
 */
object GlideUtils {

    fun loadImage(context: Context, url: String, imageView: ImageView) {
        Glide.with(context).load(url).apply(RequestOptions().fitCenter()).into(imageView)
    }

    fun loadImage(context: Context, url: String, options: RequestOptions, imageView: ImageView) {
        Glide.with(context).load(url).apply(options).into(imageView)
    }

    /**
     * 当fragment或者activity失去焦点或者destroyed的时候，Glide会自动停止加载相关资源，确保资源不会被浪费
     */
    fun loadUrlImage(context: Context, url: String, imageView: ImageView) {
        Glide.with(context)
                .load(url)
                .apply(RequestOptions()
                        .placeholder(R.drawable.yukee)
                        .error(R.drawable.yukee)
                        .centerCrop())
                .into(imageView)
    }

    // 加载头像
    fun loadUrlHead(context: Context, url: String, imageView: ImageView) {
        Glide.with(context)
            .load(url)
            .apply(RequestOptions()
                .placeholder(R.drawable.def_head_boy)
                .error(R.drawable.def_head_boy)
                .centerCrop())
            .into(imageView)
    }

}