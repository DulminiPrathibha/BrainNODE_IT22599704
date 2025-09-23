package com.example.brainnode.data.models

data class QuizAttempt(
    val id: String = "",
    val studentId: String = "",
    val studentName: String = "",
    val quizId: String = "",
    val quizTitle: String = "",
    val subject: Subject = Subject.OPERATING_SYSTEM,
    val answers: List<StudentAnswer> = emptyList(),
    val score: Int = 0,
    val totalQuestions: Int = 0,
    val timeSpent: Long = 0, // in milliseconds
    val completedAt: Long = System.currentTimeMillis(),
    val isCompleted: Boolean = false
) {
    // No-argument constructor for Firebase
    constructor() : this("", "", "", "", "", Subject.OPERATING_SYSTEM, emptyList(), 0, 0, 0L, 0L, false)
    
    fun getPercentageScore(): Double {
        return if (totalQuestions > 0) {
            (score.toDouble() / totalQuestions.toDouble()) * 100
        } else {
            0.0
        }
    }
}

data class StudentAnswer(
    val questionId: String = "",
    val selectedOptionId: String = "",
    val correctOptionId: String = "",
    val isCorrect: Boolean = false,
    val timeSpent: Long = 0 // time spent on this question in milliseconds
) {
    // No-argument constructor for Firebase
    constructor() : this("", "", "", false, 0L)
}
