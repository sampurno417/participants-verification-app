package com.example.studentverificationapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MaterialTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    StudentVerificationScreen()
                }
            }
        }
    }
}

@Composable
fun StudentVerificationScreen() {
    var studentId by remember { mutableStateOf("") }
    var resultMessage by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }
    val coroutineScope = rememberCoroutineScope()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        TextField(
            value = studentId,
            onValueChange = { studentId = it },
            label = { Text("Enter Student ID") },
            modifier = Modifier.fillMaxWidth()
        )
        
        Spacer(modifier = Modifier.height(16.dp))
        
        Button(
            onClick = {
                coroutineScope.launch {
                    isLoading = true
                    resultMessage = try {
                        val verifyResult = ApiService.verifyStudent(studentId)
                        if (verifyResult.exists) {
                            val turnInResult = ApiService.turnInStudent(studentId)
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
                    isLoading = false
                }
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Verify and Register")
        }
        
        Spacer(modifier = Modifier.height(16.dp))
        
        if (isLoading) {
            CircularProgressIndicator()
        } else {
            Text(resultMessage)
        }
    }
}

