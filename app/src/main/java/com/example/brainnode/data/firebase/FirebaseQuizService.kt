package com.example.brainnode.data.firebase

import com.example.brainnode.data.models.Quiz
import com.example.brainnode.data.models.QuizAttempt
import com.example.brainnode.data.models.Subject
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.coroutines.tasks.await

class FirebaseQuizService {
    private val firestore = FirebaseFirestore.getInstance()
    private val quizzesCollection = firestore.collection("quizzes")
    private val attemptsCollection = firestore.collection("quiz_attempts")
    
    // Quiz Management
    suspend fun createQuiz(quiz: Quiz): Result<String> {
        return try {
            val docRef = quizzesCollection.add(quiz).await()
            val quizWithId = quiz.copy(id = docRef.id)
            docRef.set(quizWithId).await()
            Result.success(docRef.id)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    suspend fun updateQuiz(quiz: Quiz): Result<Unit> {
        return try {
            quizzesCollection.document(quiz.id).set(quiz).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    suspend fun deleteQuiz(quizId: String): Result<Unit> {
        return try {
            quizzesCollection.document(quizId).delete().await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    suspend fun getQuizById(quizId: String): Result<Quiz?> {
        return try {
            val doc = quizzesCollection.document(quizId).get().await()
            val quiz = doc.toObject(Quiz::class.java)
            Result.success(quiz)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    suspend fun getQuizzesByTeacher(teacherId: String): Result<List<Quiz>> {
        return try {
            // First try the simple query without where clause to avoid index issues
            val querySnapshot = quizzesCollection.get().await()
            
            val quizzes = querySnapshot.documents.mapNotNull { doc ->
                doc.toObject(Quiz::class.java)
            }.filter { quiz ->
                quiz.teacherId == teacherId
            }.sortedByDescending { it.createdAt }
            
            Result.success(quizzes)
        } catch (e: Exception) {
            // If that fails, try the where query
            try {
                val querySnapshot = quizzesCollection
                    .whereEqualTo("teacherId", teacherId)
                    .get()
                    .await()
                
                val quizzes = querySnapshot.documents.mapNotNull { doc ->
                    doc.toObject(Quiz::class.java)
                }.sortedByDescending { it.createdAt }
                
                Result.success(quizzes)
            } catch (e2: Exception) {
                Result.failure(e2)
            }
        }
    }
    
    suspend fun getQuizzesBySubject(subject: Subject): Result<List<Quiz>> {
        return try {
            val querySnapshot = quizzesCollection
                .whereEqualTo("subject", subject)
                .whereEqualTo("isPublished", true)
                .get()
                .await()
            
            val quizzes = querySnapshot.documents.mapNotNull { doc ->
                doc.toObject(Quiz::class.java)
            }.sortedByDescending { it.createdAt }
            
            Result.success(quizzes)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    suspend fun getAllQuizzes(): Result<List<Quiz>> {
        return try {
            val querySnapshot = quizzesCollection
                .get()
                .await()
            
            val quizzes = querySnapshot.documents.mapNotNull { doc ->
                doc.toObject(Quiz::class.java)
            }.sortedByDescending { it.createdAt }
            
            Result.success(quizzes)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    suspend fun getPublishedQuizzes(): Result<List<Quiz>> {
        return try {
            val querySnapshot = quizzesCollection
                .whereEqualTo("isPublished", true)
                .get()
                .await()
            
            val quizzes = querySnapshot.documents.mapNotNull { doc ->
                doc.toObject(Quiz::class.java)
            }.sortedByDescending { it.createdAt }
            
            Result.success(quizzes)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    suspend fun publishQuiz(quizId: String): Result<Unit> {
        return try {
            quizzesCollection.document(quizId)
                .update("isPublished", true)
                .await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    // Quiz Attempt Management
    suspend fun submitQuizAttempt(attempt: QuizAttempt): Result<String> {
        return try {
            val docRef = attemptsCollection.add(attempt).await()
            val attemptWithId = attempt.copy(id = docRef.id)
            docRef.set(attemptWithId).await()
            Result.success(docRef.id)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    suspend fun getQuizAttemptsByStudent(studentId: String): Result<List<QuizAttempt>> {
        return try {
            val querySnapshot = attemptsCollection
                .whereEqualTo("studentId", studentId)
                .orderBy("completedAt", Query.Direction.DESCENDING)
                .get()
                .await()
            
            val attempts = querySnapshot.documents.mapNotNull { doc ->
                doc.toObject(QuizAttempt::class.java)
            }
            Result.success(attempts)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    suspend fun getQuizAttemptsByQuiz(quizId: String): Result<List<QuizAttempt>> {
        return try {
            val querySnapshot = attemptsCollection
                .whereEqualTo("quizId", quizId)
                .orderBy("completedAt", Query.Direction.DESCENDING)
                .get()
                .await()
            
            val attempts = querySnapshot.documents.mapNotNull { doc ->
                doc.toObject(QuizAttempt::class.java)
            }
            Result.success(attempts)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    suspend fun getQuizAttemptsBySubject(subject: Subject): Result<List<QuizAttempt>> {
        return try {
            val querySnapshot = attemptsCollection
                .whereEqualTo("subject", subject)
                .orderBy("completedAt", Query.Direction.DESCENDING)
                .get()
                .await()
            
            val attempts = querySnapshot.documents.mapNotNull { doc ->
                doc.toObject(QuizAttempt::class.java)
            }
            Result.success(attempts)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    suspend fun getAllQuizAttempts(): Result<List<QuizAttempt>> {
        return try {
            val querySnapshot = attemptsCollection
                .get()
                .await()
            
            println("📊 getAllQuizAttempts: Found ${querySnapshot.documents.size} total attempts")
            
            val attempts = querySnapshot.documents.mapNotNull { doc ->
                doc.toObject(QuizAttempt::class.java)
            }.sortedByDescending { it.completedAt }
            
            println("✅ Returning ${attempts.size} attempts")
            Result.success(attempts)
        } catch (e: Exception) {
            println("❌ Error getting all quiz attempts: ${e.message}")
            e.printStackTrace()
            Result.failure(e)
        }
    }
    
    suspend fun getStudentQuizHistory(studentId: String, quizId: String): Result<List<QuizAttempt>> {
        return try {
            val querySnapshot = attemptsCollection
                .whereEqualTo("studentId", studentId)
                .whereEqualTo("quizId", quizId)
                .orderBy("completedAt", Query.Direction.DESCENDING)
                .get()
                .await()
            
            val attempts = querySnapshot.documents.mapNotNull { doc ->
                doc.toObject(QuizAttempt::class.java)
            }
            Result.success(attempts)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    suspend fun hasStudentAttemptedQuiz(studentId: String, quizId: String): Result<Boolean> {
        return try {
            val querySnapshot = attemptsCollection
                .whereEqualTo("studentId", studentId)
                .whereEqualTo("quizId", quizId)
                .whereEqualTo("isCompleted", true)
                .limit(1)
                .get()
                .await()
            
            Result.success(querySnapshot.documents.isNotEmpty())
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    // Statistics Methods
    suspend fun calculateOverallAverageScore(): Result<Double> {
        return try {
            // First, try to get all quiz attempts without filtering
            val allAttemptsSnapshot = attemptsCollection.get().await()
            println("🔍 Total documents in quiz_attempts: ${allAttemptsSnapshot.documents.size}")
            
            // Debug: Print summary of documents
            val completedCount = allAttemptsSnapshot.documents.count { doc ->
                val attempt = doc.toObject(QuizAttempt::class.java)
                attempt?.isCompleted == true
            }
            println("📄 Documents summary: ${completedCount} marked as completed out of ${allAttemptsSnapshot.documents.size} total")
            
            // Get all attempts and filter manually (more reliable than Firestore query)
            val allAttempts = allAttemptsSnapshot.documents.mapNotNull { doc ->
                doc.toObject(QuizAttempt::class.java)
            }
            
            // Filter for completed attempts manually - be more lenient
            val completedAttempts = allAttempts.filter { 
                // Consider an attempt valid if it has a score and total questions > 0
                it.totalQuestions > 0 && it.score >= 0
            }
            println("🎯 Valid attempts found (manual filter): ${completedAttempts.size}")
            
            // Also try filtering by isCompleted for comparison
            val strictlyCompletedAttempts = allAttempts.filter { it.isCompleted && it.totalQuestions > 0 }
            println("🎯 Strictly completed attempts: ${strictlyCompletedAttempts.size}")
            
            if (completedAttempts.isEmpty()) {
                println("⚠️ No completed attempts found, returning 0.0")
                Result.success(0.0)
            } else {
                val totalScore = completedAttempts.sumOf { it.score }
                val totalQuestions = completedAttempts.sumOf { it.totalQuestions }
                val averagePercentage = if (totalQuestions > 0) {
                    (totalScore.toDouble() / totalQuestions.toDouble()) * 100
                } else 0.0
                
                println("📊 Calculation: $totalScore correct out of $totalQuestions total = $averagePercentage%")
                Result.success(averagePercentage)
            }
        } catch (e: Exception) {
            println("❌ Error calculating average score: ${e.message}")
            e.printStackTrace()
            Result.failure(e)
        }
    }
    
    suspend fun getStudentStatistics(): Result<List<com.example.brainnode.data.models.StudentStatistics>> {
        return try {
            // Get all attempts without filtering first
            val allAttemptsSnapshot = attemptsCollection.get().await()
            println("👥 Getting student statistics from ${allAttemptsSnapshot.documents.size} total attempts")
            
            val allAttempts = allAttemptsSnapshot.documents.mapNotNull { doc ->
                doc.toObject(QuizAttempt::class.java)
            }
            
            // Filter for completed attempts manually - be more lenient
            val completedAttempts = allAttempts.filter { 
                // Consider an attempt valid if it has a score and total questions > 0
                it.totalQuestions > 0 && it.score >= 0
            }
            println("✅ Valid attempts for statistics: ${completedAttempts.size}")
            
            // Also try filtering by isCompleted for comparison
            val strictlyCompletedAttempts = allAttempts.filter { it.isCompleted && it.totalQuestions > 0 }
            println("✅ Strictly completed attempts for statistics: ${strictlyCompletedAttempts.size}")
            
            if (completedAttempts.isEmpty()) {
                println("⚠️ No completed attempts found for student statistics")
                Result.success(emptyList())
            } else {
                // Get unique student IDs
                val studentIds = completedAttempts.map { it.studentId }.distinct()
                println("👤 Fetching names for ${studentIds.size} unique students")
                
                // Fetch user names from auth service
                val authService = FirebaseAuthService()
                val userNamesResult = authService.getUsersByIds(studentIds)
                val userNames = userNamesResult.getOrElse { 
                    println("⚠️ Failed to fetch user names, using fallbacks")
                    emptyMap() 
                }
                
                val studentStats = completedAttempts
                    .groupBy { it.studentId }
                    .map { (studentId, studentAttempts) ->
                        val totalScore = studentAttempts.sumOf { it.score }
                        val totalQuestions = studentAttempts.sumOf { it.totalQuestions }
                        val averagePercentage = if (totalQuestions > 0) {
                            (totalScore.toDouble() / totalQuestions.toDouble()) * 100
                        } else 0.0
                        
                        val bestAttempt = studentAttempts.maxByOrNull { it.getPercentageScore() }
                        val worstAttempt = studentAttempts.minByOrNull { it.getPercentageScore() }
                        
                        // Use fetched name or fallback
                        val studentName = userNames[studentId] 
                            ?: studentAttempts.firstOrNull()?.studentName?.takeIf { it.isNotEmpty() }
                            ?: "Student ${studentId.take(6)}"
                        
                        val stats = com.example.brainnode.data.models.StudentStatistics(
                            studentId = studentId,
                            studentName = studentName,
                            totalQuizzesTaken = studentAttempts.size,
                            totalScore = totalScore,
                            totalQuestions = totalQuestions,
                            averagePercentage = averagePercentage,
                            bestScore = bestAttempt?.score ?: 0,
                            worstScore = worstAttempt?.score ?: 0,
                            lastQuizDate = studentAttempts.maxOfOrNull { it.completedAt } ?: 0L
                        )
                        
                        println("📊 Student $studentName ($studentId): ${studentAttempts.size} quizzes, $averagePercentage% average")
                        stats
                    }
                
                println("✅ Generated statistics for ${studentStats.size} students")
                Result.success(studentStats)
            }
        } catch (e: Exception) {
            println("❌ Error getting student statistics: ${e.message}")
            e.printStackTrace()
            Result.failure(e)
        }
    }
}
