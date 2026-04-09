package com.example.myapplication
import com.google.gson.Gson
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
    suspend fun loadBooks(): Result<List<ImageItem>> = withContext(Dispatchers.IO) {
        try {
            val url = "https://raw.githubusercontent.com/Vladimir-Savvateev/books/refs/heads/main/books.json"
            val jsonString = loadTextFile(url).getOrNull()
            if (jsonString != null) {
                val response = Gson().fromJson(jsonString, Books::class.java)
                Result.success(response.books)
            } else {
                Result.failure(IOException("Ошибка Загрузки"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}