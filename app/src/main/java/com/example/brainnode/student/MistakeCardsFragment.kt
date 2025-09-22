package com.example.brainnode.student

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.brainnode.R

class MistakeCardsFragment : Fragment() {

    private lateinit var rvMistakeCards: RecyclerView
    private lateinit var mistakeCardsAdapter: MistakeCardsAdapter

    // Sample data
    private val sampleMistakeCards = listOf(
        MistakeCard(
            mscNumber = "1",
            quizInfo = "Quiz 5 : OS",
            questionText = "5. Which of the following is not an operating system?",
            userAnswer = "Oracle",
            note = "Three of these control computer hardware and provide a platform to run applications. One is mainly used for managing databases, not for running your whole computer.",
            position = "1/4"
        )
    )

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_mistake_cards, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        setupRecyclerView(view)
    }

    private fun setupRecyclerView(view: View) {
        rvMistakeCards = view.findViewById(R.id.rvMistakeCards)
        
        mistakeCardsAdapter = MistakeCardsAdapter(sampleMistakeCards)
        
        rvMistakeCards.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = mistakeCardsAdapter
        }
    }
}
