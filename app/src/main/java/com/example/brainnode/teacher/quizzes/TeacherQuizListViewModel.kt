package com.example.brainnode.teacher.quizzes

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.brainnode.R
import com.example.brainnode.teacher.quizzes.models.QuizSubject

class TeacherQuizListViewModel : ViewModel() {

    private val _subjects = MutableLiveData<List<QuizSubject>>()
    val subjects: LiveData<List<QuizSubject>> = _subjects

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private val _isEmpty = MutableLiveData<Boolean>()
    val isEmpty: LiveData<Boolean> = _isEmpty

    init {
        loadSubjects()
    }

    private fun loadSubjects() {
        _isLoading.value = true
        
        // TODO: Replace with actual database call
        // For now, using mock data since database is not connected
        val mockSubjects = listOf(
            QuizSubject(
                id = "1",
                name = "Operating System",
                iconResource = R.drawable.ic_subject_default,
                quizCount = 0
            ),
            QuizSubject(
                id = "2",
                name = "Statistics",
                iconResource = R.drawable.ic_subject_default,
                quizCount = 0
            ),
            QuizSubject(
                id = "3",
                name = "Database",
                iconResource = R.drawable.ic_subject_default,
                quizCount = 0
            )
        )

        _subjects.value = mockSubjects
        _isEmpty.value = mockSubjects.isEmpty()
        _isLoading.value = false
    }

    fun refreshSubjects() {
        loadSubjects()
    }

    fun addNewQuiz(subjectId: String) {
        // TODO: Navigate to quiz creation flow
        // This will be called when FAB is clicked
    }

    fun onSubjectClick(subject: QuizSubject) {
        // TODO: Navigate to quiz details for this subject
        // This will show all quizzes for the selected subject
    }
}
