package com.app.guardian.model.Chat

data class ChatListResp(
    val chat_detail: List<ChatDetail>,
    val user_detail: UserDetail
)

data class ChatDetail(
    val from_id: Int?=0,
    val id: Int?=0,
    val is_read: Int?=0,
    val message: String?="",
    val message_time: String?="",
    val to_id: Int?=0,
    var is_header_show:Boolean?=false,
    var header_time:String?="",
)

data class UserDetail(
    val full_name: String?="",
    val id: Int?=0,
    val is_online: Int?=0,
    val last_seen: String?="",
    val profile_avatar: String?="",
    val user_role: String?=""
)