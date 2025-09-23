package com.example.brainnode.data.models

data class Note(
    val id: String = "",
    val teacherId: String = "",
    val teacherName: String = "",
    val subject: Subject = Subject.OPERATING_SYSTEM,
    val lessonTitle: String = "",
    val content: String = "",
    val summary: String = "",
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis(),
    val isPublished: Boolean = false
) {
    // No-argument constructor for Firebase
    constructor() : this("", "", "", Subject.OPERATING_SYSTEM, "", "", "", 0L, 0L, false)
}

enum class Subject(val displayName: String, val code: String) {
    OPERATING_SYSTEM("Operating System", "OS"),
    STATISTICS("Statistics", "STAT"),
    PROGRAMMING("Programming", "PROG")
}
