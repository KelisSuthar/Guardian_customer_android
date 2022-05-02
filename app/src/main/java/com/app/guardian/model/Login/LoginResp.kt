package com.app.guardian.model.Login

data class LoginResp(
    val accesstoken: String,
    val available_drink_token: Int,
    val available_meal_plan: Int,
    val email: String,
    val end_free_trial: String,
    val fullname: String,
    val id: Int,
    val is_freetrial: Int,
    val is_member: Int,
    val is_purched: Int,
    val login_type: String,
    val member_email: String,
    val member_fullname: String,
    val member_password: String,
    val number_verify: Int,
    val password: String,
    val phone_number: String,
    val profile_image: String,
    val referral_by: String,
    val referral_code: String,
    val reward_balance: Int,
    val social_id: String,
    val start_free_trial: String,
    val isProfileCreated:Int,
    val stripe_cust_id:String,
    val status:String,



)

