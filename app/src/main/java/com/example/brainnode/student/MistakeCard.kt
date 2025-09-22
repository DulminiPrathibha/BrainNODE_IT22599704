package com.example.brainnode.student

data class MistakeCard(
    val mscNumber: String,
    val quizInfo: String,
    val questionText: String,
    val userAnswer: String,
    val note: String,
    val position: String
)
