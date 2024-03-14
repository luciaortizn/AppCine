package com.example.appcine.ui.auth

data class UserData(
    val uid: String? = null,
    val email: String? = null,
    val username: String? = null,
    val firstName: String? = null,
    val password: String? = null,
    val profileImage: String? = null,
    val moviesToWatch: List<String>? = null,
    val moviesLiked: List<Int>? = null,
    val moviesPending: List<String>? = null
)
