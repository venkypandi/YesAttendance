package com.creatah.yesattendance.data.repository

interface ScannerRepository {

    suspend fun getMemberData(qrValue: String)

}