package com.creatah.yesattendance.data.model


import com.google.gson.annotations.SerializedName

data class MemberListResponseModel(
    @SerializedName("error")
    val error: Boolean?,
    @SerializedName("member_list")
    val memberList: List<Member>?
)