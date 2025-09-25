package com.example.brainnode.data.repository

import com.example.brainnode.data.firebase.FirebaseMistakeCardService
import com.example.brainnode.data.models.MistakeCard
import com.example.brainnode.data.models.Subject

class MistakeCardRepository {
    private val firebaseMistakeCardService = FirebaseMistakeCardService()
    
    suspend fun createMistakeCard(mistakeCard: MistakeCard): Result<String> {
        return firebaseMistakeCardService.createMistakeCard(mistakeCard)
    }
    
    suspend fun getMistakeCardsByStudent(studentId: String): Result<List<MistakeCard>> {
        return firebaseMistakeCardService.getMistakeCardsByStudent(studentId)
    }
    
    suspend fun getUnresolvedMistakeCardsByStudent(studentId: String): Result<List<MistakeCard>> {
        return firebaseMistakeCardService.getUnresolvedMistakeCardsByStudent(studentId)
    }
    
    suspend fun resolveMistakeCard(mistakeCardId: String): Result<Unit> {
        return firebaseMistakeCardService.resolveMistakeCard(mistakeCardId)
    }
    
    suspend fun deleteMistakeCard(mistakeCardId: String): Result<Unit> {
        return firebaseMistakeCardService.deleteMistakeCard(mistakeCardId)
    }
    
    suspend fun getMistakeCardById(mistakeCardId: String): Result<MistakeCard?> {
        return firebaseMistakeCardService.getMistakeCardById(mistakeCardId)
    }
    
    suspend fun getNextMscNumber(studentId: String): Result<Int> {
        return firebaseMistakeCardService.getNextMscNumber(studentId)
    }
    
    suspend fun getMistakeCardsBySubject(studentId: String, subject: Subject): Result<List<MistakeCard>> {
        return firebaseMistakeCardService.getMistakeCardsBySubject(studentId, subject)
    }
    
    suspend fun hasUnresolvedMistakeCards(studentId: String): Result<Boolean> {
        return firebaseMistakeCardService.hasUnresolvedMistakeCards(studentId)
    }
}
