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
    
    // Edit mode properties
    private var isEditMode = false
    private var quizId = ""
    private var existingQuizData: Map<String, Any>? = null
    private var currentQuestionIndex = 0

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
        arguments?.let { args ->
            // Check if this is edit mode
            val mode = args.getString("mode", "create")
            isEditMode = mode == "edit"
            
            if (isEditMode) {
                // Edit mode setup
                quizId = args.getString("quiz_id", "")
                currentQuestionIndex = args.getInt("question_index", 0)
                val quizDataJson = args.getString("quiz_data", "")
                
                if (quizDataJson.isNotEmpty()) {
                    try {
                        existingQuizData = com.google.gson.Gson().fromJson(
                            quizDataJson, 
                            object : com.google.gson.reflect.TypeToken<Map<String, Any>>() {}.type
                        )
                        
                        // Extract quiz info from existing data
                        existingQuizData?.let { data ->
                            subjectName = data["subject"] as? String ?: ""
                            val questions = data["questions"] as? List<Map<String, Any>> ?: emptyList()
                            totalQuestions = questions.size
                            currentQuestionNumber = currentQuestionIndex + 1
                        }
                    } catch (e: Exception) {
                        Toast.makeText(requireContext(), "Error loading quiz data", Toast.LENGTH_SHORT).show()
                    }
                }
            } else {
                // Create mode setup
                currentQuestionNumber = args.getInt(ARG_QUESTION_NUMBER, 1)
                totalQuestions = args.getInt(ARG_TOTAL_QUESTIONS, 5)
                subjectName = args.getString(ARG_SUBJECT_NAME, "")
            }
        }
        
        // Convert subject name to Subject enum
        subjectEnum = when (subjectName.lowercase()) {
            "operating_system", "operating system", "os" -> Subject.OPERATING_SYSTEM
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
        val etCorrectAnswer = view.findViewById<EditText>(R.id.etCorrectAnswer)
        val etWrongAnswer1 = view.findViewById<EditText>(R.id.etWrongAnswer1)
        val etWrongAnswer2 = view.findViewById<EditText>(R.id.etWrongAnswer2)
        val etWrongAnswer3 = view.findViewById<EditText>(R.id.etWrongAnswer3)
        val etMistakeDescription = view.findViewById<EditText>(R.id.etMistakeDescription)
        
        tvQuestionNumber.text = "Question $currentQuestionNumber of $totalQuestions"
        
        if (isEditMode) {
            etQuestion.hint = "Edit question $currentQuestionNumber..."
            
            // Pre-fill with existing data
            existingQuizData?.let { quizData ->
                val questions = quizData["questions"] as? List<Map<String, Any>> ?: emptyList()
                if (currentQuestionIndex < questions.size) {
                    val currentQuestion = questions[currentQuestionIndex]
                    
                    // Fill question text
                    etQuestion.setText(currentQuestion["questionText"] as? String ?: "")
                    
                    // Fill answers
                    val options = currentQuestion["options"] as? List<Map<String, Any>> ?: emptyList()
                    var correctAnswer = ""
                    val wrongAnswers = mutableListOf<String>()
                    
                    for (option in options) {
                        val text = option["text"] as? String ?: ""
                        val isCorrect = option["isCorrect"] as? Boolean ?: false
                        
                        if (isCorrect) {
                            correctAnswer = text
                        } else {
                            wrongAnswers.add(text)
                        }
                    }
                    
                    etCorrectAnswer.setText(correctAnswer)
                    if (wrongAnswers.size > 0) etWrongAnswer1.setText(wrongAnswers[0])
                    if (wrongAnswers.size > 1) etWrongAnswer2.setText(wrongAnswers[1])
                    if (wrongAnswers.size > 2) etWrongAnswer3.setText(wrongAnswers[2])
                    
                    // Fill explanation
                    etMistakeDescription.setText(currentQuestion["explanation"] as? String ?: "")
                }
            }
        } else {
            etQuestion.hint = "$currentQuestionNumber. Add the question..."
        }
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
        // Save current question before navigating
        saveCurrentQuestionToSharedPreferences()
        
        if (isEditMode) {
            // In edit mode, navigate to next question with edit data
            val nextQuestionFragment = TeacherCreateQuestionFragment()
            val bundle = Bundle().apply {
                putString("quiz_id", quizId)
                putString("mode", "edit")
                putInt("question_index", currentQuestionIndex + 1)
                putString("quiz_data", com.google.gson.Gson().toJson(existingQuizData))
            }
            nextQuestionFragment.arguments = bundle
            
            parentFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, nextQuestionFragment)
                .addToBackStack("edit_question")
                .commit()
        } else {
            // Create mode - use existing logic
            val nextQuestionFragment = newInstance(
                currentQuestionNumber + 1,
                totalQuestions,
                subjectName
            )
            
            parentFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, nextQuestionFragment)
                .addToBackStack(null)
                .commit()
        }
    }

    private fun navigateToQuizCompletion() {
        if (isEditMode) {
            // In edit mode, update the existing quiz
            updateExistingQuiz()
        } else {
            // In create mode, save this question and create the quiz
            saveCurrentQuestionToSharedPreferences()
            createNewQuizWithAllQuestions()
        }
    }
    
    private fun updateExistingQuiz() {
        lifecycleScope.launch {
            try {
                Toast.makeText(requireContext(), "Updating quiz...", Toast.LENGTH_SHORT).show()
                
                // Update the current question in the existing quiz data
                saveCurrentQuestionToExistingData()
                
                // Update the quiz in Firestore
                val firestore = com.google.firebase.firestore.FirebaseFirestore.getInstance()
                firestore.collection("quizzes").document(quizId)
                    .set(existingQuizData!!)
                    .addOnSuccessListener {
                        Toast.makeText(requireContext(), "Quiz updated successfully!", Toast.LENGTH_LONG).show()
                        navigateBackToQuizList()
                    }
                    .addOnFailureListener { exception ->
                        Toast.makeText(requireContext(), "Failed to update quiz: ${exception.message}", Toast.LENGTH_LONG).show()
                    }
                    
            } catch (e: Exception) {
                Toast.makeText(requireContext(), "Error updating quiz: ${e.message}", Toast.LENGTH_LONG).show()
            }
        }
    }
    
    private fun createNewQuiz() {
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
                    "totalQuestions" to totalQuestions,
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
                        Toast.makeText(requireContext(), "Quiz saved successfully!", Toast.LENGTH_LONG).show()
                        navigateBackToQuizList()
                    }
                    .addOnFailureListener { exception ->
                        Toast.makeText(requireContext(), "Failed to save quiz: ${exception.message}", Toast.LENGTH_LONG).show()
                    }

            } catch (e: Exception) {
                Toast.makeText(requireContext(), "Error: ${e.message}", Toast.LENGTH_LONG).show()
            }
        }
    }
    
    private fun saveCurrentQuestionToExistingData() {
        // Get current question data from the form
        val view = requireView()
        val etQuestion = view.findViewById<EditText>(R.id.etQuestion)
        val etCorrectAnswer = view.findViewById<EditText>(R.id.etCorrectAnswer)
        val etWrongAnswer1 = view.findViewById<EditText>(R.id.etWrongAnswer1)
        val etWrongAnswer2 = view.findViewById<EditText>(R.id.etWrongAnswer2)
        val etWrongAnswer3 = view.findViewById<EditText>(R.id.etWrongAnswer3)
        val etMistakeDescription = view.findViewById<EditText>(R.id.etMistakeDescription)
        
        val questionText = etQuestion.text.toString().trim()
        val correctAnswerText = etCorrectAnswer.text.toString().trim()
        val wrongAnswers = listOf(
            etWrongAnswer1.text.toString().trim(),
            etWrongAnswer2.text.toString().trim(),
            etWrongAnswer3.text.toString().trim()
        ).filter { it.isNotEmpty() }
        
        // Create options
        val allOptions = mutableListOf<String>()
        allOptions.add(correctAnswerText)
        allOptions.addAll(wrongAnswers)
        allOptions.shuffle()
        
        val correctAnswerIndex = allOptions.indexOf(correctAnswerText)
        val options = allOptions.mapIndexed { index, text ->
            hashMapOf(
                "id" to UUID.randomUUID().toString(),
                "text" to text,
                "isCorrect" to (index == correctAnswerIndex)
            )
        }
        
        // Update the question in existing quiz data
        existingQuizData?.let { data ->
            val questions = (data["questions"] as? MutableList<MutableMap<String, Any>>) ?: mutableListOf()
            
            val updatedQuestion = hashMapOf(
                "id" to UUID.randomUUID().toString(),
                "questionText" to questionText,
                "options" to options,
                "correctAnswerId" to options[correctAnswerIndex]["id"] as String,
                "explanation" to etMistakeDescription.text.toString().trim()
            )
            
            if (currentQuestionIndex < questions.size) {
                questions[currentQuestionIndex] = updatedQuestion
            } else {
                questions.add(updatedQuestion)
            }
            
            (data as MutableMap<String, Any>)["questions"] = questions
        }
    }
    
    private fun navigateBackToQuizList() {
        try {
            android.os.Handler(android.os.Looper.getMainLooper()).postDelayed({
                try {
                    if (isAdded && context != null && parentFragmentManager != null) {
                        val quizListFragment = TeacherQuizListFragment()
                        
                        parentFragmentManager.beginTransaction()
                            .replace(R.id.fragment_container, quizListFragment)
                            .commitAllowingStateLoss()
                    }
                } catch (e: Exception) {
                    // Navigation failed
                }
            }, 1000)
        } catch (e: Exception) {
            // Ignore errors
        }
    }
    
    private fun saveCurrentQuestionToSharedPreferences() {
        try {
            val view = requireView()
            val etQuestion = view.findViewById<EditText>(R.id.etQuestion)
            val etCorrectAnswer = view.findViewById<EditText>(R.id.etCorrectAnswer)
            val etWrongAnswer1 = view.findViewById<EditText>(R.id.etWrongAnswer1)
            val etWrongAnswer2 = view.findViewById<EditText>(R.id.etWrongAnswer2)
            val etWrongAnswer3 = view.findViewById<EditText>(R.id.etWrongAnswer3)
            val etMistakeDescription = view.findViewById<EditText>(R.id.etMistakeDescription)
            
            val questionText = etQuestion.text.toString().trim()
            val correctAnswerText = etCorrectAnswer.text.toString().trim()
            val wrongAnswers = listOf(
                etWrongAnswer1.text.toString().trim(),
                etWrongAnswer2.text.toString().trim(),
                etWrongAnswer3.text.toString().trim()
            ).filter { it.isNotEmpty() }
            
            // Create options
            val allOptions = mutableListOf<String>()
            allOptions.add(correctAnswerText)
            allOptions.addAll(wrongAnswers)
            allOptions.shuffle()
            
            val correctAnswerIndex = allOptions.indexOf(correctAnswerText)
            val options = allOptions.mapIndexed { index, text ->
                hashMapOf(
                    "id" to UUID.randomUUID().toString(),
                    "text" to text,
                    "isCorrect" to (index == correctAnswerIndex)
                )
            }
            
            val questionData = hashMapOf(
                "id" to UUID.randomUUID().toString(),
                "questionText" to questionText,
                "options" to options,
                "correctAnswerId" to options[correctAnswerIndex]["id"] as String,
                "explanation" to etMistakeDescription.text.toString().trim()
            )
            
            // Save to SharedPreferences
            val sharedPref = requireContext().getSharedPreferences("quiz_creation", android.content.Context.MODE_PRIVATE)
            val editor = sharedPref.edit()
            
            // Get existing questions
            val existingQuestionsJson = sharedPref.getString("questions", "[]")
            val existingQuestions = com.google.gson.Gson().fromJson(
                existingQuestionsJson, 
                object : com.google.gson.reflect.TypeToken<MutableList<Map<String, Any>>>() {}.type
            ) as MutableList<Map<String, Any>>
            
            // Add current question
            existingQuestions.add(questionData)
            
            // Save back to SharedPreferences
            val questionsJson = com.google.gson.Gson().toJson(existingQuestions)
            editor.putString("questions", questionsJson)
            editor.putString("subject", subjectName)
            editor.putInt("totalQuestions", totalQuestions)
            editor.apply()
            
        } catch (e: Exception) {
            Toast.makeText(requireContext(), "Error saving question: ${e.message}", Toast.LENGTH_SHORT).show()
        }
    }
    
    private fun createNewQuizWithAllQuestions() {
        lifecycleScope.launch {
            try {
                val currentUserResult = authRepository.getCurrentUser()
                val currentUser = currentUserResult.getOrNull()
                
                if (currentUser == null) {
                    Toast.makeText(requireContext(), "Please log in to create quizzes", Toast.LENGTH_LONG).show()
                    return@launch
                }

                Toast.makeText(requireContext(), "Saving quiz...", Toast.LENGTH_SHORT).show()

                // Get all questions from SharedPreferences
                val sharedPref = requireContext().getSharedPreferences("quiz_creation", android.content.Context.MODE_PRIVATE)
                val questionsJson = sharedPref.getString("questions", "[]")
                val allQuestions = com.google.gson.Gson().fromJson(
                    questionsJson, 
                    object : com.google.gson.reflect.TypeToken<List<Map<String, Any>>>() {}.type
                ) as List<Map<String, Any>>

                // Create quiz data with all questions
                val quizData = hashMapOf(
                    "id" to UUID.randomUUID().toString(),
                    "teacherId" to currentUser.uid,
                    "teacherName" to currentUser.name,
                    "subject" to subjectEnum.name,
                    "title" to "$subjectName Quiz",
                    "description" to "Quiz created by ${currentUser.name}",
                    "totalQuestions" to totalQuestions,
                    "timeLimit" to 30,
                    "createdAt" to System.currentTimeMillis(),
                    "isPublished" to true,
                    "questions" to allQuestions
                )

                // Save to Firestore
                val firestore = com.google.firebase.firestore.FirebaseFirestore.getInstance()
                val docRef = firestore.collection("quizzes").document(quizData["id"] as String)
                
                docRef.set(quizData)
                    .addOnSuccessListener {
                        // Clear SharedPreferences
                        sharedPref.edit().clear().apply()
                        
                        Toast.makeText(requireContext(), "Quiz saved successfully!", Toast.LENGTH_LONG).show()
                        navigateBackToQuizList()
                    }
                    .addOnFailureListener { exception ->
                        Toast.makeText(requireContext(), "Failed to save quiz: ${exception.message}", Toast.LENGTH_LONG).show()
                    }

            } catch (e: Exception) {
                Toast.makeText(requireContext(), "Error: ${e.message}", Toast.LENGTH_LONG).show()
            }
        }
    }
}
