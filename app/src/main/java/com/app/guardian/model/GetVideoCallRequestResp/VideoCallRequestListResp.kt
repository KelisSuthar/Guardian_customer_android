package com.app.guardian.model.GetVideoCallRequestResp

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class GetVideoCallRequestListResp(
    val call_type: String? = "",
    val from_id: Int? = 0,
    val from_role: String? = "",
    val id: Int? = 0,
    val is_immediate_joining: Int? = 0,
//    val note: String,
    val request_datetime: String? = "",
    val request_status: String? = "",
    val room_id: String? = "",
    val schedule_datetime: String? = "",
    val to_id: Int? = 0,
    val to_role: String? = "",
    val url: String? = "",
    val user_detail: UserDetail
) : Parcelable

@Parcelize
data class UserDetail(
    val dialing_code: String? = "",
    val email: String? = "",
    val full_name: String? = "",
    val id: Int? = 0,
    val phone: String? = "",
    val profile_avatar: String? = "",
    val user_role: String? = ""
) : Parcelable