package com.creatah.yesattendance.data.model


import com.google.gson.annotations.SerializedName

data class ResponseModel(
    @SerializedName("date_time")
    val dateTime: String?,
    @SerializedName("error")
    val error: Boolean,
    @SerializedName("error_code")
    val errorCode: Int?,
    @SerializedName("member_name")
    val memberName: String?,
    @SerializedName("message")
    val message: String?,
    @SerializedName("token_number")
    val tokenNumber: Int?
)