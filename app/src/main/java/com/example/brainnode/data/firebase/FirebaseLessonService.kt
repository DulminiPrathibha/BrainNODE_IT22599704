package com.example.brainnode.data.firebase

import com.example.brainnode.data.models.LessonItem
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.coroutines.tasks.await

class FirebaseLessonService {
    
    private val firestore = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()
    private val lessonsCollection = firestore.collection("lessons")
    
    suspend fun createLesson(lesson: LessonItem): Result<String> {
        return try {
            val currentUser = auth.currentUser
            if (currentUser == null) {
                println("DEBUG Firebase: User not authenticated")
                return Result.failure(Exception("User not authenticated"))
            }
            
            println("DEBUG Firebase: Creating lesson for user ${currentUser.uid}")
            
            val lessonData = hashMapOf(
                "title" to lesson.title,
                "subjectName" to lesson.subjectName,
                "lessonNumber" to lesson.lessonNumber,
                "content" to lesson.content,
                "summary" to lesson.summary,
                "teacherId" to currentUser.uid,
                "createdAt" to System.currentTimeMillis(),
                "updatedAt" to System.currentTimeMillis()
            )
            
            println("DEBUG Firebase: Lesson data to save: $lessonData")
            
            val documentRef = lessonsCollection.add(lessonData).await()
            println("DEBUG Firebase: Lesson saved with ID: ${documentRef.id}")
            Result.success(documentRef.id)
        } catch (e: Exception) {
            println("DEBUG Firebase: Error saving lesson: ${e.message}")
            Result.failure(e)
        }
    }
    
    suspend fun getLessonsByTeacher(): Result<List<LessonItem>> {
        return try {
            val currentUser = auth.currentUser
            if (currentUser == null) {
                return Result.failure(Exception("User not authenticated"))
            }
            
            println("DEBUG: Current user UID: ${currentUser.uid}")
            
            // First, try to get all documents and then filter
            val querySnapshot = lessonsCollection.get().await()
            println("DEBUG: Total documents in lessons collection: ${querySnapshot.documents.size}")
            
            val allLessons = querySnapshot.documents.mapNotNull { document ->
                try {
                    val teacherId = document.getString("teacherId")
                    println("DEBUG: Document ${document.id} has teacherId: $teacherId")
                    
                    if (teacherId == currentUser.uid) {
                        LessonItem(
                            id = document.id,
                            title = document.getString("title") ?: "",
                            subjectName = document.getString("subjectName") ?: "",
                            lessonNumber = document.getLong("lessonNumber")?.toInt() ?: 1,
                            content = document.getString("content") ?: "",
                            summary = document.getString("summary") ?: "",
                            createdAt = document.getLong("createdAt") ?: 0L,
                            updatedAt = document.getLong("updatedAt") ?: 0L
                        )
                    } else {
                        null
                    }
                } catch (e: Exception) {
                    println("DEBUG: Error parsing document ${document.id}: ${e.message}")
                    null
                }
            }.sortedByDescending { it.createdAt }
            
            println("DEBUG: Filtered lessons for current user: ${allLessons.size}")
            Result.success(allLessons)
        } catch (e: Exception) {
            println("DEBUG: Exception in getLessonsByTeacher: ${e.message}")
            Result.failure(e)
        }
    }
    
    suspend fun updateLesson(lessonId: String, updatedLesson: LessonItem): Result<Unit> {
        return try {
            val currentUser = auth.currentUser
            if (currentUser == null) {
                return Result.failure(Exception("User not authenticated"))
            }
            
            val updateData = hashMapOf(
                "title" to updatedLesson.title,
                "subjectName" to updatedLesson.subjectName,
                "content" to updatedLesson.content,
                "summary" to updatedLesson.summary,
                "updatedAt" to System.currentTimeMillis()
            )
            
            lessonsCollection.document(lessonId).update(updateData as Map<String, Any>).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    suspend fun deleteLesson(lessonId: String): Result<Unit> {
        return try {
            val currentUser = auth.currentUser
            if (currentUser == null) {
                return Result.failure(Exception("User not authenticated"))
            }
            
            lessonsCollection.document(lessonId).delete().await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    suspend fun getLessonById(lessonId: String): Result<LessonItem?> {
        return try {
            val document = lessonsCollection.document(lessonId).get().await()
            
            if (document.exists()) {
                val lesson = LessonItem(
                    id = document.id,
                    title = document.getString("title") ?: "",
                    subjectName = document.getString("subjectName") ?: "",
                    lessonNumber = document.getLong("lessonNumber")?.toInt() ?: 1,
                    content = document.getString("content") ?: "",
                    summary = document.getString("summary") ?: "",
                    createdAt = document.getLong("createdAt") ?: 0L,
                    updatedAt = document.getLong("updatedAt") ?: 0L
                )
                Result.success(lesson)
            } else {
                Result.success(null)
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    // Method for students to get all available lessons
    suspend fun getAllLessons(): Result<List<LessonItem>> {
        return try {
            println("DEBUG: Getting all lessons for students")
            
            val querySnapshot = lessonsCollection
                .orderBy("createdAt", Query.Direction.DESCENDING)
                .get()
                .await()
            
            println("DEBUG: Total documents in lessons collection: ${querySnapshot.documents.size}")
            
            val allLessons = querySnapshot.documents.mapNotNull { document ->
                try {
                    LessonItem(
                        id = document.id,
                        title = document.getString("title") ?: "",
                        subjectName = document.getString("subjectName") ?: "",
                        lessonNumber = document.getLong("lessonNumber")?.toInt() ?: 1,
                        content = document.getString("content") ?: "",
                        summary = document.getString("summary") ?: "",
                        createdAt = document.getLong("createdAt") ?: 0L,
                        updatedAt = document.getLong("updatedAt") ?: 0L
                    )
                } catch (e: Exception) {
                    println("DEBUG: Error parsing document ${document.id}: ${e.message}")
                    null
                }
            }
            
            println("DEBUG: Successfully parsed ${allLessons.size} lessons for students")
            Result.success(allLessons)
        } catch (e: Exception) {
            println("DEBUG: Exception in getAllLessons: ${e.message}")
            Result.failure(e)
        }
    }
}
