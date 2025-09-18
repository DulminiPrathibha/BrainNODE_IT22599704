package com.example.brainnode.teacher.quizzes.models

data class QuizSubject(
    val id: String = "",
    val name: String = "",
    val iconResource: Int = 0,
    val quizCount: Int = 0,
    val quizzes: List<Quiz> = emptyList()
)

data class Quiz(
    val id: String = "",
    val title: String = "",
    val subjectId: String = "",
    val questionCount: Int = 0,
    val createdAt: Long = System.currentTimeMillis(),
    val questions: List<Question> = emptyList()
)

data class Question(
    val id: String = "",
    val questionText: String = "",
    val correctAnswer: String = "",
    val wrongAnswers: List<String> = emptyList(),
    val mistakeDescription: String = ""
)
