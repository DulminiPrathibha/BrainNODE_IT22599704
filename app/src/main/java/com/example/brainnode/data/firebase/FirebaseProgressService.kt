package com.example.brainnode.data.firebase

import com.example.brainnode.data.models.StudentProgress
import com.example.brainnode.data.models.SubjectProgress
import com.example.brainnode.data.models.CommonMistake
import com.example.brainnode.data.models.QuizAttempt
import com.example.brainnode.data.models.Subject
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.coroutines.tasks.await

class FirebaseProgressService {
    private val firestore = FirebaseFirestore.getInstance()
    private val progressCollection = firestore.collection("student_progress")
    
    suspend fun updateStudentProgress(studentId: String, quizAttempt: QuizAttempt): Result<Unit> {
        return try {
            val progressDoc = progressCollection.document(studentId)
            val currentProgress = getStudentProgress(studentId).getOrNull()
            
            val updatedProgress = if (currentProgress != null) {
                updateExistingProgress(currentProgress, quizAttempt)
            } else {
                createNewProgress(studentId, quizAttempt)
            }
            
            progressDoc.set(updatedProgress).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    suspend fun getStudentProgress(studentId: String): Result<StudentProgress?> {
        return try {
            val doc = progressCollection.document(studentId).get().await()
            val progress = doc.toObject(StudentProgress::class.java)
            Result.success(progress)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    suspend fun getAllStudentsProgress(): Result<List<StudentProgress>> {
        return try {
            val querySnapshot = progressCollection
                .orderBy("averageScore", Query.Direction.DESCENDING)
                .get()
                .await()
            
            val progressList = querySnapshot.documents.mapNotNull { doc ->
                doc.toObject(StudentProgress::class.java)
            }
            Result.success(progressList)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    suspend fun getTopPerformers(limit: Int = 10): Result<List<StudentProgress>> {
        return try {
            val querySnapshot = progressCollection
                .orderBy("averageScore", Query.Direction.DESCENDING)
                .limit(limit.toLong())
                .get()
                .await()
            
            val topPerformers = querySnapshot.documents.mapNotNull { doc ->
                doc.toObject(StudentProgress::class.java)
            }
            Result.success(topPerformers)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    suspend fun getCommonMistakesBySubject(subject: Subject): Result<List<CommonMistake>> {
        return try {
            val querySnapshot = progressCollection.get().await()
            
            val allMistakes = mutableListOf<CommonMistake>()
            querySnapshot.documents.forEach { doc ->
                val progress = doc.toObject(StudentProgress::class.java)
                progress?.commonMistakes?.filter { it.subject == subject }?.let { mistakes ->
                    allMistakes.addAll(mistakes)
                }
            }
            
            // Group mistakes by question and sum mistake counts
            val groupedMistakes = allMistakes.groupBy { it.questionId }
                .map { (questionId, mistakes) ->
                    val firstMistake = mistakes.first()
                    firstMistake.copy(
                        mistakeCount = mistakes.sumOf { it.mistakeCount },
                        lastMistakeDate = mistakes.maxOf { it.lastMistakeDate }
                    )
                }
                .sortedByDescending { it.mistakeCount }
            
            Result.success(groupedMistakes)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    suspend fun updateLearningStreak(studentId: String): Result<Unit> {
        return try {
            val progressDoc = progressCollection.document(studentId)
            val currentProgress = getStudentProgress(studentId).getOrNull()
            
            if (currentProgress != null) {
                val today = System.currentTimeMillis()
                val oneDayInMillis = 24 * 60 * 60 * 1000
                val lastActivityDate = currentProgress.lastActivityDate
                
                val newStreak = if (today - lastActivityDate <= oneDayInMillis) {
                    currentProgress.learningStreak + 1
                } else if (today - lastActivityDate <= 2 * oneDayInMillis) {
                    currentProgress.learningStreak // Same day or consecutive day
                } else {
                    1 // Reset streak
                }
                
                val updatedProgress = currentProgress.copy(
                    learningStreak = newStreak,
                    lastActivityDate = today
                )
                
                progressDoc.set(updatedProgress).await()
            }
            
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    suspend fun addCommonMistake(studentId: String, mistake: CommonMistake): Result<Unit> {
        return try {
            val progressDoc = progressCollection.document(studentId)
            val currentProgress = getStudentProgress(studentId).getOrNull()
            
            if (currentProgress != null) {
                val existingMistakes = currentProgress.commonMistakes.toMutableList()
                val existingMistakeIndex = existingMistakes.indexOfFirst { 
                    it.questionId == mistake.questionId 
                }
                
                if (existingMistakeIndex != -1) {
                    // Update existing mistake
                    val existingMistake = existingMistakes[existingMistakeIndex]
                    existingMistakes[existingMistakeIndex] = existingMistake.copy(
                        mistakeCount = existingMistake.mistakeCount + 1,
                        lastMistakeDate = mistake.lastMistakeDate
                    )
                } else {
                    // Add new mistake
                    existingMistakes.add(mistake)
                }
                
                val updatedProgress = currentProgress.copy(commonMistakes = existingMistakes)
                progressDoc.set(updatedProgress).await()
            }
            
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    private fun updateExistingProgress(currentProgress: StudentProgress, quizAttempt: QuizAttempt): StudentProgress {
        val newTotalQuizzes = currentProgress.totalQuizzesTaken + 1
        val newTotalCorrect = currentProgress.totalCorrectAnswers + quizAttempt.score
        val newTotalQuestions = currentProgress.totalQuestions + quizAttempt.totalQuestions
        val newAverageScore = (newTotalCorrect.toDouble() / newTotalQuestions.toDouble()) * 100
        
        // Update subject progress
        val subjectProgressMap = currentProgress.subjectProgress.toMutableMap()
        val currentSubjectProgress = subjectProgressMap[quizAttempt.subject.name] ?: SubjectProgress(quizAttempt.subject)
        
        val updatedSubjectProgress = currentSubjectProgress.copy(
            quizzesTaken = currentSubjectProgress.quizzesTaken + 1,
            correctAnswers = currentSubjectProgress.correctAnswers + quizAttempt.score,
            totalQuestions = currentSubjectProgress.totalQuestions + quizAttempt.totalQuestions,
            averageScore = ((currentSubjectProgress.correctAnswers + quizAttempt.score).toDouble() / 
                          (currentSubjectProgress.totalQuestions + quizAttempt.totalQuestions).toDouble()) * 100,
            lastQuizDate = quizAttempt.completedAt
        )
        
        subjectProgressMap[quizAttempt.subject.name] = updatedSubjectProgress
        
        return currentProgress.copy(
            totalQuizzesTaken = newTotalQuizzes,
            totalCorrectAnswers = newTotalCorrect,
            totalQuestions = newTotalQuestions,
            averageScore = newAverageScore,
            lastActivityDate = quizAttempt.completedAt,
            subjectProgress = subjectProgressMap
        )
    }
    
    private fun createNewProgress(studentId: String, quizAttempt: QuizAttempt): StudentProgress {
        val subjectProgress = SubjectProgress(
            subject = quizAttempt.subject,
            quizzesTaken = 1,
            correctAnswers = quizAttempt.score,
            totalQuestions = quizAttempt.totalQuestions,
            averageScore = quizAttempt.getPercentageScore(),
            lastQuizDate = quizAttempt.completedAt
        )
        
        return StudentProgress(
            studentId = studentId,
            studentName = quizAttempt.studentName,
            totalQuizzesTaken = 1,
            totalCorrectAnswers = quizAttempt.score,
            totalQuestions = quizAttempt.totalQuestions,
            averageScore = quizAttempt.getPercentageScore(),
            learningStreak = 1,
            lastActivityDate = quizAttempt.completedAt,
            subjectProgress = mapOf(quizAttempt.subject.name to subjectProgress)
        )
    }
}
