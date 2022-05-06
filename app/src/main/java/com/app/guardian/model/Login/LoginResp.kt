package com.app.guardian.model.Login

data class LoginResp(
    val token: String,
    val user: User
)
data class User(
    val created_at: String,
    val deleted_at: Any,
    val email: String,
    val email_verified_at: Any,
    val fp_otp: Any,
    val fp_status: String,
    val fp_str: Any,
    val full_name: String,
    val id: Int,
    val licence_no: Any,
    val login_otp: Any,
    val office_phone: Any,
    val phone: String,
    val postal_code: String,
    val profile_avatar: String,
    val specialization: Any,
    val state: String,
    val status: String,
    val updated_at: String,
    val user_role: String,
    val vehicle_no: Any,
    val years_of_experience: Any
)
