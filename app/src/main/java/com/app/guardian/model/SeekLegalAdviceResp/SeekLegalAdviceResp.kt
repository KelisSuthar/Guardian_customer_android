package com.app.guardian.model.SeekLegalAdviceResp

data class SeekLegalAdviceResp(
    val created_at: String,
    val description: String,
    val id: Int,
    val status: String,
    val title: String,
    val updated_at: String,
    val user_id: Int,
    val user_role: String
)