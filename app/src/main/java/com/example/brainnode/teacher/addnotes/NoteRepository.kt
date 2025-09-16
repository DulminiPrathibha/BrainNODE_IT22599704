package com.example.brainnode.teacher.addnotes

import android.content.Context
import android.content.SharedPreferences

class NoteRepository(context: Context) {
    
    private val sharedPreferences: SharedPreferences = 
        context.getSharedPreferences("notes_prefs", Context.MODE_PRIVATE)
    
    companion object {
        private const val NOTES_KEY = "saved_notes"
    }
    
    fun saveNote(note: Note): Boolean {
        return try {
            val existingNotes = getAllNotes().toMutableList()
            existingNotes.add(note)
            
            // Simple string concatenation instead of JSON
            val notesString = existingNotes.joinToString("|||") { 
                "${it.id}::${it.subjectCode}::${it.content}::${it.summary}::${it.createdAt}::${it.teacherId}"
            }
            sharedPreferences.edit()
                .putString(NOTES_KEY, notesString)
                .apply()
            true
        } catch (e: Exception) {
            false
        }
    }
    
    fun getAllNotes(): List<Note> {
        return try {
            val notesString = sharedPreferences.getString(NOTES_KEY, null)
            if (notesString != null && notesString.isNotEmpty()) {
                notesString.split("|||").mapNotNull { noteString ->
                    val parts = noteString.split("::")
                    if (parts.size >= 6) {
                        Note(
                            id = parts[0],
                            subjectCode = parts[1],
                            content = parts[2],
                            summary = parts[3],
                            createdAt = parts[4].toLongOrNull() ?: System.currentTimeMillis(),
                            teacherId = parts[5]
                        )
                    } else null
                }
            } else {
                emptyList()
            }
        } catch (e: Exception) {
            emptyList()
        }
    }
    
    fun getNotesBySubject(subjectCode: String): List<Note> {
        return getAllNotes().filter { it.subjectCode.equals(subjectCode, ignoreCase = true) }
    }
    
    fun getNotesByTeacher(teacherId: String): List<Note> {
        return getAllNotes().filter { it.teacherId == teacherId }
    }
    
    fun deleteNote(noteId: String): Boolean {
        return try {
            val existingNotes = getAllNotes().toMutableList()
            val noteToRemove = existingNotes.find { it.id == noteId }
            
            if (noteToRemove != null) {
                existingNotes.remove(noteToRemove)
                val notesString = existingNotes.joinToString("|||") { 
                    "${it.id}::${it.subjectCode}::${it.content}::${it.summary}::${it.createdAt}::${it.teacherId}"
                }
                sharedPreferences.edit()
                    .putString(NOTES_KEY, notesString)
                    .apply()
                true
            } else {
                false
            }
        } catch (e: Exception) {
            false
        }
    }
    
    fun updateNote(updatedNote: Note): Boolean {
        return try {
            val existingNotes = getAllNotes().toMutableList()
            val index = existingNotes.indexOfFirst { it.id == updatedNote.id }
            
            if (index != -1) {
                existingNotes[index] = updatedNote.copy(updatedAt = System.currentTimeMillis())
                val notesString = existingNotes.joinToString("|||") { 
                    "${it.id}::${it.subjectCode}::${it.content}::${it.summary}::${it.createdAt}::${it.teacherId}"
                }
                sharedPreferences.edit()
                    .putString(NOTES_KEY, notesString)
                    .apply()
                true
            } else {
                false
            }
        } catch (e: Exception) {
            false
        }
    }
}
