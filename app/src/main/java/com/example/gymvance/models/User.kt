package com.example.gymvance.models

data class User(
    val username: String = "",
    val email: String = "",
    val role: String = "user" // "user" o "admin"
)
