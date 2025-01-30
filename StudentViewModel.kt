package com.example.myapplication

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class StudentViewModel : ViewModel() {
    private val _resultMessage = MutableStateFlow("")
    val resultMessage: StateFlow<String> = _resultMessage

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    fun verifyAndRegisterStudent(studentId: String) {
        viewModelScope.launch {
            _isLoading.value = true
            _resultMessage.value = try {
                val verifyResult = ApiService.instance.verifyStudent(studentId)
                if (verifyResult.exists) {
                    val turnInResult = ApiService.instance.turnInStudent(studentId)
                    if (turnInResult.alreadyRegistered) {
                        "Student already registered"
                    } else {
                        "Student registered successfully"
                    }
                } else {
                    "Student not found"
                }
            } catch (e: Exception) {
                "Error: ${e.message}"
            }
            _isLoading.value = false
        }
    }
}

