package com.example.brainnode.utils

import com.example.brainnode.data.models.Subject

object FirebaseUtils {
    
    // Collection names
    const val USERS_COLLECTION = "users"
    const val NOTES_COLLECTION = "notes"
    const val QUIZZES_COLLECTION = "quizzes"
    const val QUIZ_ATTEMPTS_COLLECTION = "quiz_attempts"
    const val STUDENT_PROGRESS_COLLECTION = "student_progress"
    
    // Field names
    const val FIELD_USER_TYPE = "userType"
    const val FIELD_CREATED_AT = "createdAt"
    const val FIELD_UPDATED_AT = "updatedAt"
    const val FIELD_IS_PUBLISHED = "isPublished"
    const val FIELD_TEACHER_ID = "teacherId"
    const val FIELD_STUDENT_ID = "studentId"
    const val FIELD_SUBJECT = "subject"
    const val FIELD_COMPLETED_AT = "completedAt"
    const val FIELD_AVERAGE_SCORE = "averageScore"
    const val FIELD_LAST_LOGIN_AT = "lastLoginAt"
    
    // Helper functions
    fun getSubjectFromString(subjectString: String): Subject {
        return when (subjectString.uppercase()) {
            "OPERATING SYSTEM", "OS" -> Subject.OPERATING_SYSTEM
            "STATISTICS", "STAT" -> Subject.STATISTICS
            "PROGRAMMING", "PROG" -> Subject.PROGRAMMING
            else -> Subject.OPERATING_SYSTEM
        }
    }
    
    fun getSubjectCode(subject: Subject): String {
        return when (subject) {
            Subject.OPERATING_SYSTEM -> "OS"
            Subject.STATISTICS -> "STAT"
            Subject.PROGRAMMING -> "PROG"
        }
    }
    
    fun getSubjectDisplayName(subject: Subject): String {
        return when (subject) {
            Subject.OPERATING_SYSTEM -> "Operating System"
            Subject.STATISTICS -> "Statistics"
            Subject.PROGRAMMING -> "Programming"
        }
    }
    
    fun formatTimestamp(timestamp: Long): String {
        val date = java.util.Date(timestamp)
        val formatter = java.text.SimpleDateFormat("MMM dd, yyyy HH:mm", java.util.Locale.getDefault())
        return formatter.format(date)
    }
    
    fun formatScore(score: Double): String {
        return String.format("%.1f%%", score)
    }
    
    fun isValidEmail(email: String): Boolean {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()
    }
    
    fun isValidPassword(password: String): Boolean {
        return password.length >= 6
    }
    
    fun generateQuizId(): String {
        return "quiz_${System.currentTimeMillis()}_${(1000..9999).random()}"
    }
    
    fun generateNoteId(): String {
        return "note_${System.currentTimeMillis()}_${(1000..9999).random()}"
    }
    
    fun generateAttemptId(): String {
        return "attempt_${System.currentTimeMillis()}_${(1000..9999).random()}"
    }
}
