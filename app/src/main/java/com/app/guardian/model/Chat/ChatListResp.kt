package com.app.guardian.model.Chat

data class ChatListResp(
    val from_id: Int,
    val id: Int,
    val is_read: Int,
    val message: String,
    val message_time: String,
    val to_id: Int
)