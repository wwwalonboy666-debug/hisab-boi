package com.example.model

data class UserProfile(
    val name: String = "",
    val photoPath: String? = null,
    val photoUpdatedAt: Long = 0L,
    val isSetupCompleted: Boolean = false
)
