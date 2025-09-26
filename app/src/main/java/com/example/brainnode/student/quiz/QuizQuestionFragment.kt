package com.example.brainnode.student.quiz

import android.os.Bundle
import android.os.CountDownTimer
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ProgressBar
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.fragment.app.FragmentTransaction
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.brainnode.R
import com.example.brainnode.data.models.Quiz
import com.example.brainnode.data.models.QuizQuestion
import com.example.brainnode.data.models.QuizOption
import com.example.brainnode.data.models.MistakeCard
import com.example.brainnode.data.models.QuizAttempt
import com.example.brainnode.data.models.StudentAnswer
import com.example.brainnode.data.repository.QuizRepository
import com.example.brainnode.data.repository.MistakeCardRepository
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.launch

class QuizQuestionFragment : Fragment() {

    private lateinit var timerText: TextView
    private lateinit var timerProgress: ProgressBar
    private lateinit var questionCounter: TextView
    private lateinit var questionText: TextView
    private lateinit var answersRecyclerView: RecyclerView
    private lateinit var nextButton: Button
    
    private lateinit var answerOptionsAdapter: AnswerOptionsAdapter
    private var countDownTimer: CountDownTimer? = null
    private var selectedAnswer: QuizOption? = null
    
    private val quizRepository = QuizRepository()
    private val mistakeCardRepository = MistakeCardRepository()
    private val auth = FirebaseAuth.getInstance()
    private var currentQuiz: Quiz? = null
    private var currentQuestionIndex = 0
    private var quizId: String = ""
    private var correctAnswers = 0
    private val userAnswers = mutableListOf<StudentAnswer>()
    private val mistakeQuestions = mutableListOf<QuizQuestion>()
    
    companion object {
        private const val ARG_QUIZ_ID = "quiz_id"
        
        fun newInstance(quizId: String): QuizQuestionFragment {
            val fragment = QuizQuestionFragment()
            val args = Bundle()
            args.putString(ARG_QUIZ_ID, quizId)
            fragment.arguments = args
            return fragment
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_quiz_question, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        // Get quiz ID from arguments
        arguments?.getString(ARG_QUIZ_ID)?.let { id ->
            quizId = id
        }
        
        initializeViews(view)
        loadQuizData()
    }

    private fun initializeViews(view: View) {
        timerText = view.findViewById(R.id.timerText)
        timerProgress = view.findViewById(R.id.timerProgress)
        questionCounter = view.findViewById(R.id.questionCounter)
        questionText = view.findViewById(R.id.questionText)
        answersRecyclerView = view.findViewById(R.id.answersRecyclerView)
        nextButton = view.findViewById(R.id.nextButton)
    }
    
    private fun loadQuizData() {
        lifecycleScope.launch {
            val result = quizRepository.getQuizById(quizId)
            
            result.fold(
                onSuccess = { quiz ->
                    quiz?.let {
                        currentQuiz = it
                        if (it.questions.isNotEmpty()) {
                            setupRecyclerView()
                            setupQuestion()
                            startTimer() // Start timer once for entire quiz
                            setupNextButton()
                        } else {
                            // Handle empty quiz
                            questionText.text = "This quiz has no questions available."
                            nextButton.text = "Go Back"
                            nextButton.setOnClickListener {
                                parentFragmentManager.popBackStack()
                            }
                        }
                    } ?: run {
                        // Handle quiz not found
                        questionText.text = "Quiz not found."
                        nextButton.text = "Go Back"
                        nextButton.setOnClickListener {
                            parentFragmentManager.popBackStack()
                        }
                    }
                },
                onFailure = { exception ->
                    // Handle error
                    questionText.text = "Failed to load quiz: ${exception.message}"
                    nextButton.text = "Go Back"
                    nextButton.setOnClickListener {
                        parentFragmentManager.popBackStack()
                    }
                }
            )
        }
    }

    private fun setupRecyclerView() {
        currentQuiz?.let { quiz ->
            if (currentQuestionIndex < quiz.questions.size) {
                val currentQuestion = quiz.questions[currentQuestionIndex]
                answerOptionsAdapter = AnswerOptionsAdapter(
                    currentQuestion.options.toMutableList()
                ) { selectedOption ->
                    selectedAnswer = selectedOption
                    nextButton.isEnabled = true
                    nextButton.alpha = 1.0f
                }
                
                answersRecyclerView.apply {
                    layoutManager = LinearLayoutManager(context)
                    adapter = answerOptionsAdapter
                }
            }
        }
    }

    private fun setupQuestion() {
        currentQuiz?.let { quiz ->
            if (currentQuestionIndex < quiz.questions.size) {
                val currentQuestion = quiz.questions[currentQuestionIndex]
                questionText.text = "${currentQuestionIndex + 1}. ${currentQuestion.questionText}"
                questionCounter.text = "${currentQuestionIndex + 1}/${quiz.questions.size}"
                
                // Initially disable next button
                nextButton.isEnabled = false
                nextButton.alpha = 0.5f
                
                // Update button text based on question position
                nextButton.text = if (currentQuestionIndex == quiz.questions.size - 1) "Finish" else "Next"
            }
        }
    }

    private fun startTimer() {
        currentQuiz?.let { quiz ->
            // Calculate total time: 1 minute per question for the entire quiz
            val totalTimeInMillis = (quiz.questions.size * 60 * 1000).toLong()
            
            countDownTimer = object : CountDownTimer(totalTimeInMillis, 100) {
                override fun onTick(millisUntilFinished: Long) {
                    val secondsRemaining = millisUntilFinished / 1000.0
                    val minutes = (secondsRemaining / 60).toInt()
                    val seconds = (secondsRemaining % 60).toInt()
                    timerText.text = String.format("%d:%02d", minutes, seconds)
                    
                    // Update progress bar (reverse progress as time decreases)
                    val progress = ((totalTimeInMillis - millisUntilFinished) * 100 / totalTimeInMillis).toInt()
                    timerProgress.progress = progress
                }

                override fun onFinish() {
                    timerText.text = "0:00"
                    timerProgress.progress = 100
                    // Auto-submit the entire quiz when time runs out
                    handleQuizTimeUp()
                }
            }.start()
        }
    }

    private fun setupNextButton() {
        nextButton.setOnClickListener {
            currentQuiz?.let { quiz ->
                if (currentQuestionIndex < quiz.questions.size) {
                    val currentQuestion = quiz.questions[currentQuestionIndex]
                    
                    // Process the answer
                    selectedAnswer?.let { answer ->
                        val isCorrect = answer.id == currentQuestion.correctAnswerId
                        val correctOption = currentQuestion.options.find { it.id == currentQuestion.correctAnswerId }
                        
                        // Create StudentAnswer object
                        val studentAnswer = StudentAnswer(
                            questionId = currentQuestion.id,
                            selectedOptionId = answer.id,
                            correctOptionId = currentQuestion.correctAnswerId,
                            isCorrect = isCorrect,
                            timeSpent = 0L // We can track this later if needed
                        )
                        userAnswers.add(studentAnswer)
                        
                        if (isCorrect) {
                            correctAnswers++
                            println("✅ Correct answer for question: ${currentQuestion.questionText}")
                        } else {
                            // Add to mistake questions for later processing
                            mistakeQuestions.add(currentQuestion)
                            println("❌ Wrong answer for question: ${currentQuestion.questionText}")
                            println("Selected: ${answer.text}, Correct: ${correctOption?.text}")
                        }
                    } ?: run {
                        // No answer selected - treat as incorrect
                        val correctOption = currentQuestion.options.find { it.id == currentQuestion.correctAnswerId }
                        val studentAnswer = StudentAnswer(
                            questionId = currentQuestion.id,
                            selectedOptionId = "",
                            correctOptionId = currentQuestion.correctAnswerId,
                            isCorrect = false,
                            timeSpent = 0L
                        )
                        userAnswers.add(studentAnswer)
                        mistakeQuestions.add(currentQuestion)
                        println("❌ No answer selected for question: ${currentQuestion.questionText}")
                    }
                    
                    moveToNextQuestion()
                }
            }
        }
    }

    private fun handleTimeUp() {
        // Handle when time runs out
        nextButton.isEnabled = true
        nextButton.alpha = 1.0f
        // Auto-submit current answer or mark as unanswered
        moveToNextQuestion()
    }

    private fun moveToNextQuestion() {
        // Don't cancel timer - let it continue for entire quiz
        
        currentQuiz?.let { quiz ->
            currentQuestionIndex++
            
            if (currentQuestionIndex < quiz.questions.size) {
                // Move to next question
                selectedAnswer = null
                setupRecyclerView()
                setupQuestion()
                // Don't restart timer - it continues for entire quiz
            } else {
                // Quiz completed - show completion screen
                countDownTimer?.cancel()
                showQuizCompletion()
            }
        }
    }
    
    private fun handleQuizTimeUp() {
        // Handle when entire quiz time runs out
        countDownTimer?.cancel()
        // Auto-submit the quiz and show completion
        showQuizCompletion()
    }
    
    private fun showQuizCompletion() {
        currentQuiz?.let { quiz ->
            println("🏁 Quiz completed! Score: $correctAnswers/${quiz.questions.size}")
            println("📝 Total mistake questions: ${mistakeQuestions.size}")
            
            // Submit quiz attempt and create mistake cards
            submitQuizAttempt(quiz)
            
            val completionFragment = QuizCompletionFragment.newInstance(
                score = correctAnswers,
                totalQuestions = quiz.questions.size,
                isFirstQuiz = true
            )
            
            parentFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, completionFragment)
                .addToBackStack(null)
                .commit()
        }
    }
    
    private fun submitQuizAttempt(quiz: Quiz) {
        val currentUser = auth.currentUser
        if (currentUser == null) {
            // Handle user not logged in
            return
        }
        
        lifecycleScope.launch {
            try {
                // Create quiz attempt
                val quizAttempt = QuizAttempt(
                    studentId = currentUser.uid,
                    studentName = currentUser.displayName ?: "Student",
                    quizId = quiz.id,
                    quizTitle = quiz.title,
                    subject = quiz.subject,
                    answers = userAnswers,
                    score = correctAnswers,
                    totalQuestions = quiz.questions.size,
                    timeSpent = 0L, // We can track this later
                    completedAt = System.currentTimeMillis(),
                    isCompleted = true
                )
                
                // Submit quiz attempt
                quizRepository.submitQuizAttempt(quizAttempt)
                
                // Create mistake cards for incorrect answers
                createMistakeCards(quiz, currentUser.uid, currentUser.displayName ?: "Student")
                
            } catch (e: Exception) {
                // Handle error - but don't block the UI
                e.printStackTrace()
            }
        }
    }
    
    private suspend fun createMistakeCards(quiz: Quiz, studentId: String, studentName: String) {
        try {
            println("Creating mistake cards for ${mistakeQuestions.size} incorrect answers")
            
            // Get next MSC number for this student
            val nextMscResult = mistakeCardRepository.getNextMscNumber(studentId)
            var mscNumber = nextMscResult.getOrNull() ?: 1
            
            println("Next MSC number: $mscNumber")
            
            // Create mistake cards for each incorrect answer
            for (i in mistakeQuestions.indices) {
                val question = mistakeQuestions[i]
                val studentAnswer = userAnswers.find { it.questionId == question.id }
                
                if (studentAnswer != null && !studentAnswer.isCorrect) {
                    val correctOption = question.options.find { it.id == question.correctAnswerId }
                    val selectedOption = question.options.find { it.id == studentAnswer.selectedOptionId }
                    
                    val mistakeCard = MistakeCard(
                        studentId = studentId,
                        studentName = studentName,
                        quizId = quiz.id,
                        quizTitle = quiz.title,
                        subject = quiz.subject,
                        questionId = question.id,
                        questionText = question.questionText,
                        correctOptionId = question.correctAnswerId,
                        correctOptionText = correctOption?.text ?: "",
                        selectedOptionId = studentAnswer.selectedOptionId,
                        selectedOptionText = selectedOption?.text ?: "No answer selected",
                        explanation = question.explanation.ifEmpty { "Review this question and understand the correct answer." },
                        mscNumber = mscNumber++,
                        createdAt = System.currentTimeMillis(),
                        isResolved = false
                    )
                    
                    println("Creating mistake card: MSC $mscNumber for question: ${question.questionText}")
                    val result = mistakeCardRepository.createMistakeCard(mistakeCard)
                    result.fold(
                        onSuccess = { id -> println("Successfully created mistake card with ID: $id") },
                        onFailure = { e -> 
                            println("Failed to create mistake card: ${e.message}")
                            e.printStackTrace()
                        }
                    )
                }
            }
        } catch (e: Exception) {
            println("Exception creating mistake cards: ${e.message}")
            e.printStackTrace()
        }
    }
    

    override fun onDestroyView() {
        super.onDestroyView()
        countDownTimer?.cancel()
    }
}
