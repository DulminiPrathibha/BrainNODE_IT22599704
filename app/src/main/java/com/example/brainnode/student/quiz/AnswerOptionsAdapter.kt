package com.example.brainnode.student.quiz

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.brainnode.R
import com.example.brainnode.data.models.QuizOption

class AnswerOptionsAdapter(
    private val options: MutableList<QuizOption>,
    private val onOptionSelected: (QuizOption) -> Unit
) : RecyclerView.Adapter<AnswerOptionsAdapter.AnswerOptionViewHolder>() {
    
    private var selectedOptionId: String? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AnswerOptionViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_answer_option, parent, false)
        return AnswerOptionViewHolder(view)
    }

    override fun onBindViewHolder(holder: AnswerOptionViewHolder, position: Int) {
        holder.bind(options[position])
    }

    override fun getItemCount(): Int = options.size

    fun updateSelection(selectedOption: QuizOption) {
        selectedOptionId = selectedOption.id
        notifyDataSetChanged()
    }

    inner class AnswerOptionViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val answerCard: CardView = itemView.findViewById(R.id.answerCard)
        private val answerText: TextView = itemView.findViewById(R.id.answerText)
        private val selectionIndicator: View = itemView.findViewById(R.id.selectionIndicator)

        fun bind(option: QuizOption) {
            answerText.text = option.text
            val isSelected = option.id == selectedOptionId

            // Update UI based on selection state
            if (isSelected) {
                answerCard.setCardBackgroundColor(
                    ContextCompat.getColor(itemView.context, android.R.color.holo_blue_light)
                )
                selectionIndicator.setBackgroundColor(
                    ContextCompat.getColor(itemView.context, android.R.color.holo_blue_dark)
                )
            } else {
                answerCard.setCardBackgroundColor(
                    ContextCompat.getColor(itemView.context, android.R.color.white)
                )
                selectionIndicator.setBackgroundColor(
                    ContextCompat.getColor(itemView.context, android.R.color.darker_gray)
                )
            }

            // Set click listener
            answerCard.setOnClickListener {
                onOptionSelected(option)
                updateSelection(option)
            }
        }
    }
}
