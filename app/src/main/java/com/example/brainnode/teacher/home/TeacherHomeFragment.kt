package com.example.brainnode.teacher.home

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
import com.example.brainnode.teacher.addnotes.TeacherAddNotesFragment
import com.example.brainnode.teacher.addquizzes.TeacherAddQuizzesFragment
import com.example.brainnode.teacher.progress.ManageStudentsProgressFragment
import com.example.brainnode.data.repository.AuthRepository
import kotlinx.coroutines.launch

class TeacherHomeFragment : Fragment() {

    private val authRepository = AuthRepository()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_teacher_home, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        // Set up personalized greeting
        setupPersonalizedGreeting(view)
        
        val studentProgressCard = view.findViewById<LinearLayout>(R.id.llStudentProgress)
        studentProgressCard.setOnClickListener {
            // Navigate to Student Progress fragment using fragment transaction
            val studentProgressFragment = ManageStudentsProgressFragment()
            val transaction: FragmentTransaction = parentFragmentManager.beginTransaction()
            transaction.replace(R.id.fragment_teacher_home, studentProgressFragment)
            transaction.addToBackStack(null)
            transaction.commit()
        }
        
        val addNotesCard = view.findViewById<LinearLayout>(R.id.llAddNotes)
        addNotesCard.setOnClickListener {
            // Navigate to Add Notes fragment using fragment transaction
            val addNotesFragment = TeacherAddNotesFragment()
            val transaction: FragmentTransaction = parentFragmentManager.beginTransaction()
            transaction.replace(R.id.fragment_teacher_home, addNotesFragment)
            transaction.addToBackStack(null)
            transaction.commit()
        }

        val addQuizCard = view.findViewById<LinearLayout>(R.id.llAddQuiz)
        addQuizCard.setOnClickListener {
            // Navigate to Add Quiz fragment using fragment transaction
            val addQuizFragment = TeacherAddQuizzesFragment()
            val transaction: FragmentTransaction = parentFragmentManager.beginTransaction()
            transaction.replace(R.id.fragment_teacher_home, addQuizFragment)
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