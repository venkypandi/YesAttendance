package com.creatah.yesattendance.data.model


import com.google.gson.annotations.SerializedName

data class Member(
    @SerializedName("member_name")
    val memberName: String?,
    @SerializedName("qr_value")
    val qrValue: String?
)