package com.app.guardian.model.sendRequestVirtualWitness

data class SendRequestVirtualWitnessResp(
    val created_at: String,
    val id: Int,
    val is_immediate_joining: Int,
    val request_datetime: String,
    val schedule_datetime: Any,
    val support_group_id: Any,
    val updated_at: String,
    val user_id: Int
)