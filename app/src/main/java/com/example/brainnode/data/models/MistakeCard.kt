package com.example.brainnode.data.models

data class MistakeCard(
    val id: String = "",
    val studentId: String = "",
    val studentName: String = "",
    val quizId: String = "",
    val quizTitle: String = "",
    val subject: Subject = Subject.OPERATING_SYSTEM,
    val questionId: String = "",
    val questionText: String = "",
    val correctOptionId: String = "",
    val correctOptionText: String = "",
    val selectedOptionId: String = "",
    val selectedOptionText: String = "",
    val explanation: String = "", // Teacher's mistake card description
    val mscNumber: Int = 0, // Mistake card number for this student
    val createdAt: Long = System.currentTimeMillis(),
    val isResolved: Boolean = false,
    val resolvedAt: Long = 0L
) {
    // No-argument constructor for Firebase
    constructor() : this(
        "", "", "", "", "", Subject.OPERATING_SYSTEM, "", "", "", "", "", "", "", 0, 0L, false, 0L
    )
    
    fun getFormattedMscNumber(): String = "MSC : $mscNumber"
    
    fun getQuizInfo(): String = "$quizTitle : ${subject.code}"
}
