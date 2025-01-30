package com.example.myapplication

import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MainActivity : AppCompatActivity() {

    private lateinit var editTextStudentId: EditText
    private lateinit var buttonVerify: Button
    private lateinit var textViewResult: TextView
    private lateinit var progressBar: ProgressBar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        editTextStudentId = findViewById(R.id.editTextStudentId)
        buttonVerify = findViewById(R.id.buttonVerify)
        textViewResult = findViewById(R.id.textViewResult)
        progressBar = findViewById(R.id.progressBar)

        buttonVerify.setOnClickListener {
            val studentId = editTextStudentId.text.toString()
            if (studentId.isNotEmpty()) {
                verifyStudent(studentId)
            } else {
                textViewResult.text = "Please enter a student ID"
            }
        }
    }

    private fun verifyStudent(studentId: String) {
        progressBar.visibility = View.VISIBLE
        textViewResult.text = ""

        lifecycleScope.launch(Dispatchers.Main) {
            try {
                val responseMessage = withContext(Dispatchers.IO) {
                    ApiService.turnInStudent(studentId)
                }
                textViewResult.text = responseMessage
            } catch (e: Exception) {
                textViewResult.text = "Error: ${e.message}"
            } finally {
                progressBar.visibility = View.GONE
            }
        }
    }
}
