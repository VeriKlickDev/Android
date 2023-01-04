package com.domain.BaseModels

import com.google.gson.annotations.SerializedName


data class LoginResponseBean (

    @SerializedName("success"      ) var success      : Boolean? = null,
    @SerializedName("message"      ) var message      : String?  = null,
    @SerializedName("data"         ) var data         : LoginData?    = LoginData(),
    @SerializedName("errorMessage" ) var errorMessage : String?  = null

)

data class LoginData (
    @SerializedName("access_token" ) var accessToken : String? = null,
    @SerializedName("Expiration"   ) var expiration  : Int?    = null,
    @SerializedName("Email"        ) var email       : String? = null,
    @SerializedName("UserId"       ) var userId      : Int?    = null,
    @SerializedName("Role"         ) var role        : String? = null

)

data class BodyLoginBean(val userName:String,val password:String,val isweb:Boolean=false,val RecruiterEmail:String)



data class ResponseJWTTokenLogin (
    val Username : String,
    val Email : String,
    val Name : String,
    val CreatedBy : Int,
    val permissionId : Int,
    val CompName : String,
    val Website : String,
    val Phone : String,
    val CreatedDate : String,
    val PasswordUpdateOn : String,
    val SubscriberId : Int,
    //val http://schemas.microsoft.com/ws/2008/06/identity/claims/role : String,
    val Role : String,
    val UserId : Int,
    //val Nbf : Int,
    val nbf : Int,
    val exp : Int,
    //val Iss : String,
    val iss : String,
    val Aud : String
)
