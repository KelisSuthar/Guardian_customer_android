package com.app.guardian.model.SendVideoCallReq

data class SendVideoCallReqResp(
    val assign_by: String,
    val call_type: String,
    val created_at: String,
    val from_id: Int,
    val from_role: String,
    val id: Int,
    val is_immediate_joining: String,
    val request_datetime: String,
    val request_status: String,
    val schedule_datetime: Any,
    val to_id: Int,
    val to_role: String,
    val updated_at: String
)