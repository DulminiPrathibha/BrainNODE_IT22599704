package com.example.brainnode.data.models

data class StudentStatistics(
    val studentId: String = "",
    val studentName: String = "",
    val totalQuizzesTaken: Int = 0,
    val totalScore: Int = 0,
    val totalQuestions: Int = 0,
    val averagePercentage: Double = 0.0,
    val bestScore: Int = 0,
    val worstScore: Int = 0,
    val lastQuizDate: Long = 0L
) {
    // No-argument constructor for Firebase
    constructor() : this("", "", 0, 0, 0, 0.0, 0, 0, 0L)
    
    fun getFormattedAveragePercentage(): String {
        return String.format("%.1f%%", averagePercentage)
    }
    
    fun getFormattedBestScore(): String {
        return String.format("%.1f%%", if (totalQuestions > 0) (bestScore.toDouble() / totalQuestions.toDouble()) * 100 else 0.0)
    }
    
    fun getFormattedWorstScore(): String {
        return String.format("%.1f%%", if (totalQuestions > 0) (worstScore.toDouble() / totalQuestions.toDouble()) * 100 else 0.0)
    }
}

data class OverallStatistics(
    val totalStudents: Int = 0,
    val totalQuizzesTaken: Int = 0,
    val averageScore: Double = 0.0,
    val topPerformers: List<StudentStatistics> = emptyList(),
    val bottomPerformers: List<StudentStatistics> = emptyList()
) {
    // No-argument constructor for Firebase
    constructor() : this(0, 0, 0.0, emptyList(), emptyList())
    
    fun getFormattedAverageScore(): String {
        return String.format("%.1f%%", averageScore)
    }
}
