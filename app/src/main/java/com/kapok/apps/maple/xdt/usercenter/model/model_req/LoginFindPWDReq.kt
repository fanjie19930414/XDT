package com.kapok.apps.maple.xdt.usercenter.model.model_req

data class LoginFindPWDReq(val code: String, val password: String, val phone: String) {
}