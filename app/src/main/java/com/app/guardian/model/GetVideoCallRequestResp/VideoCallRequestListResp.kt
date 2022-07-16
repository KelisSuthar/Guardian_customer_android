package com.app.guardian.model.GetVideoCallRequestResp

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class VideoCallRequestListResp(
    val availability_time: String,
    val call_type: String,
    val description: String,
    val dialing_code: String,
    val email: String,
    val full_name: String,
    val id: Int,
    val is_immediate_joining: Int,
    val is_online: Int,
    val last_seen: String,
    val note: String,
    val phone: String,
    val profile_avatar: String,
    val state: String? = "",
    val request_datetime: String,
    val request_status: String,
    val my_status: String,
    val room_id: String,
    val schedule_datetime: String,
    val specialization: String,
    val url: String,
    val user_id: Int,
    val user_role: String,
    val years_of_experience: String
) : Parcelable