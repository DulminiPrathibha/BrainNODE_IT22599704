package com.example.brainnode.data.firebase

import com.example.brainnode.data.models.Note
import com.example.brainnode.data.models.Subject
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.coroutines.tasks.await

class FirebaseNotesService {
    private val firestore = FirebaseFirestore.getInstance()
    private val notesCollection = firestore.collection("notes")
    
    suspend fun createNote(note: Note): Result<String> {
        return try {
            val docRef = notesCollection.add(note).await()
            val noteWithId = note.copy(id = docRef.id)
            docRef.set(noteWithId).await()
            Result.success(docRef.id)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    suspend fun updateNote(note: Note): Result<Unit> {
        return try {
            notesCollection.document(note.id)
                .set(note.copy(updatedAt = System.currentTimeMillis()))
                .await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    suspend fun deleteNote(noteId: String): Result<Unit> {
        return try {
            notesCollection.document(noteId).delete().await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    suspend fun getNoteById(noteId: String): Result<Note?> {
        return try {
            val doc = notesCollection.document(noteId).get().await()
            val note = doc.toObject(Note::class.java)
            Result.success(note)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    suspend fun getNotesByTeacher(teacherId: String): Result<List<Note>> {
        return try {
            val querySnapshot = notesCollection
                .whereEqualTo("teacherId", teacherId)
                .orderBy("createdAt", Query.Direction.DESCENDING)
                .get()
                .await()
            
            val notes = querySnapshot.documents.mapNotNull { doc ->
                doc.toObject(Note::class.java)
            }
            Result.success(notes)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    suspend fun getNotesBySubject(subject: Subject): Result<List<Note>> {
        return try {
            val querySnapshot = notesCollection
                .whereEqualTo("subject", subject)
                .whereEqualTo("isPublished", true)
                .orderBy("createdAt", Query.Direction.DESCENDING)
                .get()
                .await()
            
            val notes = querySnapshot.documents.mapNotNull { doc ->
                doc.toObject(Note::class.java)
            }
            Result.success(notes)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    suspend fun getPublishedNotes(): Result<List<Note>> {
        return try {
            val querySnapshot = notesCollection
                .whereEqualTo("isPublished", true)
                .orderBy("createdAt", Query.Direction.DESCENDING)
                .get()
                .await()
            
            val notes = querySnapshot.documents.mapNotNull { doc ->
                doc.toObject(Note::class.java)
            }
            Result.success(notes)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    suspend fun publishNote(noteId: String): Result<Unit> {
        return try {
            notesCollection.document(noteId)
                .update(
                    mapOf(
                        "isPublished" to true,
                        "updatedAt" to System.currentTimeMillis()
                    )
                )
                .await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    suspend fun unpublishNote(noteId: String): Result<Unit> {
        return try {
            notesCollection.document(noteId)
                .update(
                    mapOf(
                        "isPublished" to false,
                        "updatedAt" to System.currentTimeMillis()
                    )
                )
                .await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    suspend fun searchNotes(query: String, subject: Subject? = null): Result<List<Note>> {
        return try {
            var firestoreQuery = notesCollection
                .whereEqualTo("isPublished", true)
            
            if (subject != null) {
                firestoreQuery = firestoreQuery.whereEqualTo("subject", subject)
            }
            
            val querySnapshot = firestoreQuery.get().await()
            
            val notes = querySnapshot.documents.mapNotNull { doc ->
                doc.toObject(Note::class.java)
            }.filter { note ->
                note.lessonTitle.contains(query, ignoreCase = true) ||
                note.content.contains(query, ignoreCase = true) ||
                note.summary.contains(query, ignoreCase = true)
            }
            
            Result.success(notes)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
