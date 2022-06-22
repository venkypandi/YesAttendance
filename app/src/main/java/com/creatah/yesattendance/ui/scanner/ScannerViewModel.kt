package com.creatah.yesattendance.ui.scanner

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.creatah.yesattendance.data.repository.ScannerRepositoryImpl
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ScannerViewModel @Inject constructor(private val scannerRepository: ScannerRepositoryImpl) :
    ViewModel() {

    val responseValue = scannerRepository.responseValue

    fun getMemberData(qrValue: String) {
        viewModelScope.launch {
            scannerRepository.getMemberData(qrValue = qrValue)
        }
    }
}