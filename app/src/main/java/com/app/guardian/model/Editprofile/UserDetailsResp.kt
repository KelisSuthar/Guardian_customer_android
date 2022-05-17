package com.app.guardian.model.Editprofile

data class UserDetailsResp(
    val created_at: String,
    val deleted_at: Any,
    val email: String,
    val fp_status: String,
    val full_name: String,
    val id: Int,
    val licence_no: Any,
    val office_phone: Any,
    val phone: String,
    val plan_expiry_date: Any,
    val postal_code: String,
    val profile_avatar: String,
    val specialization: Any,
    val state: String,
    val status: String,
    val updated_at: String,
    val user_doc: List<UserDoc>,
    val user_role: String,
    val vehicle_no: Any,
    val years_of_experience: Any
)
data class UserDoc(
    val created_at: String,
    val deleted_at: Any,
    val document: String,
    val id: Int,
    val status: String,
    val updated_at: String,
    val user_id: Int
)