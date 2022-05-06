package com.app.guardian.model.SignUp

data class SignupResp(
    val created_at: String,
    val email: String,
    val full_name: String,
    val id: Int,
    val phone: String,
    val postal_code: String,
    val profile_avatar: String,
    val state: String,
    val token: String,
    val updated_at: String,
    val user_role: String
)