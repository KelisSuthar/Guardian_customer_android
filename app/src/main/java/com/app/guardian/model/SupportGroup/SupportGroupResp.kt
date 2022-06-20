package com.app.guardian.model.SupportGroup

data class SupportGroupResp(
    val created_at: Any,
    val id: Int,
    val title: String,
    val updated_at: Any,
    val isSelected:Boolean? = false,
)