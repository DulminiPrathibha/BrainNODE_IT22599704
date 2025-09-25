package com.example.brainnode.data.repository

import com.example.brainnode.data.firebase.FirebaseProgressService
import com.example.brainnode.data.models.StudentProgress
import com.example.brainnode.data.models.CommonMistake
import com.example.brainnode.data.models.QuizAttempt
import com.example.brainnode.data.models.Subject

class ProgressRepository {
    private val progressService = FirebaseProgressService()
    
    suspend fun updateStudentProgress(studentId: String, quizAttempt: QuizAttempt): Result<Unit> {
        return progressService.updateStudentProgress(studentId, quizAttempt)
    }
    
    suspend fun getStudentProgress(studentId: String): Result<StudentProgress?> {
        return progressService.getStudentProgress(studentId)
    }
    
    suspend fun getAllStudentsProgress(): Result<List<StudentProgress>> {
        return progressService.getAllStudentsProgress()
    }
    
    suspend fun getTopPerformers(limit: Int = 10): Result<List<StudentProgress>> {
        return progressService.getTopPerformers(limit)
    }
    
    suspend fun getCommonMistakesBySubject(subject: Subject): Result<List<CommonMistake>> {
        return progressService.getCommonMistakesBySubject(subject)
    }
    
    suspend fun updateLearningStreak(studentId: String): Result<Unit> {
        return progressService.updateLearningStreak(studentId)
    }
    
    suspend fun addCommonMistake(studentId: String, mistake: CommonMistake): Result<Unit> {
        return progressService.addCommonMistake(studentId, mistake)
    }
    
    suspend fun awardBadge(studentId: String, badgeName: String): Result<Unit> {
        return progressService.awardBadge(studentId, badgeName)
    }
    
    suspend fun hasBadge(studentId: String, badgeName: String): Result<Boolean> {
        return progressService.hasBadge(studentId, badgeName)
    }
    
    suspend fun isFirstQuizCompletion(studentId: String): Result<Boolean> {
        return progressService.isFirstQuizCompletion(studentId)
    }
}
