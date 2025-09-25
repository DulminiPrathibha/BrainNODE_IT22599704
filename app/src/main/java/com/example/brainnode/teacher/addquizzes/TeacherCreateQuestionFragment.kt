package com.example.brainnode.teacher.addquizzes

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import androidx.lifecycle.lifecycleScope
import com.example.brainnode.R
import com.example.brainnode.data.models.*
import com.example.brainnode.data.repository.QuizRepository
import com.example.brainnode.data.repository.AuthRepository
import kotlinx.coroutines.launch
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.util.UUID

class TeacherCreateQuestionFragment : Fragment() {

    private var currentQuestionNumber = 1
    private var totalQuestions = 5
    private var subjectName = ""
    private val quizRepository = QuizRepository()
    private val authRepository = AuthRepository()
    private val createdQuestions = mutableListOf<QuizQuestion>()
    private lateinit var subjectEnum: Subject

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
        
        // Convert subject name to Subject enum
        subjectEnum = when (subjectName.lowercase()) {
            "operating system", "os" -> Subject.OPERATING_SYSTEM
            "statistics", "stat" -> Subject.STATISTICS
            "programming", "prog" -> Subject.PROGRAMMING
            else -> Subject.OPERATING_SYSTEM
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
        val etQuestion = view.findViewById<EditText>(R.id.etQuestion)
        val etCorrectAnswer = view.findViewById<EditText>(R.id.etCorrectAnswer)
        val etWrongAnswer1 = view.findViewById<EditText>(R.id.etWrongAnswer1)
        val etWrongAnswer2 = view.findViewById<EditText>(R.id.etWrongAnswer2)
        val etWrongAnswer3 = view.findViewById<EditText>(R.id.etWrongAnswer3)

        val questionText = etQuestion.text.toString().trim()
        val correctAnswerText = etCorrectAnswer.text.toString().trim()
        val wrongAnswers = listOf(
            etWrongAnswer1.text.toString().trim(),
            etWrongAnswer2.text.toString().trim(),
            etWrongAnswer3.text.toString().trim()
        ).filter { it.isNotEmpty() }

        // Create options with correct answer marked
        val allOptions = mutableListOf<String>()
        allOptions.add(correctAnswerText)
        allOptions.addAll(wrongAnswers)
        allOptions.shuffle() // Randomize option order

        val correctAnswerIndex = allOptions.indexOf(correctAnswerText)
        
        val options = allOptions.mapIndexed { index, text ->
            QuizOption(
                id = UUID.randomUUID().toString(),
                text = text,
                isCorrect = index == correctAnswerIndex
            )
        }

        val correctAnswerId = options[correctAnswerIndex].id

        val question = QuizQuestion(
            id = UUID.randomUUID().toString(),
            questionText = questionText,
            options = options,
            correctAnswerId = correctAnswerId,
            explanation = ""
        )

        createdQuestions.add(question)
    }

    private fun navigateToNextQuestion() {
        val nextQuestionFragment = newInstance(
            currentQuestionNumber + 1,
            totalQuestions,
            subjectName
        )
        
        parentFragmentManager.beginTransaction()
            .replace(R.id.fragment_teacher_home, nextQuestionFragment)
            .addToBackStack(null)
            .commit()
    }

    private fun navigateToQuizCompletion() {
        // Use simple approach with direct Firestore access
        lifecycleScope.launch {
            try {
                // Get current user info
                val currentUserResult = authRepository.getCurrentUser()
                val currentUser = currentUserResult.getOrNull()
                
                if (currentUser == null) {
                    Toast.makeText(requireContext(), "Please log in to create quizzes", Toast.LENGTH_LONG).show()
                    return@launch
                }

                // Validate that we have questions
                if (createdQuestions.isEmpty()) {
                    Toast.makeText(requireContext(), "No questions to save", Toast.LENGTH_LONG).show()
                    return@launch
                }

                // Show saving message
                Toast.makeText(requireContext(), "Saving quiz...", Toast.LENGTH_SHORT).show()

                // Create simple quiz data for Firestore
                val quizData = hashMapOf(
                    "id" to UUID.randomUUID().toString(),
                    "teacherId" to currentUser.uid,
                    "teacherName" to currentUser.name,
                    "subject" to subjectEnum.name,
                    "title" to "$subjectName Quiz",
                    "description" to "Quiz created by ${currentUser.name}",
                    "totalQuestions" to createdQuestions.size,
                    "timeLimit" to 30,
                    "createdAt" to System.currentTimeMillis(),
                    "isPublished" to true,
                    "questions" to createdQuestions.map { question ->
                        hashMapOf(
                            "id" to question.id,
                            "questionText" to question.questionText,
                            "options" to question.options.map { option ->
                                hashMapOf(
                                    "id" to option.id,
                                    "text" to option.text,
                                    "isCorrect" to option.isCorrect
                                )
                            },
                            "correctAnswerId" to question.correctAnswerId,
                            "explanation" to question.explanation
                        )
                    }
                )

                // Save directly to Firestore
                val firestore = com.google.firebase.firestore.FirebaseFirestore.getInstance()
                val docRef = firestore.collection("quizzes").document(quizData["id"] as String)
                
                docRef.set(quizData)
                    .addOnSuccessListener {
                        try {
                            if (isAdded && context != null) {
                                Toast.makeText(requireContext(), 
                                    "Quiz saved successfully!", 
                                    Toast.LENGTH_LONG).show()

                                // Simple navigation without complex back stack management
                                android.os.Handler(android.os.Looper.getMainLooper()).postDelayed({
                                    try {
                                        if (isAdded && context != null && parentFragmentManager != null) {
                                            val quizListFragment = TeacherQuizListFragment()
                                            
                                            parentFragmentManager.beginTransaction()
                                                .replace(R.id.fragment_teacher_home, quizListFragment)
                                                .commitAllowingStateLoss()
                                        }
                                    } catch (e: Exception) {
                                        // Navigation failed, but quiz is saved
                                    }
                                }, 1000) // 1 second delay
                            }
                        } catch (e: Exception) {
                            // Ignore toast errors
                        }
                    }
                    .addOnFailureListener { exception ->
                        val errorMessage = "Failed to save quiz: ${exception.message}"
                        Toast.makeText(requireContext(), errorMessage, Toast.LENGTH_LONG).show()
                        
                        // Show what to do in Firebase Console
                        Toast.makeText(requireContext(), 
                            "Please check Firebase Console security rules", 
                            Toast.LENGTH_LONG).show()
                    }

            } catch (e: Exception) {
                Toast.makeText(requireContext(), 
                    "Error: ${e.message}", 
                    Toast.LENGTH_LONG).show()
            }
        }
    }
}
