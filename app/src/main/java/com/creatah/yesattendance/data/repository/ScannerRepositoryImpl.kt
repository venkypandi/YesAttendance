package com.creatah.yesattendance.data.repository

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.creatah.yesattendance.MainActivity
import com.creatah.yesattendance.data.api.YesAttendanceApiService
import com.creatah.yesattendance.data.model.ErrorModel
import com.creatah.yesattendance.data.model.MemberListResponseModel
import com.creatah.yesattendance.data.model.ResponseModel
import com.creatah.yesattendance.utils.Resource
import com.google.gson.Gson
import javax.inject.Inject

class ScannerRepositoryImpl @Inject constructor(private val yesAttendanceApiService: YesAttendanceApiService) :
    ScannerRepository {

    private val _responseValue = MutableLiveData<Resource<ResponseModel>?>()
    val responseValue: LiveData<Resource<ResponseModel>?>
        get() = _responseValue

    private val _memberList = MutableLiveData<Resource<MemberListResponseModel>?>()
    val memberList: LiveData<Resource<MemberListResponseModel>?>
        get() = _memberList

    override suspend fun getMemberData(qrValue: String) {
        _responseValue.value = Resource.loading(null)

        if (!MainActivity.hasInternet) {
            _responseValue.value = Resource.error("No Internet Connection!", 500, null)
            return
        }

        try {
            val response = yesAttendanceApiService.getMemberData(qrValue = qrValue)
            if (response.isSuccessful) {
                _responseValue.value = Resource.success(response.body())
                Log.d("CallRepoS: ", response.body().toString())
            } else {
                if (response.code() == 400) {
                    val error = Gson().fromJson(
                        response.errorBody()!!.charStream(),
                        ErrorModel::class.java
                    )
                    error.message?.let { Log.d("CallRepo: ", it) }
                    _responseValue.value =
                        Resource.error(
                            error.message ?: "Something went wrong!",
                            response.code(),
                            null
                        )
                } else {
                    _responseValue.value = Resource.error(response.message(), response.code(), null)
                }
            }
        } catch (exception: Exception) {

            Log.d("Ex: ", exception.message!!)
            _responseValue.value =
                Resource.error("Error: Data Incorrect", 0, null)
        }
    }

    override suspend fun getMemberList() {

        _memberList.value = Resource.loading(null)

        if (!MainActivity.hasInternet) {
            _memberList.value = Resource.error("No Internet Connection!", 500, null)
            return
        }

        try {
            val response = yesAttendanceApiService.getMemberList()
            if (response.isSuccessful) {
                _memberList.value = Resource.success(response.body())
                Log.d("MemberList: ", response.body().toString())
            } else {
                if (response.code() == 400) {
                    val error = Gson().fromJson(
                        response.errorBody()!!.charStream(),
                        ErrorModel::class.java
                    )
                    error.message?.let { Log.d("MemberList: ", it) }
                    _memberList.value =
                        Resource.error(
                            error.message ?: "Something went wrong!",
                            response.code(),
                            null
                        )
                } else {
                    _memberList.value = Resource.error(response.message(), response.code(), null)
                }
            }
        }catch (exception: Exception) {
            _memberList.value =
                Resource.error("Error: Data Incorrect", 0, null)
        }

    }
}