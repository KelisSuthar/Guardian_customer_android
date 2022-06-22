package com.app.guardian.model.scheduleRequestedVideoCall

data class ScheduleRequestedVideoCallResp(
    val assign_by: Any,
    val call_status: Int,
    val call_type: String,
    val conversation_id: Any,
    val created_at: String,
    val duration_min: Any,
    val from_id: Int,
    val from_role: String,
    val from_time: String,
    val id: Int,
    val is_immediate_joining: Int,
    val note: String,
    val request_datetime: Any,
    val request_status: String,
    val room_id: String,
    val schedule_datetime: String,
    val to_id: Int,
    val to_role: String,
    val to_time: String,
    val updated_at: String,
    val url: String
)