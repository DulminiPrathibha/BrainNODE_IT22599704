package com.example.brainnode.student.quiz

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.brainnode.R

class AnswerOptionsAdapter(
    private val options: MutableList<AnswerOption>,
    private val onOptionSelected: (AnswerOption) -> Unit
) : RecyclerView.Adapter<AnswerOptionsAdapter.AnswerOptionViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AnswerOptionViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_answer_option, parent, false)
        return AnswerOptionViewHolder(view)
    }

    override fun onBindViewHolder(holder: AnswerOptionViewHolder, position: Int) {
        holder.bind(options[position])
    }

    override fun getItemCount(): Int = options.size

    fun updateSelection(selectedOption: AnswerOption) {
        // Clear all selections
        options.forEach { it.isSelected = false }
        // Set the selected option
        selectedOption.isSelected = true
        notifyDataSetChanged()
    }

    inner class AnswerOptionViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val answerCard: CardView = itemView.findViewById(R.id.answerCard)
        private val answerText: TextView = itemView.findViewById(R.id.answerText)
        private val selectionIndicator: View = itemView.findViewById(R.id.selectionIndicator)

        fun bind(option: AnswerOption) {
            answerText.text = option.text

            // Update UI based on selection state
            if (option.isSelected) {
                answerCard.setCardBackgroundColor(
                    ContextCompat.getColor(itemView.context, R.color.answer_selected_background)
                )
                selectionIndicator.background = ContextCompat.getDrawable(
                    itemView.context, R.drawable.selection_indicator_selected
                )
            } else {
                answerCard.setCardBackgroundColor(
                    ContextCompat.getColor(itemView.context, R.color.answer_unselected_background)
                )
                selectionIndicator.background = ContextCompat.getDrawable(
                    itemView.context, R.drawable.selection_indicator_unselected
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
