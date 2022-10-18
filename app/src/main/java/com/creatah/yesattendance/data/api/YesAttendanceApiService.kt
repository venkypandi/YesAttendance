package com.creatah.yesattendance.data.api

import com.creatah.yesattendance.data.model.MemberListResponseModel
import com.creatah.yesattendance.data.model.ResponseModel
import retrofit2.Response
import retrofit2.http.*

interface YesAttendanceApiService {

    @POST("updateMemberAttendance")
    @FormUrlEncoded
    suspend fun getMemberData(@Field("qr_value", encoded = true) qrValue: String): Response<ResponseModel>

    @GET("viewAllMemberList")
    suspend fun getMemberList(): Response<MemberListResponseModel>

}