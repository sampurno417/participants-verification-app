package com.example.studentverificationapp

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query

interface ApiService {
    @GET("verify")
    suspend fun verifyStudent(@Query("id") id: String): VerifyResponse

    @GET("turnin")
    suspend fun turnInStudent(@Query("id") id: String): TurnInResponse

    companion object {
        private const val BASE_URL = "https://mongodb-to-sheet.vercel.app/"

        val instance: ApiService by lazy {
            val retrofit = Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build()

            retrofit.create(ApiService::class.java)
        }
    }
}

data class VerifyResponse(val exists: Boolean)
data class TurnInResponse(val alreadyRegistered: Boolean)

