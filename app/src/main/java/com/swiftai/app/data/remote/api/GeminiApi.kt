package com.swiftai.app.data.remote.api

import android.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject
import java.util.concurrent.TimeUnit
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class GeminiApi @Inject constructor() {
    private val client = OkHttpClient.Builder()
        .connectTimeout(60, TimeUnit.SECONDS)
        .readTimeout(60, TimeUnit.SECONDS)
        .writeTimeout(60, TimeUnit.SECONDS)
        .build()

    private val baseUrl = "https://shawniii-swiftai-api.hf.space"

    suspend fun sendMessage(prompt: String): Result<String> = withContext(Dispatchers.IO) {
        try {
            Log.d("GeminiApi", "Sending request to: $baseUrl/api/gemini")
            val url = "$baseUrl/api/gemini"
            val json = JSONObject().apply {
                put("prompt", prompt)
                put("model", "gemini-2.5-pro")
            }

            val request = Request.Builder()
                .url(url)
                .post(json.toString().toRequestBody("application/json".toMediaType()))
                .build()

            val response = client.newCall(request).execute()
            val body = response.body?.string()

            Log.d("GeminiApi", "Response code: ${response.code}")
            Log.d("GeminiApi", "Response body: $body")

            if (!response.isSuccessful) {
                Log.e("GeminiApi", "HTTP error: ${response.code}")
                return@withContext Result.failure(Exception("Server error: ${response.code}"))
            }

            if (body.isNullOrBlank()) {
                Log.e("GeminiApi", "Blank response body!")
                return@withContext Result.failure(Exception("No response from server"))
            }

            val result = JSONObject(body)
            val out = result.optString("response", "")

            Log.d("GeminiApi", "Parsed response: $out")

            if (out.isNotBlank()) {
                Result.success(out)
            } else {
                Result.failure(Exception("Empty response from API"))
            }
        } catch (e: Exception) {
            Log.e("GeminiApi", "Exception: ${e.message}", e)
            Result.failure(e)
        }
    }
}
