package com.example.projekkampus.models

import java.io.Serializable

data class Review(
    val title: String = "",
    val description: String = "",
    val rating: Float = 0f,
    val image: String = ""
): Serializable
