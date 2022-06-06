package com.app.guardian.model.Chat

data class SendMessageResp(
    val conversation_id: String,
    val created_at: String,
    val device_type: String,
    val from_id: Int,
    val id: Int,
    val is_read: Int,
    val message: String,
    val message_time: String,
    val message_type: String,
    val to_id: String,
    val updated_at: String
)