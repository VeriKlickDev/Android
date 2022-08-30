package com.domain.BaseModels

import com.google.gson.annotations.SerializedName

class ResponseChatFromToken(
        @SerializedName("iss"    ) var iss    : String? = null,
        @SerializedName("exp"    ) var exp    : Int?    = null,
        @SerializedName("jti"    ) var jti    : String? = null,
        @SerializedName("sub"    ) var sub    : String? = null,
        @SerializedName("grants" ) var grants : Grants? = Grants()
    )
data class Grants (

    @SerializedName("identity"     ) var identity    : String?      = null,
    @SerializedName("ip_messaging" ) var ipMessaging : IpMessaging? = IpMessaging()

)

data class IpMessaging (

    @SerializedName("service_sid" ) var serviceSid : String? = null,
    @SerializedName("endpoint_id" ) var endpointId : String? = null

)