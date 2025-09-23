package com.example.brainnode.data.models

data class StudentProgress(
    val studentId: String = "",
    val studentName: String = "",
    val totalQuizzesTaken: Int = 0,
    val totalCorrectAnswers: Int = 0,
    val totalQuestions: Int = 0,
    val averageScore: Double = 0.0,
    val learningStreak: Int = 0,
    val lastActivityDate: Long = 0L,
    val subjectProgress: Map<String, SubjectProgress> = emptyMap(),
    val achievements: List<String> = emptyList(),
    val commonMistakes: List<CommonMistake> = emptyList()
) {
    // No-argument constructor for Firebase
    constructor() : this("", "", 0, 0, 0, 0.0, 0, 0L, emptyMap(), emptyList(), emptyList())
}

data class SubjectProgress(
    val subject: Subject = Subject.OPERATING_SYSTEM,
    val quizzesTaken: Int = 0,
    val correctAnswers: Int = 0,
    val totalQuestions: Int = 0,
    val averageScore: Double = 0.0,
    val lastQuizDate: Long = 0L
) {
    // No-argument constructor for Firebase
    constructor() : this(Subject.OPERATING_SYSTEM, 0, 0, 0, 0.0, 0L)
}

data class CommonMistake(
    val questionId: String = "",
    val questionText: String = "",
    val subject: Subject = Subject.OPERATING_SYSTEM,
    val incorrectAnswer: String = "",
    val correctAnswer: String = "",
    val mistakeCount: Int = 1,
    val lastMistakeDate: Long = System.currentTimeMillis()
) {
    // No-argument constructor for Firebase
    constructor() : this("", "", Subject.OPERATING_SYSTEM, "", "", 1, 0L)
}
