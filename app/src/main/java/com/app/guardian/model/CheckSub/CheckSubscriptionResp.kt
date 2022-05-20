package com.app.guardian.model.CheckSub

data class CheckSubscriptionResp(
    val id: Int,
    val is_subscribe: Int,
    val plan_expiry_date: Any,
    val user_role: String
)