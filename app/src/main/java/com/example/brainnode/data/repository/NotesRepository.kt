package com.example.brainnode.data.repository

import com.example.brainnode.data.firebase.FirebaseNotesService
import com.example.brainnode.data.models.Note
import com.example.brainnode.data.models.Subject

class NotesRepository {
    private val notesService = FirebaseNotesService()
    
    suspend fun createNote(note: Note): Result<String> {
        return notesService.createNote(note)
    }
    
    suspend fun updateNote(note: Note): Result<Unit> {
        return notesService.updateNote(note)
    }
    
    suspend fun deleteNote(noteId: String): Result<Unit> {
        return notesService.deleteNote(noteId)
    }
    
    suspend fun getNoteById(noteId: String): Result<Note?> {
        return notesService.getNoteById(noteId)
    }
    
    suspend fun getNotesByTeacher(teacherId: String): Result<List<Note>> {
        return notesService.getNotesByTeacher(teacherId)
    }
    
    suspend fun getNotesBySubject(subject: Subject): Result<List<Note>> {
        return notesService.getNotesBySubject(subject)
    }
    
    suspend fun getPublishedNotes(): Result<List<Note>> {
        return notesService.getPublishedNotes()
    }
    
    suspend fun publishNote(noteId: String): Result<Unit> {
        return notesService.publishNote(noteId)
    }
    
    suspend fun unpublishNote(noteId: String): Result<Unit> {
        return notesService.unpublishNote(noteId)
    }
    
    suspend fun searchNotes(query: String, subject: Subject? = null): Result<List<Note>> {
        return notesService.searchNotes(query, subject)
    }
}
