package com.example.brainnode.student

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.fragment.app.FragmentTransaction
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.brainnode.R
import com.example.brainnode.data.models.MistakeCard
import com.example.brainnode.data.repository.MistakeCardRepository
import com.example.brainnode.student.home.StudentHomeFragment
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.launch

class MistakeCardsFragment : Fragment() {

    private lateinit var rvMistakeCards: RecyclerView
    private lateinit var mistakeCardsAdapter: MistakeCardsAdapter
    
    private val mistakeCardRepository = MistakeCardRepository()
    private val auth = FirebaseAuth.getInstance()
    private var mistakeCards = mutableListOf<MistakeCard>()
    private var currentMistakeIndex = 0

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
        loadMistakeCards()
    }
    
    override fun onResume() {
        super.onResume()
        
        // Always reload when fragment resumes - this ensures fresh data after card resolution
        println("🔄 Fragment resumed - force reloading mistake cards...")
        
        // Clear current data first
        mistakeCards.clear()
        currentMistakeIndex = 0
        
        // Force reload from Firebase to get updated list
        loadMistakeCards()
    }

    private fun setupRecyclerView(view: View) {
        rvMistakeCards = view.findViewById(R.id.rvMistakeCards)
        
        mistakeCardsAdapter = MistakeCardsAdapter(
            mistakeCards = mistakeCards,
            onResolveClick = { mistakeCard -> resolveMistakeCard(mistakeCard) },
            onNextClick = { navigateToNextMistakeCard() },
            onDeleteClick = { mistakeCard -> deleteMistakeCard(mistakeCard) }
        )
        
        rvMistakeCards.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = mistakeCardsAdapter
        }
    }
    
    private fun loadMistakeCards() {
        val currentUser = auth.currentUser
        if (currentUser == null) {
            Toast.makeText(context, "Please log in to view mistake cards", Toast.LENGTH_SHORT).show()
            return
        }
        
        println("🔍 Starting to load mistake cards for student: ${currentUser.uid}")
        println("📧 Student email: ${currentUser.email}")
        
        lifecycleScope.launch {
            try {
                // First, let's try to get ALL mistake cards to see if any exist
                println("📋 Attempting to fetch all mistake cards for student...")
                val allCardsResult = mistakeCardRepository.getMistakeCardsByStudent(currentUser.uid)
                
                allCardsResult.fold(
                    onSuccess = { allCards ->
                        println("📊 Total mistake cards found: ${allCards.size}")
                        
                        // Filter out test cards and invalid cards - only keep valid unresolved cards
                        val validUnresolvedCards = allCards.filter { card ->
                            val isValid = !card.quizId.startsWith("test_") && card.quizId.isNotEmpty() && !card.isResolved
                            if (card.quizId.startsWith("test_") || card.quizId.isEmpty()) {
                                println("🗑️ Filtering out invalid card: Quiz ID = ${card.quizId}")
                                // Optionally delete invalid test cards
                                lifecycleScope.launch {
                                    mistakeCardRepository.deleteMistakeCard(card.id)
                                }
                            }
                            isValid
                        }
                        
                        println("📊 Valid unresolved mistake cards: ${validUnresolvedCards.size}")
                        validUnresolvedCards.forEachIndexed { index, card ->
                            println("Card $index: MSC ${card.mscNumber}")
                            println("  Quiz ID: ${card.quizId}")
                            println("  Question ID: ${card.questionId}")
                            println("  Question: ${card.questionText.take(50)}...")
                        }
                        
                        mistakeCards.clear()
                        mistakeCards.addAll(validUnresolvedCards)
                        
                        if (mistakeCards.isEmpty()) {
                            Toast.makeText(context, "🎉 All mistake cards resolved! Excellent work!", Toast.LENGTH_LONG).show()
                            // Navigate to home immediately
                            navigateToHome()
                        } else {
                            println("✅ Showing ${mistakeCards.size} unresolved mistake cards")
                            // Reset current index to 0 to always show the first unresolved card
                            currentMistakeIndex = 0
                            showCurrentMistakeCard()
                        }
                    },
                    onFailure = { exception ->
                        println("❌ Failed to load mistake cards: ${exception.message}")
                        exception.printStackTrace()
                        
                        // Check if it's a permission error
                        if (exception.message?.contains("PERMISSION_DENIED") == true) {
                            Toast.makeText(context, "Permission denied. Please check Firebase rules.", Toast.LENGTH_LONG).show()
                        } else {
                            Toast.makeText(context, "Failed to load mistake cards: ${exception.message}", Toast.LENGTH_LONG).show()
                        }
                    }
                )
            } catch (e: Exception) {
                println("💥 Exception loading mistake cards: ${e.message}")
                e.printStackTrace()
                Toast.makeText(context, "Error loading mistake cards: ${e.message}", Toast.LENGTH_LONG).show()
            }
        }
    }
    
    private fun showCurrentMistakeCard() {
        if (currentMistakeIndex < mistakeCards.size) {
            val currentCard = mistakeCards[currentMistakeIndex]
            val singleCardList = listOf(currentCard)
            
            println("📱 Showing mistake card ${currentMistakeIndex + 1} of ${mistakeCards.size}")
            println("🎯 Current card: MSC ${currentCard.mscNumber} - ${currentCard.questionText.take(50)}...")
            
            mistakeCardsAdapter = MistakeCardsAdapter(
                mistakeCards = singleCardList,
                onResolveClick = { mistakeCard -> resolveMistakeCard(mistakeCard) },
                onNextClick = { navigateToNextMistakeCard() },
                onDeleteClick = { mistakeCard -> deleteMistakeCard(mistakeCard) },
                currentPosition = currentMistakeIndex + 1,
                totalCards = mistakeCards.size
            )
            
            rvMistakeCards.adapter = mistakeCardsAdapter
        } else {
            println("⚠️ No more mistake cards to show")
            navigateToHome()
        }
    }
    
    private fun resolveMistakeCard(mistakeCard: MistakeCard) {
        // Navigate to resolve fragment using Navigation Component
        val resolveFragment = MistakeCardResolveFragment.newInstance(mistakeCard.id)
        
        parentFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, resolveFragment)
            .addToBackStack(null)
            .commit()
    }
    
    private fun navigateToNextMistakeCard() {
        currentMistakeIndex++
        if (currentMistakeIndex >= mistakeCards.size) {
            // No more mistake cards
            Toast.makeText(context, "All mistake cards completed!", Toast.LENGTH_SHORT).show()
            navigateToHome()
        } else {
            showCurrentMistakeCard()
        }
    }
    
    private fun deleteMistakeCard(mistakeCard: MistakeCard) {
        lifecycleScope.launch {
            try {
                val result = mistakeCardRepository.deleteMistakeCard(mistakeCard.id)
                result.fold(
                    onSuccess = {
                        Toast.makeText(context, "Mistake card deleted", Toast.LENGTH_SHORT).show()
                        // Remove from local list
                        mistakeCards.removeAt(currentMistakeIndex)
                        
                        if (mistakeCards.isEmpty()) {
                            Toast.makeText(context, "🎉 No more mistake cards! Excellent work!", Toast.LENGTH_SHORT).show()
                            navigateToHome()
                        } else {
                            // Adjust index if needed - if we deleted the last card, go to the previous one
                            if (currentMistakeIndex >= mistakeCards.size) {
                                currentMistakeIndex = mistakeCards.size - 1
                            }
                            showCurrentMistakeCard()
                        }
                    },
                    onFailure = { exception ->
                        Toast.makeText(context, "Failed to delete mistake card: ${exception.message}", Toast.LENGTH_SHORT).show()
                    }
                )
            } catch (e: Exception) {
                Toast.makeText(context, "Error deleting mistake card", Toast.LENGTH_SHORT).show()
            }
        }
    }
    
    private fun navigateToHome() {
        println("🏠 Navigating back to student home")
        val homeFragment = StudentHomeFragment()
        
        parentFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, homeFragment)
            .commit()
    }
    
    
    companion object {
        fun newInstance(): MistakeCardsFragment {
            return MistakeCardsFragment()
        }
    }
}
