package com.creatah.yesattendance.data.model


import com.google.gson.annotations.SerializedName

data class ErrorModel(
    @SerializedName("error")
    val error: Boolean?,
    @SerializedName("message")
    val message: String?
)