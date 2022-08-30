package com.app.wavespodcast.api.model.response

/**
 * Helper Data Class for Api's Output
*/

data class ApiResponse<T>(
    val success:Boolean,
    val message :String,
    val data:T?,
    val errorMessage: String
)

