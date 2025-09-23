package com.example.brainnode.data.models

data class Quiz(
    val id: String = "",
    val teacherId: String = "",
    val teacherName: String = "",
    val subject: Subject = Subject.OPERATING_SYSTEM,
    val title: String = "",
    val description: String = "",
    val questions: List<QuizQuestion> = emptyList(),
    val timeLimit: Int = 30, // seconds per question
    val totalQuestions: Int = 0,
    val createdAt: Long = System.currentTimeMillis(),
    val isPublished: Boolean = false
) {
    // No-argument constructor for Firebase
    constructor() : this("", "", "", Subject.OPERATING_SYSTEM, "", "", emptyList(), 30, 0, 0L, false)
}

data class QuizQuestion(
    val id: String = "",
    val questionText: String = "",
    val options: List<QuizOption> = emptyList(),
    val correctAnswerId: String = "",
    val explanation: String = ""
) {
    // No-argument constructor for Firebase
    constructor() : this("", "", emptyList(), "", "")
}

data class QuizOption(
    val id: String = "",
    val text: String = "",
    val isCorrect: Boolean = false
) {
    // No-argument constructor for Firebase
    constructor() : this("", "", false)
}
