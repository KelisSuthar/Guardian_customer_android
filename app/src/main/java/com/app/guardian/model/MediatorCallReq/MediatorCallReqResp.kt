package com.app.guardian.model.MediatorCallReq

data class MediatorCallReqResp(
    val assign_by: String,
    val call_type: String,
    val created_at: String,
    val from_id: Int,
    val from_role: String,
    val id: Int,
    val is_immediate_joining: Int,
    val request_datetime: String,
    val schedule_datetime: String,
    val to_id: Any,
    val to_role: String,
    val updated_at: String
)