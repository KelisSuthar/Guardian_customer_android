package com.app.guardian.model.LawyerBySpecialization

data class LawyerBySpecializationResp(
    val description: Any,
    val dialing_code: String,
    val firebase_uid: String,
    val full_name: String,
    val id: Int,
    val phone: String,
    val profile_avatar: String,
    val specialization: String,
    val years_of_experience: Any
)