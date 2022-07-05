package com.app.guardian.model.SupportGroup

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class SupportGroupResp(
    val created_at: String?="",
    val id: Int?=0,
    val title: String?="",
    val updated_at: String?="",
    val isSelected:Boolean? = false,
) : Parcelable