package com.domain.BaseModels

import com.google.gson.annotations.SerializedName

data class ResponseCountryName (

    @SerializedName("SortName"    ) var SortName    : String?  = null,
    @SerializedName("PhoneCode"   ) var PhoneCode   : Int?     = null,
    @SerializedName("Nationality" ) var Nationality : String?  = null,
    @SerializedName("IsActive"    ) var IsActive    : Boolean? = null,
    @SerializedName("Id"          ) var Id          : Int?     = null,
    @SerializedName("Name"        ) var Name        : String?  = null

)

data class ResponseCountryCode (

    @SerializedName("Id"          ) var Id          : Int?    = null,
    @SerializedName("Iso"         ) var Iso         : String? = null,
    @SerializedName("Iso3"        ) var Iso3        : String? = null,
    @SerializedName("PhoneCode"   ) var PhoneCode   : Int?    = null,
    @SerializedName("Name"        ) var Name        : String? = null,
    @SerializedName("codedisplay" ) var codedisplay : String? = null

)

data class ResponseNationality (

    @SerializedName("Id"          ) var Id          : Int?    = null,
    @SerializedName("nationality" ) var nationality : String? = null

)

data class ResponseState (

    @SerializedName("id"        ) var id        : Int?    = null,
    @SerializedName("StateName" ) var StateName : String? = null,
    @SerializedName("Country"   ) var Country   : Int?    = null,
    @SerializedName("Status"    ) var Status    : Int?    = null,
    @SerializedName("Shortname" ) var Shortname : String? = null

)

data class ResponseCity (

    @SerializedName("CityName" ) var CityName : String? = null,
    @SerializedName("Code"     ) var Code     : String? = null,
    @SerializedName("Id"       ) var Id       : Int?    = null,
    @SerializedName("Name"     ) var Name     : String? = null

)

data class ModelJobType (

    @SerializedName("id"       ) var id       : String?  = null,
    @SerializedName("name"     ) var name     : String?  = null,
    @SerializedName("selected" ) var selected : Boolean? = null

)