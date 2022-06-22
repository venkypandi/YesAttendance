package com.creatah.yesattendance.data.api

import com.creatah.yesattendance.data.model.ResponseModel
import retrofit2.Response
import retrofit2.http.Field
import retrofit2.http.FieldMap
import retrofit2.http.FormUrlEncoded
import retrofit2.http.POST

interface YesAttendanceApiService {

    @POST("updateMemberAttendance")
    @FormUrlEncoded
    suspend fun getMemberData(@Field("qr_value") qrValue: String): Response<ResponseModel>

}