package com.example.myapplication
data class ImageItem(
    val id : Int,
    val url: String,
    val author: String,
    val headLine: String,
    val genres: List<String>,
    val size: Int
)
