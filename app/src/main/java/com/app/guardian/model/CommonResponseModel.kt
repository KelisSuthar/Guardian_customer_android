package com.app.guardian.model

import com.google.gson.annotations.SerializedName

data class CommonResponseModel<T>(
        @SerializedName("status")
        var status: Boolean,
        @SerializedName("message")
        var message: String? = null,
        @SerializedName("data")
        var data: T? = null
)

data class RequestState<T>(
        val error: ApiError? = null,
        val progress: Boolean = false,
        val apiResponse: CommonResponseModel<T>? = null
)


/**
 * @errorState error state defined in the Config.kt class
 * you can set CUSTOM_ERROR, NETWORK_ERROR
 * In case of CUSTOM_ERROR, you have to set customMessage also
 */
data class ApiError(val errorState: String, val customMessage: String?)
