package com.example.brainnode.data.models

data class User(
    val uid: String = "",
    val email: String = "",
    val name: String = "",
    val userType: UserType = UserType.STUDENT,
    val profileImageUrl: String = "",
    val createdAt: Long = System.currentTimeMillis(),
    val lastLoginAt: Long = System.currentTimeMillis()
) {
    // No-argument constructor for Firebase
    constructor() : this("", "", "", UserType.STUDENT, "", 0L, 0L)
}

enum class UserType {
    STUDENT,
    TEACHER
}
