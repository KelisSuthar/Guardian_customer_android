package com.app.guardian.model.Notification

data class NotificationResp(
    val created_at: String? = "",
    val data_obj: String? = "",
    val id: Int? = 0,
    val is_read: Int? = 0,
    val message: String? = "",
    val notification_type: String? = "",
    val receiver_id: Int? = 0,
    val send_by: String? = "",
    val send_to: String? = "",
    val sender_id: Int? = 0,
    val title: String? = "",
    val updated_at: String? = "",
    val deleted_at: String? = "",
    )