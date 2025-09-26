package com.example.brainnode.data.repository

import com.example.brainnode.data.firebase.FirebaseLessonService
import com.example.brainnode.data.models.LessonItem

class LessonRepository {
    
    private val firebaseLessonService = FirebaseLessonService()
    
    suspend fun createLesson(lesson: LessonItem): Result<String> {
        return firebaseLessonService.createLesson(lesson)
    }
    
    suspend fun getLessonsByTeacher(): Result<List<LessonItem>> {
        return firebaseLessonService.getLessonsByTeacher()
    }
    
    suspend fun updateLesson(lessonId: String, updatedLesson: LessonItem): Result<Unit> {
        return firebaseLessonService.updateLesson(lessonId, updatedLesson)
    }
    
    suspend fun deleteLesson(lessonId: String): Result<Unit> {
        return firebaseLessonService.deleteLesson(lessonId)
    }
    
    suspend fun getLessonById(lessonId: String): Result<LessonItem?> {
        return firebaseLessonService.getLessonById(lessonId)
    }
}
