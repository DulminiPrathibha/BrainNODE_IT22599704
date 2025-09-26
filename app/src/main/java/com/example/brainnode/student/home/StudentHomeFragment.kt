package com.example.brainnode.student.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import androidx.lifecycle.lifecycleScope
import com.example.brainnode.R
import com.example.brainnode.student.quizzes.QuizzesFragment
import com.example.brainnode.student.MistakeCardsFragment
import com.example.brainnode.student.StudentNewLessonSubjectListFragment
import com.example.brainnode.data.repository.AuthRepository
import kotlinx.coroutines.launch

class StudentHomeFragment : Fragment() {

    private val authRepository = AuthRepository()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_student_home, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        // Set up personalized greeting
        setupPersonalizedGreeting(view)
        
        // Set up click listeners for the cards
        val mistakeCardsLayout = view.findViewById<LinearLayout>(R.id.llMistakeCards)
        val notesLayout = view.findViewById<LinearLayout>(R.id.llNotes)
        val quizLayout = view.findViewById<LinearLayout>(R.id.llQuiz)
        
        mistakeCardsLayout.setOnClickListener {
            // Navigate to Mistake Cards screen
            val mistakeCardsFragment = MistakeCardsFragment()
            val transaction: FragmentTransaction = parentFragmentManager.beginTransaction()
            transaction.replace(R.id.fragment_container, mistakeCardsFragment)
            transaction.addToBackStack(null)
            transaction.commit()
        }
        
        notesLayout.setOnClickListener {
            // Navigate to Student Lesson Subject List
            val lessonListFragment = StudentNewLessonSubjectListFragment.newInstance()
            val transaction: FragmentTransaction = parentFragmentManager.beginTransaction()
            transaction.replace(R.id.fragment_container, lessonListFragment)
            transaction.addToBackStack(null)
            transaction.commit()
        }
        
        quizLayout.setOnClickListener {
            // Navigate to Quiz section
            val quizzesFragment = QuizzesFragment()
            val transaction: FragmentTransaction = parentFragmentManager.beginTransaction()
            transaction.replace(R.id.fragment_container, quizzesFragment)
            transaction.addToBackStack(null)
            transaction.commit()
        }
    }
    
    private fun setupPersonalizedGreeting(view: View) {
        val tvGreeting = view.findViewById<TextView>(R.id.tvGreeting)
        
        lifecycleScope.launch {
            val result = authRepository.getCurrentUser()
            result.fold(
                onSuccess = { user ->
                    if (user != null) {
                        // Extract first name from full name
                        val firstName = user.name.split(" ").firstOrNull() ?: user.name
                        tvGreeting.text = "Welcome $firstName!"
                    } else {
                        tvGreeting.text = "Welcome!"
                    }
                },
                onFailure = {
                    // Keep default greeting if unable to get user data
                    tvGreeting.text = "Welcome!"
                }
            )
        }
    }
}
