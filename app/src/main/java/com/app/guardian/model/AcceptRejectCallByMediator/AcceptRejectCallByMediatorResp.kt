package com.app.guardian.model.AcceptRejectCallByMediator

data class AcceptRejectCallByMediatorResp(
    val assign_by: Int? = 0,
    val call_status: Int? = 0,
    val call_type: String? = "",
    val conversation_id: Int? = 0,
    val created_at: String? = "",
    val duration_min: String? = "",
    val from_id: Int? = 0,
    val from_role: String? = "",
    val from_time: String? = "",
    val id: Int? = 0,
    val is_immediate_joining: Int? = 0,
    val note: String? = "",
    val request_datetime: String? = "",
    val request_status: String? = "",
    val room_id: String? = "",
    val schedule_datetime: String? = "",
    val to_id: Int? = 0,
    val to_role: String? = "",
    val to_time: String? = "",
    val updated_at: String? = "",
    val url: String? = ""
)