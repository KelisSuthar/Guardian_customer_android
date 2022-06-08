package com.app.guardian.model.Chat

data class SendMessageResp(
    val conversation_id: String? = "",
    val created_at: String? = "",
    val device_type: String? = "",
    val from_id: Int? = 0,
    val id: Int? = 0,
    val is_read: Int? = 0,
    val message: String? = "",
    val message_time: String? = "",
    val message_type: String? = "",
    val to_id: Int? = 0,
    val updated_at: String? = ""
)
