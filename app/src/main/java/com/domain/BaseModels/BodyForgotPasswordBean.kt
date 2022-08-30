package com.domain.BaseModels

data class BodyForgotPasswordBean(val email:String?)

data class ResponseForgotPassword(val success:Boolean,val message:String?, val data:String ?,val errorMessage:String? )

