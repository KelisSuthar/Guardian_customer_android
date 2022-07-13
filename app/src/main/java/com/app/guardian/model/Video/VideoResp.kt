package com.app.guardian.model.Video

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class VideoResp(
    val id: String,
    val title: String,
    val path: String,
    var isSelected:Boolean?=false,
    val is_Checkable:Boolean?=false,
) : Parcelable