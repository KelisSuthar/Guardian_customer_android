package com.app.guardian.model

import android.os.Parcel
import android.os.Parcelable
import com.google.gson.annotations.SerializedName

data class CommonResponse(
        @SerializedName("status")
        var status: Boolean?,
        @SerializedName("message")
        var message: String?
) : Parcelable {
        constructor(parcel: Parcel) : this(
                parcel.readValue(Int::class.java.classLoader) as? Boolean,
                parcel.readString()
        ) {
        }

        override fun writeToParcel(parcel: Parcel, flags: Int) {
                parcel.writeValue(status)
                parcel.writeString(message)
        }

        override fun describeContents(): Int {
                return 0
        }

        companion object CREATOR : Parcelable.Creator<CommonResponse> {
                override fun createFromParcel(parcel: Parcel): CommonResponse {
                        return CommonResponse(parcel)
                }

                override fun newArray(size: Int): Array<CommonResponse?> {
                        return arrayOfNulls(size)
                }
        }
}


