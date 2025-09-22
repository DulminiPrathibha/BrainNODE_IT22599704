package com.example.brainnode.student.quizzes

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.cardview.widget.CardView
import androidx.fragment.app.Fragment
import com.example.brainnode.R

class QuizzesFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_student_quizzes, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        // Set up click listeners for subject cards
        val operatingSystemCard = view.findViewById<CardView>(R.id.operatingSystemCard)
        val statisticsCard = view.findViewById<CardView>(R.id.statisticsCard)
        val programmingCard = view.findViewById<CardView>(R.id.programmingCard)
        
        operatingSystemCard.setOnClickListener {
            // TODO: Navigate to Operating System quiz
        }
        
        statisticsCard.setOnClickListener {
            // TODO: Navigate to Statistics quiz
        }
        
        programmingCard.setOnClickListener {
            // TODO: Navigate to Programming quiz
        }
    }
}