package com.example.brainnode.student

// This is a deprecated class - use com.example.brainnode.data.models.MistakeCard instead
// Keeping for backward compatibility temporarily
@Deprecated("Use com.example.brainnode.data.models.MistakeCard instead")
data class MistakeCard(
    val mscNumber: String,
    val quizInfo: String,
    val questionText: String,
    val userAnswer: String,
    val note: String,
    val position: String
)
