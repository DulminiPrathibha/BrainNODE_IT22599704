package com.example.brainnode.student

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.brainnode.R
import com.example.brainnode.data.models.MistakeCard

class MistakeCardsAdapter(
    private val mistakeCards: List<MistakeCard>,
    private val onResolveClick: (MistakeCard) -> Unit,
    private val onNextClick: () -> Unit,
    private val onDeleteClick: (MistakeCard) -> Unit,
    private val currentPosition: Int = 1,
    private val totalCards: Int = 1
) : RecyclerView.Adapter<MistakeCardsAdapter.MistakeCardViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MistakeCardViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_mistake_card, parent, false)
        return MistakeCardViewHolder(view)
    }

    override fun onBindViewHolder(holder: MistakeCardViewHolder, position: Int) {
        holder.bind(mistakeCards[position])
    }

    override fun getItemCount(): Int = mistakeCards.size

    inner class MistakeCardViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val tvMscTag: TextView = itemView.findViewById(R.id.tvMscTag)
        private val tvCounter: TextView = itemView.findViewById(R.id.tvCounter)
        private val tvQuestion: TextView = itemView.findViewById(R.id.tvQuestion)
        private val tvYourAnswer: TextView = itemView.findViewById(R.id.tvYourAnswer)
        private val tvNote: TextView = itemView.findViewById(R.id.tvNote)
        private val btnResolve: Button = itemView.findViewById(R.id.btnResolve)
        private val btnNext: Button = itemView.findViewById(R.id.btnNext)
        private val btnDelete: Button = itemView.findViewById(R.id.btnDelete)

        fun bind(mistakeCard: MistakeCard) {
            tvMscTag.text = mistakeCard.getFormattedMscNumber()
            tvCounter.text = "$currentPosition/$totalCards"
            tvQuestion.text = mistakeCard.questionText
            tvYourAnswer.text = mistakeCard.selectedOptionText
            tvNote.text = mistakeCard.explanation

            // Set click listeners
            btnResolve.setOnClickListener {
                onResolveClick(mistakeCard)
            }

            btnNext.setOnClickListener {
                onNextClick()
            }

            btnDelete.setOnClickListener {
                onDeleteClick(mistakeCard)
            }
        }
    }
}
