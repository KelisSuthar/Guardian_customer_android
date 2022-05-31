package com.app.guardian.model.connectedhistory

data class ConnectedHistoryResp(
    val dialing_code: String? = "",
    val email: String? = "",
    val from_time: String? = "",
    val full_name: String? = "",
    val phone: String? = "",
    val profile_avatar: String? = "",
    val specialization: String? = "",
    val to_time: String? = "",
    val user_role: String? = "",
    val office_phone: String? = "",
    val state: String? = "",
    val postal_code: String? = "",
    val licence_no: String? = "",
    val description: String? = "",
    val years_of_experience: String? = "",
    val vehicle_no: String? = "",
    val id: Int? = 0,
)