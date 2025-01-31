package com.example.myapplication

import android.util.Log
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Body
import retrofit2.http.PATCH

interface ApiService {

    @PATCH("turnin")
    suspend fun turnInStudent(@Body request: TurnInRequest): ApiResponse

    companion object {
        private const val BASE_URL = "https://mongodb-to-sheet.vercel.app/"
        private const val TAG = "ApiService"

        private val loggingInterceptor = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }

        private val client = OkHttpClient.Builder()
            .addInterceptor(loggingInterceptor)
            .build()

        private val retrofit = Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        private val service: ApiService = retrofit.create(ApiService::class.java)

        suspend fun turnInStudent(id: String): ApiResponse {
            return try {
                Log.d(TAG, "Turning in student with ID: $id")
                val response = service.turnInStudent(TurnInRequest(id))
                Log.d(TAG, "Turn in response: $response")
                response
            } catch (e: Exception) {
                Log.e(TAG, "Error turning in student: ${e.message}", e)
                ApiResponse("Network error: ${e.message}", null, null, null, null)
            }
        }
    }
}

// Request model
data class TurnInRequest(val id: String)

// Response model
data class ApiResponse(
    val message: String,
    val name: String?,
    val roll: String?,
    val year: String?,
    val college: String?
)
