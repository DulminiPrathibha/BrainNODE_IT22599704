package com.example.brainnode.data.models

data class LessonItem(
    val id: String = "",
    val title: String = "",
    val subjectName: String = "",
    val lessonNumber: Int = 1,
    val content: String = "",
    val summary: String = "",
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis()
) {
    // Helper method to get formatted lesson title
    fun getFormattedTitle(): String {
        return "$subjectName - Lesson $lessonNumber"
    }
    
    // No-argument constructor for Firebase
    constructor() : this("", "", "", 1, "", "", 0L, 0L)
}
