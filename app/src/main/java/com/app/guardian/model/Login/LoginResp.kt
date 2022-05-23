package com.app.guardian.model.Login

data class LoginResp(
    val token: String,
    val user: User
)
data class User(
    val created_at: String?="",
    val deleted_at: String?="",
    val email: String?="",
    val email_verified_at: String?="",
    val fp_otp: String?="",
    val fp_status: String?="",
    val fp_str: String?="",
    val full_name: String?="",
    val id: Int?=0,
    val licence_no: String?="",
    val login_otp: String?="",
    val office_phone: String?="",
    val phone: String?="",
    val is_subscribe: Int?=0,
    val postal_code: String?="",
    val profile_avatar: String?="",
    val dialing_code: String?="",
    val office_dialing_code: String?="",
    val specialization: String?="",
    val state: String?="",
    val status: String?="",
    val updated_at: String?="",
    val user_role: String?="",
    val vehicle_no: String?="",
    val years_of_experience: String?="",
    val changephone_otp: String?="",
    val description: String?="",
    val plan_id: String?="",
    val plan_expiry_date: String?="",
)
