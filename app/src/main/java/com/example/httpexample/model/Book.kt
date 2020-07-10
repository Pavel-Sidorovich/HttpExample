package com.example.httpexample.model

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

/**
 * Simple model for storing Book.
 */
@JsonClass (generateAdapter = true)
data class Book(
    val title: String,
    val id: Int
)