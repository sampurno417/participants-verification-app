package com.example.myapplication

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.google.zxing.integration.android.IntentIntegrator
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MainActivity : AppCompatActivity() {

    private lateinit var editTextStudentId: EditText
    private lateinit var buttonVerify: Button
    private lateinit var buttonScan: Button
    private lateinit var textViewResult: TextView
    private lateinit var progressBar: ProgressBar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        editTextStudentId = findViewById(R.id.editTextStudentId)
        buttonVerify = findViewById(R.id.buttonVerify)
        buttonScan = findViewById(R.id.buttonScan)
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

        buttonScan.setOnClickListener {
            initiateQRCodeScan()
        }
    }

    private fun initiateQRCodeScan() {
        val integrator = IntentIntegrator(this).apply {
            setDesiredBarcodeFormats(IntentIntegrator.QR_CODE)
            setPrompt("Scan a QR Code")
            setCameraId(0)  // Use default camera
            setBeepEnabled(false)
            setBarcodeImageEnabled(true)
            setOrientationLocked(true)  // Lock orientation
            setRequestCode(REQUEST_CODE_QR_SCAN)
        }
        integrator.initiateScan()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == REQUEST_CODE_QR_SCAN) {
            val result = IntentIntegrator.parseActivityResult(resultCode, data)
            if (result != null) {
                if (result.contents == null) {
                    textViewResult.text = "Cancelled"
                } else {
                    editTextStudentId.setText(result.contents)
                    verifyStudent(result.contents)
                }
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data)
        }
    }

    private fun verifyStudent(studentId: String) {
        progressBar.visibility = View.VISIBLE
        textViewResult.text = ""

        lifecycleScope.launch(Dispatchers.Main) {
            try {
                val response = withContext(Dispatchers.IO) {
                    ApiService.turnInStudent(studentId)
                }
                textViewResult.text = """
                    ${response.message}
                    Name: ${response.name ?: "N/A"}
                    Roll: ${response.roll ?: "N/A"}
                    Year: ${response.year ?: "N/A"}
                    College: ${response.college ?: "N/A"}
                """.trimIndent()
            } catch (e: Exception) {
                textViewResult.text = "Error: ${e.message}"
            } finally {
                progressBar.visibility = View.GONE
            }
        }
    }

    companion object {
        private const val REQUEST_CODE_QR_SCAN = 101
    }
}
