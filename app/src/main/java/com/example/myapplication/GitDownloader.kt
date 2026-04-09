package com.example.myapplication
import okhttp3.OkHttpClient
import okhttp3.Request
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.IOException


class GitDownloader {

    private val githubToken = BuildConfig.GITHUB_TOKEN
    val client = OkHttpClient.Builder()
        .addInterceptor { chain ->
            val request = chain.request().newBuilder()
                .header("Authorization", "Bearer $githubToken")
                .header("User-Agent", "AndroidApp/1.0")
                .build()
            chain.proceed(request)
        }
        .build()
    suspend fun loadTextFile(url: String): Result<String> = withContext(Dispatchers.IO) {
        try {
            val request = Request.Builder().url(url).get().build()

            val response = client.newCall(request).execute()
            val body = response.body?.string()

            if (response.isSuccessful && body != null) {
                Result.success(body)
            }
            else {
                Result.failure(IOException("Ошибка Загрузки"))
            }
        } catch (e: Exception) {
            Result.failure(IOException("Ошибка Загрузки"))
        }
    }
}