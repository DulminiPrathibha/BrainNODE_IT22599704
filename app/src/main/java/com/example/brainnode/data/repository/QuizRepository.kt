package com.example.brainnode.data.repository

import com.example.brainnode.data.firebase.FirebaseQuizService
import com.example.brainnode.data.models.Quiz
import com.example.brainnode.data.models.QuizAttempt
import com.example.brainnode.data.models.Subject

class QuizRepository {
    private val quizService = FirebaseQuizService()
    
    // Quiz Management
    suspend fun createQuiz(quiz: Quiz): Result<String> {
        return quizService.createQuiz(quiz)
    }
    
    suspend fun updateQuiz(quiz: Quiz): Result<Unit> {
        return quizService.updateQuiz(quiz)
    }
    
    suspend fun deleteQuiz(quizId: String): Result<Unit> {
        return quizService.deleteQuiz(quizId)
    }
    
    suspend fun getQuizById(quizId: String): Result<Quiz?> {
        return quizService.getQuizById(quizId)
    }
    
    suspend fun getQuizzesByTeacher(teacherId: String): Result<List<Quiz>> {
        return quizService.getQuizzesByTeacher(teacherId)
    }
    
    suspend fun getQuizzesBySubject(subject: Subject): Result<List<Quiz>> {
        return quizService.getQuizzesBySubject(subject)
    }
    
    suspend fun getAllQuizzes(): Result<List<Quiz>> {
        return quizService.getAllQuizzes()
    }
    
    suspend fun getPublishedQuizzes(): Result<List<Quiz>> {
        return quizService.getPublishedQuizzes()
    }
    
    suspend fun publishQuiz(quizId: String): Result<Unit> {
        return quizService.publishQuiz(quizId)
    }
    
    // Quiz Attempt Management
    suspend fun submitQuizAttempt(attempt: QuizAttempt): Result<String> {
        return quizService.submitQuizAttempt(attempt)
    }
    
    suspend fun getQuizAttemptsByStudent(studentId: String): Result<List<QuizAttempt>> {
        return quizService.getQuizAttemptsByStudent(studentId)
    }
    
    suspend fun getQuizAttemptsByQuiz(quizId: String): Result<List<QuizAttempt>> {
        return quizService.getQuizAttemptsByQuiz(quizId)
    }
    
    suspend fun getQuizAttemptsBySubject(subject: Subject): Result<List<QuizAttempt>> {
        return quizService.getQuizAttemptsBySubject(subject)
    }
    
    suspend fun getAllQuizAttempts(): Result<List<QuizAttempt>> {
        return quizService.getAllQuizAttempts()
    }
    
    suspend fun getStudentQuizHistory(studentId: String, quizId: String): Result<List<QuizAttempt>> {
        return quizService.getStudentQuizHistory(studentId, quizId)
    }
    
    suspend fun hasStudentAttemptedQuiz(studentId: String, quizId: String): Result<Boolean> {
        return quizService.hasStudentAttemptedQuiz(studentId, quizId)
    }
}
