package com.app.guardian.model.SupportGroup

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class MultipleCallResp(
    val time: String? = "",
    val id: Int? = 0,
) : Parcelable