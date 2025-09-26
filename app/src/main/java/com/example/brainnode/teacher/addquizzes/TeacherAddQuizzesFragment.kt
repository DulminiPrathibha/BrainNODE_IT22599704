package com.example.brainnode.teacher.addquizzes

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
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
        val btnNext = view.findViewById<Button>(R.id.btnNext)
        val btnViewQuizzes = view.findViewById<Button>(R.id.btnViewQuizzes)
        val etSubjectName = view.findViewById<EditText>(R.id.etSubjectName)
        val etNumberOfQuestions = view.findViewById<EditText>(R.id.etNumberOfQuestions)
        
        btnNext.setOnClickListener {
            val subjectName = etSubjectName.text.toString().trim()
            val numberOfQuestionsStr = etNumberOfQuestions.text.toString().trim()
            
            if (validateInputs(subjectName, numberOfQuestionsStr)) {
                val numberOfQuestions = numberOfQuestionsStr.toInt()
                navigateToCreateQuestion(subjectName, numberOfQuestions)
            }
        }
        
        btnViewQuizzes.setOnClickListener {
            navigateToQuizList()
        }
    }
    
    private fun validateInputs(subjectName: String, numberOfQuestionsStr: String): Boolean {
        if (subjectName.isEmpty()) {
            Toast.makeText(requireContext(), "Subject name is required", Toast.LENGTH_SHORT).show()
            return false
        }
        
        if (numberOfQuestionsStr.isEmpty()) {
            Toast.makeText(requireContext(), "Number of questions is required", Toast.LENGTH_SHORT).show()
            return false
        }
        
        val numberOfQuestions = numberOfQuestionsStr.toIntOrNull()
        if (numberOfQuestions == null || numberOfQuestions <= 0) {
            Toast.makeText(requireContext(), "Please enter a valid number", Toast.LENGTH_SHORT).show()
            return false
        }
        
        return true
    }
    
    private fun navigateToCreateQuestion(subjectName: String, totalQuestions: Int) {
        // Clear any previous quiz creation data
        val sharedPref = requireContext().getSharedPreferences("quiz_creation", android.content.Context.MODE_PRIVATE)
        sharedPref.edit().clear().apply()
        
        val createQuestionFragment = TeacherCreateQuestionFragment.newInstance(
            questionNumber = 1,
            totalQuestions = totalQuestions,
            subjectName = subjectName
        )
        
        parentFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, createQuestionFragment)
            .addToBackStack(null)
            .commit()
    }
    
    private fun navigateToQuizList() {
        val quizListFragment = TeacherQuizListFragment()
        
        parentFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, quizListFragment)
            .addToBackStack(null)
            .commit()
    }
}