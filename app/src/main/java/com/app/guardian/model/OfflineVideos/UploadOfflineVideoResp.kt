package com.app.guardian.model.OfflineVideos

data class UploadOfflineVideoResp(
    val created_at: String,
    val id: Int,
    val updated_at: String,
    val user_id: Int,
    val video_type: String,
    val video_url: String
)