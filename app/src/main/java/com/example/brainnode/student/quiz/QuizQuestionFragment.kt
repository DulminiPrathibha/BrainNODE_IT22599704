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
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.brainnode.R

class QuizQuestionFragment : Fragment() {

    private lateinit var timerText: TextView
    private lateinit var timerProgress: ProgressBar
    private lateinit var questionCounter: TextView
    private lateinit var questionText: TextView
    private lateinit var answersRecyclerView: RecyclerView
    private lateinit var nextButton: Button
    
    private lateinit var answerOptionsAdapter: AnswerOptionsAdapter
    private var countDownTimer: CountDownTimer? = null
    private var selectedAnswer: AnswerOption? = null
    
    // Sample data - replace with actual data from your backend
    private val currentQuestion = QuizQuestion(
        id = "1",
        questionText = "1. Which of the following is responsible for managing memory in an OS?",
        options = mutableListOf(
            AnswerOption("A", "A) File Manager"),
            AnswerOption("B", "B) Process Scheduler"),
            AnswerOption("C", "C) Memory Manager"),
            AnswerOption("D", "D) Device Driver")
        ),
        correctAnswerId = "C"
    )

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_quiz_question, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        initializeViews(view)
        setupRecyclerView()
        setupQuestion()
        startTimer()
        setupNextButton()
    }

    private fun initializeViews(view: View) {
        timerText = view.findViewById(R.id.timerText)
        timerProgress = view.findViewById(R.id.timerProgress)
        questionCounter = view.findViewById(R.id.questionCounter)
        questionText = view.findViewById(R.id.questionText)
        answersRecyclerView = view.findViewById(R.id.answersRecyclerView)
        nextButton = view.findViewById(R.id.nextButton)
    }

    private fun setupRecyclerView() {
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

    private fun setupQuestion() {
        questionText.text = currentQuestion.questionText
        questionCounter.text = "1/5" // Update based on actual question number
        
        // Initially disable next button
        nextButton.isEnabled = false
        nextButton.alpha = 0.5f
    }

    private fun startTimer() {
        val totalTimeInMillis = 30000L // 30 seconds
        
        countDownTimer = object : CountDownTimer(totalTimeInMillis, 100) {
            override fun onTick(millisUntilFinished: Long) {
                val secondsRemaining = millisUntilFinished / 1000.0
                timerText.text = String.format("%.2f", secondsRemaining)
                
                // Update progress bar (reverse progress as time decreases)
                val progress = ((totalTimeInMillis - millisUntilFinished) * 100 / totalTimeInMillis).toInt()
                timerProgress.progress = progress
            }

            override fun onFinish() {
                timerText.text = "0.00"
                timerProgress.progress = 100
                // Auto-submit or move to next question
                handleTimeUp()
            }
        }.start()
    }

    private fun setupNextButton() {
        nextButton.setOnClickListener {
            // Handle next question logic
            selectedAnswer?.let { answer ->
                // Process the answer
                val isCorrect = answer.id == currentQuestion.correctAnswerId
                // TODO: Save answer and move to next question
                moveToNextQuestion()
            }
        }
    }

    private fun handleTimeUp() {
        // Handle when time runs out
        nextButton.isEnabled = true
        nextButton.alpha = 1.0f
        // TODO: Auto-submit current answer or mark as unanswered
    }

    private fun moveToNextQuestion() {
        // TODO: Implement navigation to next question or results
        countDownTimer?.cancel()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        countDownTimer?.cancel()
    }
}
