package com.example.brainnode.teacher.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import com.example.brainnode.R
import com.example.brainnode.teacher.addnotes.TeacherAddNotesFragment
import com.example.brainnode.teacher.addquizzes.TeacherAddQuizzesFragment
import com.example.brainnode.teacher.progress.ManageStudentsProgressFragment

class TeacherHomeFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_teacher_home, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
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
}