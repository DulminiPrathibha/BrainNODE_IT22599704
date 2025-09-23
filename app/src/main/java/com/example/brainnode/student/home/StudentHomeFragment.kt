package com.example.brainnode.student.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import com.example.brainnode.R
import com.example.brainnode.student.quizzes.QuizzesFragment
import com.example.brainnode.student.MistakeCardsFragment
import com.example.brainnode.student.notes.NotesSubjectSelectionFragment

class StudentHomeFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_student_home, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
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
            // Navigate to Notes Subject Selection screen
            val notesSubjectSelectionFragment = NotesSubjectSelectionFragment()
            val transaction: FragmentTransaction = parentFragmentManager.beginTransaction()
            transaction.replace(R.id.fragment_container, notesSubjectSelectionFragment)
            transaction.addToBackStack(null)
            transaction.commit()
        }
        
        quizLayout.setOnClickListener {
            // Navigate to Quiz screen
            val quizzesFragment = QuizzesFragment()
            val transaction: FragmentTransaction = parentFragmentManager.beginTransaction()
            transaction.replace(R.id.fragment_container, quizzesFragment)
            transaction.addToBackStack(null)
            transaction.commit()
        }
    }
}
