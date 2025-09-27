package com.example.brainnode.data.firebase

import com.example.brainnode.data.models.MistakeCard
import com.example.brainnode.data.models.Subject
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.coroutines.tasks.await

class FirebaseMistakeCardService {
    private val firestore = FirebaseFirestore.getInstance()
    private val mistakeCardsCollection = firestore.collection("mistake_cards")
    
    // Create a new mistake card
    suspend fun createMistakeCard(mistakeCard: MistakeCard): Result<String> {
        return try {
            val docRef = mistakeCardsCollection.add(mistakeCard).await()
            val mistakeCardWithId = mistakeCard.copy(id = docRef.id)
            docRef.set(mistakeCardWithId).await()
            Result.success(docRef.id)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    // Get all mistake cards for a student (unresolved first)
    suspend fun getMistakeCardsByStudent(studentId: String): Result<List<MistakeCard>> {
        return try {
            val querySnapshot = mistakeCardsCollection
                .whereEqualTo("studentId", studentId)
                .get()
                .await()
            
            val mistakeCards = querySnapshot.documents.mapNotNull { doc ->
                doc.toObject(MistakeCard::class.java)
            }.sortedWith(compareBy<MistakeCard> { it.isResolved }.thenByDescending { it.createdAt })
            
            Result.success(mistakeCards)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    // Get unresolved mistake cards for a student
    suspend fun getUnresolvedMistakeCardsByStudent(studentId: String): Result<List<MistakeCard>> {
        return try {
            val querySnapshot = mistakeCardsCollection
                .whereEqualTo("studentId", studentId)
                .whereEqualTo("isResolved", false)
                .get()
                .await()
            
            val mistakeCards = querySnapshot.documents.mapNotNull { doc ->
                doc.toObject(MistakeCard::class.java)
            }.sortedBy { it.createdAt }
            
            Result.success(mistakeCards)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    // Mark mistake card as resolved
    suspend fun resolveMistakeCard(mistakeCardId: String): Result<Unit> {
        return try {
            println("🔧 Updating mistake card $mistakeCardId to resolved=true")
            mistakeCardsCollection.document(mistakeCardId)
                .update(
                    mapOf(
                        "isResolved" to true,
                        "resolvedAt" to System.currentTimeMillis()
                    )
                )
                .await()
            println("✅ Successfully updated mistake card $mistakeCardId")
            Result.success(Unit)
        } catch (e: Exception) {
            println("❌ Failed to update mistake card: ${e.message}")
            Result.failure(e)
        }
    }
    
    // Delete mistake card
    suspend fun deleteMistakeCard(mistakeCardId: String): Result<Unit> {
        return try {
            mistakeCardsCollection.document(mistakeCardId).delete().await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    // Get mistake card by ID
    suspend fun getMistakeCardById(mistakeCardId: String): Result<MistakeCard?> {
        return try {
            val doc = mistakeCardsCollection.document(mistakeCardId).get().await()
            val mistakeCard = doc.toObject(MistakeCard::class.java)
            Result.success(mistakeCard)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    // Get next MSC number for a student
    suspend fun getNextMscNumber(studentId: String): Result<Int> {
        return try {
            val querySnapshot = mistakeCardsCollection
                .whereEqualTo("studentId", studentId)
                .get()
                .await()
            
            val highestMsc = querySnapshot.documents.mapNotNull { doc ->
                doc.toObject(MistakeCard::class.java)?.mscNumber
            }.maxOrNull() ?: 0
            
            Result.success(highestMsc + 1)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    // Get mistake cards by subject for a student
    suspend fun getMistakeCardsBySubject(studentId: String, subject: Subject): Result<List<MistakeCard>> {
        return try {
            val querySnapshot = mistakeCardsCollection
                .whereEqualTo("studentId", studentId)
                .whereEqualTo("subject", subject)
                .get()
                .await()
            
            val mistakeCards = querySnapshot.documents.mapNotNull { doc ->
                doc.toObject(MistakeCard::class.java)
            }.sortedWith(compareBy<MistakeCard> { it.isResolved }.thenByDescending { it.createdAt })
            
            Result.success(mistakeCards)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    // Check if student has any unresolved mistake cards
    suspend fun hasUnresolvedMistakeCards(studentId: String): Result<Boolean> {
        return try {
            val querySnapshot = mistakeCardsCollection
                .whereEqualTo("studentId", studentId)
                .whereEqualTo("isResolved", false)
                .limit(1)
                .get()
                .await()
            
            Result.success(querySnapshot.documents.isNotEmpty())
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    // Get the most common mistakes across all students (for teachers)
    suspend fun getMostCommonMistakes(limit: Int = 2): Result<List<MistakeCard>> {
        return try {
            println("🔍 Fetching most common mistakes with limit: $limit")
            
            // Get all mistake cards from Firebase
            val querySnapshot = mistakeCardsCollection
                .get()
                .await()
            
            val allMistakeCards = querySnapshot.documents.mapNotNull { doc ->
                try {
                    doc.toObject(MistakeCard::class.java)
                } catch (e: Exception) {
                    println("⚠️ Failed to parse mistake card: ${e.message}")
                    null
                }
            }
            
            println("📊 Total mistake cards found: ${allMistakeCards.size}")
            
            if (allMistakeCards.isEmpty()) {
                println("📭 No mistake cards found")
                return Result.success(emptyList())
            }
            
            // Group by question text and count occurrences
            val questionFrequency = allMistakeCards
                .filter { it.questionText.isNotBlank() } // Only include valid questions
                .groupBy { it.questionText }
                .mapValues { (_, cards) -> cards.size }
            
            println("🔢 Question frequency map: ${questionFrequency.size} unique questions")
            
            // Get the most frequent questions
            val mostCommonQuestions = questionFrequency
                .toList()
                .sortedByDescending { (_, count) -> count }
                .take(limit)
            
            println("🏆 Most common questions: ${mostCommonQuestions.map { "${it.first.take(50)}... (${it.second} times)" }}")
            
            // Get representative mistake cards for the most common questions
            val mostCommonMistakes = mostCommonQuestions.mapNotNull { (questionText, count) ->
                val representativeCard = allMistakeCards.find { it.questionText == questionText }
                representativeCard?.let { card ->
                    // Add frequency information to the card for display purposes
                    card.copy(
                        explanation = if (card.explanation.isNotBlank()) {
                            "${card.explanation} (Incorrect ${count} times)"
                        } else {
                            "This question was answered incorrectly ${count} times by students."
                        }
                    )
                }
            }
            
            println("✅ Returning ${mostCommonMistakes.size} most common mistakes")
            Result.success(mostCommonMistakes)
            
        } catch (e: Exception) {
            println("❌ Error fetching most common mistakes: ${e.message}")
            e.printStackTrace()
            Result.failure(e)
        }
    }
}
