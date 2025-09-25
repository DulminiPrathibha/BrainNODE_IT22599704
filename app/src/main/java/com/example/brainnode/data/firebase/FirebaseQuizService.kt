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
}
