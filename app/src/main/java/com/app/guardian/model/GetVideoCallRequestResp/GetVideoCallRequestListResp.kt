package com.app.guardian.model.GetVideoCallRequestResp

data class GetVideoCallRequestListResp(
    val call_type: String,
    val from_id: Int,
    val from_role: String,
    val id: Int,
    val is_immediate_joining: Int,
    val note: Any,
    val request_datetime: String,
    val request_status: String,
    val room_id: Any,
    val schedule_datetime: Any,
    val to_id: Int,
    val to_role: String,
    val url: Any,
    val user_detail: UserDetail
)
data class UserDetail(
    val dialing_code: String,
    val email: String,
    val full_name: String,
    val id: Int,
    val phone: String,
    val profile_avatar: String,
    val user_role: String
)