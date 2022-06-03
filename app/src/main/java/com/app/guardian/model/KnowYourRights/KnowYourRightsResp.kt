package com.app.guardian.model.KnowYourRights

data class KnowYourRightsResp(
    val city: String? = "",
    val code: String? = "",
    val created_at: String? = "",
    val description: String? = "",
    val id: Int? = 0,
    val location: String? = "",
    val state: String? = "",
    val status: String? = "",
    val title: String? = "",
    val updated_at: String? = "",
    val user_role: String? = "",
    var is_selected: Boolean? = false
)