package com.example.brainnode.student

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.brainnode.R

class MistakeCardsAdapter(
    private val mistakeCards: List<MistakeCard>
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
            tvMscTag.text = "MSC : ${mistakeCard.mscNumber}"
            tvCounter.text = mistakeCard.position
            tvQuestion.text = mistakeCard.questionText
            tvYourAnswer.text = mistakeCard.userAnswer
            tvNote.text = mistakeCard.note

            // Set click listeners
            btnResolve.setOnClickListener {
                // Handle resolve action
            }

            btnNext.setOnClickListener {
                // Handle next action
            }

            btnDelete.setOnClickListener {
                // Handle delete action
            }
        }
    }
}
