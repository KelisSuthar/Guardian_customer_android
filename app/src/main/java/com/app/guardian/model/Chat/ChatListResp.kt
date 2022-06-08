package com.app.guardian.model.Chat

data class ChatListResp(
    val from_id: Int?=0,
    val id: Int?=0,
    val is_read: Int?=0,
    val message: String?="",
    val message_time: String?="",
    val to_id: Int?=0,
    var header_time: String?="",
    var is_header_show: Boolean?=false,
)