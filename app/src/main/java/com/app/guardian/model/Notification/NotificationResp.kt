package com.app.guardian.model.Notification

data class NotificationResp(
    val created_at: String,
    val data_obj: String,
    val id: Int,
    val is_read: Int,
    val message: String,
    val notification_type: String,
    val receiver_id: Int,
    val send_by: String,
    val send_to: String,
    val sender_id: Int,
    val title: String,
    val updated_at: String
)