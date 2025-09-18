package com.example.brainnode.teacher.addquizzes

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import com.example.brainnode.R

class TeacherCreateQuestionFragment : Fragment() {

    private var currentQuestionNumber = 1
    private var totalQuestions = 5
    private var subjectName = ""

    companion object {
        private const val ARG_QUESTION_NUMBER = "question_number"
        private const val ARG_TOTAL_QUESTIONS = "total_questions"
        private const val ARG_SUBJECT_NAME = "subject_name"

        fun newInstance(questionNumber: Int, totalQuestions: Int, subjectName: String): TeacherCreateQuestionFragment {
            val fragment = TeacherCreateQuestionFragment()
            val args = Bundle()
            args.putInt(ARG_QUESTION_NUMBER, questionNumber)
            args.putInt(ARG_TOTAL_QUESTIONS, totalQuestions)
            args.putString(ARG_SUBJECT_NAME, subjectName)
            fragment.arguments = args
            return fragment
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            currentQuestionNumber = it.getInt(ARG_QUESTION_NUMBER, 1)
            totalQuestions = it.getInt(ARG_TOTAL_QUESTIONS, 5)
            subjectName = it.getString(ARG_SUBJECT_NAME, "")
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_teacher_create_question, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        setupViews(view)
        setupClickListeners(view)
    }

    private fun setupViews(view: View) {
        val tvQuestionNumber = view.findViewById<TextView>(R.id.tvQuestionNumber)
        val etQuestion = view.findViewById<EditText>(R.id.etQuestion)
        
        tvQuestionNumber.text = "Question $currentQuestionNumber of $totalQuestions"
        etQuestion.hint = "$currentQuestionNumber. Add the question..."
    }

    private fun setupClickListeners(view: View) {
        val btnNext = view.findViewById<Button>(R.id.btnNext)
        
        btnNext.setOnClickListener {
            if (validateInputs(view)) {
                // Save current question data (you can implement this later)
                saveQuestionData(view)
                
                if (currentQuestionNumber < totalQuestions) {
                    // Navigate to next question
                    navigateToNextQuestion()
                } else {
                    // All questions completed, navigate back to quiz list or home
                    navigateToQuizCompletion()
                }
            }
        }
    }

    private fun validateInputs(view: View): Boolean {
        val etQuestion = view.findViewById<EditText>(R.id.etQuestion)
        val etCorrectAnswer = view.findViewById<EditText>(R.id.etCorrectAnswer)
        val etWrongAnswer1 = view.findViewById<EditText>(R.id.etWrongAnswer1)
        val etWrongAnswer2 = view.findViewById<EditText>(R.id.etWrongAnswer2)
        val etWrongAnswer3 = view.findViewById<EditText>(R.id.etWrongAnswer3)
        val etMistakeDescription = view.findViewById<EditText>(R.id.etMistakeDescription)

        // Basic validation - you can enhance this
        if (etQuestion.text.toString().trim().isEmpty()) {
            etQuestion.error = "Please enter a question"
            return false
        }
        
        if (etCorrectAnswer.text.toString().trim().isEmpty()) {
            etCorrectAnswer.error = "Please enter the correct answer"
            return false
        }
        
        if (etWrongAnswer1.text.toString().trim().isEmpty()) {
            etWrongAnswer1.error = "Please enter a wrong answer"
            return false
        }

        return true
    }

    private fun saveQuestionData(view: View) {
        // TODO: Implement data saving logic
        // You can save to database, shared preferences, or pass to ViewModel
        val etQuestion = view.findViewById<EditText>(R.id.etQuestion)
        val etCorrectAnswer = view.findViewById<EditText>(R.id.etCorrectAnswer)
        val etWrongAnswer1 = view.findViewById<EditText>(R.id.etWrongAnswer1)
        val etWrongAnswer2 = view.findViewById<EditText>(R.id.etWrongAnswer2)
        val etWrongAnswer3 = view.findViewById<EditText>(R.id.etWrongAnswer3)
        val etMistakeDescription = view.findViewById<EditText>(R.id.etMistakeDescription)

        // Example: Create a data class and save it
        // val questionData = QuestionData(
        //     question = etQuestion.text.toString(),
        //     correctAnswer = etCorrectAnswer.text.toString(),
        //     wrongAnswers = listOf(
        //         etWrongAnswer1.text.toString(),
        //         etWrongAnswer2.text.toString(),
        //         etWrongAnswer3.text.toString()
        //     ),
        //     mistakeDescription = etMistakeDescription.text.toString()
        // )
    }

    private fun navigateToNextQuestion() {
        val nextQuestionFragment = newInstance(
            currentQuestionNumber + 1,
            totalQuestions,
            subjectName
        )
        
        val transaction: FragmentTransaction = parentFragmentManager.beginTransaction()
        transaction.replace(R.id.fragment_teacher_home, nextQuestionFragment)
        transaction.addToBackStack(null)
        transaction.commit()
    }

    private fun navigateToQuizCompletion() {
        // Navigate back to teacher home or show completion message
        parentFragmentManager.popBackStack()
        // You can also show a success message or navigate to a completion screen
    }
}
