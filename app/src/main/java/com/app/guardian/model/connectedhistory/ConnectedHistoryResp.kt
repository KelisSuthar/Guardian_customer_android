package com.app.guardian.model.connectedhistory

data class ConnectedHistoryResp(
    val dialing_code: String? = "",
    val email: String? = "",
    val from_time: String? = "",
    val full_name: String? = "",
    val phone: String? = "",
    val profile_avatar: String? = "",
    val specialization: String? = "",
    val to_time: String? = ""
)