package com.example.model

enum class AppLanguage(val code: String, val title: String) {
    BN("bn", "বাংলা"),
    EN("en", "English")
}

enum class ThemeMode(val code: String) {
    SYSTEM("SYSTEM"),
    LIGHT("LIGHT"),
    DARK("DARK")
}
