package com.example.brainnode.teacher.addnotes

data class Note(
    val id: String,
    val subjectCode: String,
    val content: String,
    val summary: String,
    val createdAt: Long,
    val teacherId: String,
    val updatedAt: Long = createdAt,
    val isPublished: Boolean = true
) {
    // Helper function to get formatted date
    fun getFormattedDate(): String {
        val date = java.util.Date(createdAt)
        val formatter = java.text.SimpleDateFormat("MMM dd, yyyy", java.util.Locale.getDefault())
        return formatter.format(date)
    }

    // Helper function to get formatted time
    fun getFormattedTime(): String {
        val date = java.util.Date(createdAt)
        val formatter = java.text.SimpleDateFormat("hh:mm a", java.util.Locale.getDefault())
        return formatter.format(date)
    }

    // Helper function to get subject name from code
    fun getSubjectName(): String {
        return when (subjectCode.uppercase()) {
            "OS" -> "Operating System"
            "STAT" -> "Statistics"
            "DB" -> "Database"
            "PROG" -> "Programming"
            else -> subjectCode
        }
    }
}
