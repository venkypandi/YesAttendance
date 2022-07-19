package com.creatah.yesattendance.data.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.creatah.yesattendance.MainActivity
import com.creatah.yesattendance.data.api.YesAttendanceApiService
import com.creatah.yesattendance.data.model.ErrorModel
import com.creatah.yesattendance.data.model.ResponseModel
import com.creatah.yesattendance.utils.Resource
import com.google.gson.Gson
import javax.inject.Inject

class ScannerRepositoryImpl @Inject constructor(private val yesAttendanceApiService: YesAttendanceApiService) :
    ScannerRepository {

    private val _responseValue = MutableLiveData<Resource<ResponseModel>?>()
    val responseValue: LiveData<Resource<ResponseModel>?>
        get() = _responseValue

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
            } else {
                if (response.code() == 400) {
                    val error = Gson().fromJson(
                        response.errorBody()!!.charStream(),
                        ErrorModel::class.java
                    )
                    _responseValue.value =
                        Resource.error(error.message ?: "Something went wrong!", response.code(), null)
                } else {
                    _responseValue.value = Resource.error(response.message(), response.code(), null)
                }

            }
        } catch (exception: Exception) {
            _responseValue.value =
                Resource.error("Error: Data Incorrect", 0, null)
        }
    }
}