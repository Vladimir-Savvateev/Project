package com.example.myapplication
import java.io.Serializable
data class ImageItem(
    val id : Int,
    val url: String,
    val author: String,
    val headLine: String,
    val genres: ArrayList<String>,
    val size: Int
) : Serializable
