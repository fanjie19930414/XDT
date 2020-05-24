package com.kapok.apps.maple.xdt.usercenter.bean

/**
 * 手机验证码登录接口Bean
 */
data class LoginByCodeBean(val token : String?,val userId : Int?,val realName : String?,val avatar: String?,val identityType: String?)