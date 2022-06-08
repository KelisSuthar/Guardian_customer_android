package com.app.guardian.model.HomeBanners

data class UserHomeBannerResp(
    val bannerCollection: List<BannerCollection>,
    val is_online: Int,
    val last_seen: String
)
data class BannerCollection(
    val banner_avatar: String,
    val created_at: String,
    val end_date: String,
    val id: Int,
    val start_date: String,
    val status: String,
    val updated_at: String,
    val url: String,
    val user_id: Int,
    val user_role: String
)