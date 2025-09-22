package com.example.brainnode.student.quiz

data class QuizQuestion(
    val id: String,
    val questionText: String,
    val options: List<AnswerOption>,
    val correctAnswerId: String
)
