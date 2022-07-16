package com.app.guardian.model.LawyerBySpecialization

data class LawyerBySpecializationResp(
    val description: String?="",
    val dialing_code: String?="",
    val firebase_uid: String?="",
    val full_name: String?="",
    val email: String?="",
    val last_seen: String?="",
    val id: Int?=0,
    val is_online: Int?=0,
    val phone: String?="",
    val profile_avatar: String?="",
    val specialization: String?="",
    val years_of_experience: String?="",
    val availability_time: String?="",
)
