package com.app.guardian.model.SubscriptionPlan

data class SubscriptionPlanResp(
    val created_at: String?="",
    val deleted_at: String?="",
    val features: String?="",
    val id: Int?=0,
    val offer_detail: String?="",
    val plan_duration: String?="",
    val plan_type: String?="",
    val pricing: String?="",
    val status: String?="",
    val updated_at: String?=""
)