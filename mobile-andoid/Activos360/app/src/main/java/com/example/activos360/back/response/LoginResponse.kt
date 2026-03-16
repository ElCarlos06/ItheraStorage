package com.example.activos360.back.model

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class LoginResponse(
    val data: TokenData?,
    val error: Boolean,
    val message: String,
    val status: String
)

@JsonClass(generateAdapter = true)
data class TokenData(
    val token: String
)