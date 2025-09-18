package com.example.brainnode.teacher.addquizzes

import androidx.lifecycle.ViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.LiveData

data class QuestionData(
    val questionNumber: Int,
    val question: String,
    val correctAnswer: String,
    val wrongAnswers: List<String>,
    val mistakeDescription: String
)

data class QuizData(
    val subjectName: String,
    val totalQuestions: Int,
    val questions: MutableList<QuestionData> = mutableListOf()
)

class TeacherCreateQuestionViewModel : ViewModel() {
    
    private val _currentQuiz = MutableLiveData<QuizData>()
    val currentQuiz: LiveData<QuizData> = _currentQuiz
    
    private val _currentQuestionNumber = MutableLiveData<Int>()
    val currentQuestionNumber: LiveData<Int> = _currentQuestionNumber
    
    fun initializeQuiz(subjectName: String, totalQuestions: Int) {
        _currentQuiz.value = QuizData(subjectName, totalQuestions)
        _currentQuestionNumber.value = 1
    }
    
    fun saveQuestion(questionData: QuestionData) {
        val quiz = _currentQuiz.value ?: return
        
        // Remove existing question with same number if it exists
        quiz.questions.removeAll { it.questionNumber == questionData.questionNumber }
        
        // Add the new question
        quiz.questions.add(questionData)
        
        _currentQuiz.value = quiz
    }
    
    fun moveToNextQuestion() {
        val currentNumber = _currentQuestionNumber.value ?: 1
        val totalQuestions = _currentQuiz.value?.totalQuestions ?: 5
        
        if (currentNumber < totalQuestions) {
            _currentQuestionNumber.value = currentNumber + 1
        }
    }
    
    fun isLastQuestion(): Boolean {
        val currentNumber = _currentQuestionNumber.value ?: 1
        val totalQuestions = _currentQuiz.value?.totalQuestions ?: 5
        return currentNumber >= totalQuestions
    }
    
    fun getQuestionData(questionNumber: Int): QuestionData? {
        return _currentQuiz.value?.questions?.find { it.questionNumber == questionNumber }
    }
    
    fun getAllQuestions(): List<QuestionData> {
        return _currentQuiz.value?.questions ?: emptyList()
    }
    
    fun isQuizComplete(): Boolean {
        val quiz = _currentQuiz.value ?: return false
        return quiz.questions.size == quiz.totalQuestions
    }
}
