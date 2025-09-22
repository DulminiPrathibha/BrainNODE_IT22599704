package com.example.brainnode.student.quiz

data class AnswerOption(
    val id: String,
    val text: String,
    var isSelected: Boolean = false
)
