package com.example.brainnode.student

import android.os.Bundle
import android.os.CountDownTimer
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.brainnode.R
import com.example.brainnode.data.models.MistakeCard
import com.example.brainnode.data.models.Quiz
import com.example.brainnode.data.models.QuizQuestion
import com.example.brainnode.data.models.QuizOption
import com.example.brainnode.data.repository.MistakeCardRepository
import com.example.brainnode.data.repository.QuizRepository
import com.example.brainnode.student.quiz.AnswerOptionsAdapter
import kotlinx.coroutines.launch

class MistakeCardResolveFragment : Fragment() {

    private lateinit var timerText: TextView
    private lateinit var timerProgress: ProgressBar
    private lateinit var questionCounter: TextView
    private lateinit var questionText: TextView
    private lateinit var answersRecyclerView: RecyclerView
    private lateinit var submitButton: Button
    
    private lateinit var answerOptionsAdapter: AnswerOptionsAdapter
    private var countDownTimer: CountDownTimer? = null
    private var selectedAnswer: QuizOption? = null
    
    private val mistakeCardRepository = MistakeCardRepository()
    private val quizRepository = QuizRepository()
    private var mistakeCard: MistakeCard? = null
    private var mistakeCardId: String = ""
    private var currentQuiz: Quiz? = null
    private var currentQuestion: QuizQuestion? = null
    
    companion object {
        private const val ARG_MISTAKE_CARD_ID = "mistake_card_id"
        private const val RESOLVE_TIME_LIMIT = 60000L // 1 minute in milliseconds
        
        fun newInstance(mistakeCardId: String): MistakeCardResolveFragment {
            val fragment = MistakeCardResolveFragment()
            val args = Bundle()
            args.putString(ARG_MISTAKE_CARD_ID, mistakeCardId)
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
        
        // Get mistake card ID from arguments
        arguments?.getString(ARG_MISTAKE_CARD_ID)?.let { id ->
            mistakeCardId = id
        }
        
        initializeViews(view)
        loadMistakeCard()
    }

    private fun initializeViews(view: View) {
        timerText = view.findViewById(R.id.timerText)
        timerProgress = view.findViewById(R.id.timerProgress)
        questionCounter = view.findViewById(R.id.questionCounter)
        questionText = view.findViewById(R.id.questionText)
        answersRecyclerView = view.findViewById(R.id.answersRecyclerView)
        submitButton = view.findViewById(R.id.nextButton)
        
        // Change button text to "Submit"
        submitButton.text = "Submit"
    }
    
    private fun loadMistakeCard() {
        lifecycleScope.launch {
            try {
                println("🔍 Loading mistake card with ID: $mistakeCardId")
                val result = mistakeCardRepository.getMistakeCardById(mistakeCardId)
                result.fold(
                    onSuccess = { card ->
                        card?.let {
                            mistakeCard = it
                            println("✅ Mistake card loaded: ${it.questionText}")
                            // Now load the actual quiz to get the real options
                            loadQuizData(it.quizId, it.questionId)
                        } ?: run {
                            Toast.makeText(context, "Mistake card not found", Toast.LENGTH_SHORT).show()
                            parentFragmentManager.popBackStack()
                        }
                    },
                    onFailure = { exception ->
                        println("❌ Failed to load mistake card: ${exception.message}")
                        Toast.makeText(context, "Failed to load mistake card: ${exception.message}", Toast.LENGTH_SHORT).show()
                        parentFragmentManager.popBackStack()
                    }
                )
            } catch (e: Exception) {
                println("💥 Exception loading mistake card: ${e.message}")
                Toast.makeText(context, "Error loading mistake card", Toast.LENGTH_SHORT).show()
                parentFragmentManager.popBackStack()
            }
        }
    }
    
    private suspend fun loadQuizData(quizId: String, questionId: String) {
        try {
            println("📋 Loading quiz data for quiz: $quizId, question: $questionId")
            
            // First, let's check if the quiz ID looks valid
            if (quizId.isEmpty() || quizId.startsWith("test_")) {
                println("❌ Invalid quiz ID detected: $quizId")
                Toast.makeText(context, "Invalid quiz reference. This mistake card may be corrupted.", Toast.LENGTH_LONG).show()
                parentFragmentManager.popBackStack()
                return
            }
            
            val quizResult = quizRepository.getQuizById(quizId)
            quizResult.fold(
                onSuccess = { quiz ->
                    quiz?.let {
                        println("✅ Quiz loaded: ${it.title} with ${it.questions.size} questions")
                        currentQuiz = it
                        
                        // Find the specific question
                        currentQuestion = it.questions.find { q -> q.id == questionId }
                        
                        if (currentQuestion != null) {
                            println("✅ Found question: ${currentQuestion!!.questionText}")
                            println("✅ Question has ${currentQuestion!!.options.size} options")
                            setupQuestion()
                            setupRecyclerView()
                            startTimer()
                            setupSubmitButton()
                        } else {
                            println("❌ Question ID '$questionId' not found in quiz")
                            println("Available question IDs in quiz:")
                            it.questions.forEachIndexed { index, q ->
                                println("  $index: ${q.id} - ${q.questionText.take(30)}...")
                            }
                            Toast.makeText(context, "Question not found in quiz", Toast.LENGTH_SHORT).show()
                            parentFragmentManager.popBackStack()
                        }
                    } ?: run {
                        println("❌ Quiz with ID '$quizId' not found in database")
                        Toast.makeText(context, "Quiz not found", Toast.LENGTH_SHORT).show()
                        parentFragmentManager.popBackStack()
                    }
                },
                onFailure = { exception ->
                    println("❌ Failed to load quiz: ${exception.message}")
                    exception.printStackTrace()
                    Toast.makeText(context, "Failed to load quiz: ${exception.message}", Toast.LENGTH_SHORT).show()
                    parentFragmentManager.popBackStack()
                }
            )
        } catch (e: Exception) {
            println("💥 Exception loading quiz: ${e.message}")
            e.printStackTrace()
            Toast.makeText(context, "Error loading quiz", Toast.LENGTH_SHORT).show()
            parentFragmentManager.popBackStack()
        }
    }

    private fun setupQuestion() {
        mistakeCard?.let { card ->
            questionText.text = card.questionText
            questionCounter.text = "Resolve Mistake Card"
            
            // Initially disable submit button
            submitButton.isEnabled = false
            submitButton.alpha = 0.5f
        }
    }

    private fun setupRecyclerView() {
        currentQuestion?.let { question ->
            println("🎯 Setting up options for question: ${question.questionText}")
            println("📝 Available options: ${question.options.size}")
            
            // Use the actual quiz options from the teacher
            val options = question.options.toMutableList()
            
            // Log all options for debugging
            options.forEachIndexed { index, option ->
                println("Option $index: ${option.text} (ID: ${option.id}, Correct: ${option.isCorrect})")
            }
            
            answerOptionsAdapter = AnswerOptionsAdapter(
                options
            ) { selectedOption ->
                selectedAnswer = selectedOption
                submitButton.isEnabled = true
                submitButton.alpha = 1.0f
                println("🎯 Selected option: ${selectedOption.text}")
            }
            
            answersRecyclerView.apply {
                layoutManager = LinearLayoutManager(context)
                adapter = answerOptionsAdapter
            }
        } ?: run {
            println("❌ No current question available for RecyclerView setup")
        }
    }

    private fun startTimer() {
        countDownTimer = object : CountDownTimer(RESOLVE_TIME_LIMIT, 100) {
            override fun onTick(millisUntilFinished: Long) {
                val secondsRemaining = millisUntilFinished / 1000.0
                val minutes = (secondsRemaining / 60).toInt()
                val seconds = (secondsRemaining % 60).toInt()
                timerText.text = String.format("%d:%02d", minutes, seconds)
                
                // Update progress bar
                val progress = ((RESOLVE_TIME_LIMIT - millisUntilFinished) * 100 / RESOLVE_TIME_LIMIT).toInt()
                timerProgress.progress = progress
            }

            override fun onFinish() {
                timerText.text = "0:00"
                timerProgress.progress = 100
                // Auto-submit when time runs out
                handleTimeUp()
            }
        }.start()
    }

    private fun setupSubmitButton() {
        submitButton.setOnClickListener {
            currentQuestion?.let { question ->
                selectedAnswer?.let { answer ->
                    println("🎯 Checking answer: ${answer.text} (ID: ${answer.id})")
                    println("✅ Correct answer ID: ${question.correctAnswerId}")
                    
                    val isCorrect = answer.id == question.correctAnswerId
                    
                    if (isCorrect) {
                        println("🎉 Correct answer selected!")
                        // Correct answer - resolve the mistake card
                        resolveMistakeCard()
                    } else {
                        println("❌ Wrong answer selected")
                        // Wrong answer - show message and go back
                        Toast.makeText(context, "Incorrect answer. Try again later!", Toast.LENGTH_LONG).show()
                        parentFragmentManager.popBackStack()
                    }
                } ?: run {
                    Toast.makeText(context, "Please select an answer", Toast.LENGTH_SHORT).show()
                }
            } ?: run {
                Toast.makeText(context, "Question data not available", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun handleTimeUp() {
        // Time ran out - treat as incorrect
        Toast.makeText(context, "Time's up! Try again later.", Toast.LENGTH_LONG).show()
        parentFragmentManager.popBackStack()
    }

    private fun resolveMistakeCard() {
        lifecycleScope.launch {
            try {
                println("🎉 Deleting resolved mistake card: $mistakeCardId")
                val result = mistakeCardRepository.deleteMistakeCard(mistakeCardId)
                result.fold(
                    onSuccess = {
                        countDownTimer?.cancel()
                        println("✅ Mistake card deleted successfully")
                        
                        // Show success message
                        Toast.makeText(context, "Well done! Mistake card resolved and removed.", Toast.LENGTH_LONG).show()
                        
                        // Add delay to ensure Firebase deletion propagates and UI sees the message
                        kotlinx.coroutines.delay(1500)
                        
                        // Navigate back to mistake cards fragment which will automatically show next card or home
                        parentFragmentManager.popBackStack()
                    },
                    onFailure = { exception ->
                        println("❌ Failed to delete mistake card: ${exception.message}")
                        Toast.makeText(context, "Failed to delete mistake card: ${exception.message}", Toast.LENGTH_SHORT).show()
                    }
                )
            } catch (e: Exception) {
                println("💥 Exception deleting mistake card: ${e.message}")
                Toast.makeText(context, "Error deleting mistake card", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        countDownTimer?.cancel()
    }
}
