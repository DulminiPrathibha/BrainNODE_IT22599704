package com.example.brainnode.teacher.addquizzes

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.brainnode.R

class TeacherAddQuizzesFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_teacher_addquizzes, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        setupClickListeners(view)
    }
    
    private fun setupClickListeners(view: View) {
        val btnNext = view.findViewById<android.widget.Button>(R.id.btnNext)
        val btnViewQuizzes = view.findViewById<android.widget.Button>(R.id.btnViewQuizzes)
        val etSubjectName = view.findViewById<android.widget.EditText>(R.id.etSubjectName)
        val etNumberOfQuestions = view.findViewById<android.widget.EditText>(R.id.etNumberOfQuestions)
        
        btnNext.setOnClickListener {
            val subjectName = etSubjectName.text.toString().trim()
            val numberOfQuestionsStr = etNumberOfQuestions.text.toString().trim()
            
            if (validateInputs(subjectName, numberOfQuestionsStr)) {
                val numberOfQuestions = numberOfQuestionsStr.toInt()
                navigateToCreateQuestion(subjectName, numberOfQuestions)
            }
        }
        
        btnViewQuizzes.setOnClickListener {
            // TODO: Navigate to view quizzes screen
        }
    }
    
    private fun validateInputs(subjectName: String, numberOfQuestionsStr: String): Boolean {
        if (subjectName.isEmpty()) {
            // Show error for subject name
            return false
        }
        
        if (numberOfQuestionsStr.isEmpty()) {
            // Show error for number of questions
            return false
        }
        
        val numberOfQuestions = numberOfQuestionsStr.toIntOrNull()
        if (numberOfQuestions == null || numberOfQuestions <= 0) {
            // Show error for invalid number
            return false
        }
        
        return true
    }
    
    private fun navigateToCreateQuestion(subjectName: String, totalQuestions: Int) {
        val createQuestionFragment = TeacherCreateQuestionFragment.newInstance(
            questionNumber = 1,
            totalQuestions = totalQuestions,
            subjectName = subjectName
        )
        
        val transaction = parentFragmentManager.beginTransaction()
        transaction.replace(R.id.fragment_teacher_home, createQuestionFragment)
        transaction.addToBackStack(null)
        transaction.commit()
    }
}