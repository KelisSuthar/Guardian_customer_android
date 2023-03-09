package com.app.guardian.model.Editprofile

data class UserDetailsResp(
    val created_at: String? = "",
    val deleted_at: String? = "",
    val email: String? = "",
    val fp_status: String? = "",
    val full_name: String? = "",
    val id: Int? = 0,
    val licence_no: String? = "",
    val office_phone: String? = "",
    val office_dialing_code: String? = "1",
    val dialing_code: String? = "1",
    val phone: String? = "",
    val plan_expiry_date: String? = "",
    val postal_code: String? = "",
    val profile_avatar: String? = "",
    val specialization: String? = "",
    val state: String? = "",
    val status: String? = "",
    val updated_at: String? = "",
    val user_doc: List<UserDoc>,
    val user_role: String? = "",
    val vehicle_no: String? = "",
    val changephone_otp: String? = "",
    val description: String? = "",
    val plan_id: Int? = 0,
    val is_expired: Int? = 0,
    val is_subscribe: Int? = 0,
    val is_online: Int? = 0,
    val last_seen: String? = "",
    val is_autoupload: Int? = 0,
    val years_of_experience: String? = "",
    val availability_time: String? = "",
    val payment_detail: String? = "",
    val pay_receipt: String? = "",
    val shared_secret: String? = "",

    )

data class UserDoc(
    val created_at: String? = "",
    val deleted_at: String? = "",
    val document: String? = "",
    val id: Int? = 0,
    val status: String? = "",
    val updated_at: String? = "",
    val user_id: Int? = 0
)