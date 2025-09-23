package com.example.brainnode.student.notes

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import com.example.brainnode.R
import com.example.brainnode.databinding.FragmentStudentNotesLessonSelectionBinding

class NotesLessonSelectionFragment : Fragment() {

    private var _binding: FragmentStudentNotesLessonSelectionBinding? = null
    private val binding get() = _binding!!
    
    private var subjectName: String = "Operating System"

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentStudentNotesLessonSelectionBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        // Get subject name from arguments
        subjectName = arguments?.getString("subject_name") ?: "Operating System"
        
        setupUI()
        setupClickListeners()
    }

    private fun setupUI() {
        // Set the subject title
        binding.tvSubjectTitle.text = subjectName
        
        // Update lesson titles based on subject
        updateLessonTitles()
    }

    private fun updateLessonTitles() {
        when (subjectName) {
            "Operating System" -> {
                binding.tvLesson1Title.text = "1. Introduction to OS"
                binding.tvLesson2Title.text = "2. Process Management"
                binding.tvLesson3Title.text = "3. Memory Management"
            }
            "Statistics" -> {
                binding.tvLesson1Title.text = "1. Descriptive Statistics"
                binding.tvLesson2Title.text = "2. Probability Theory"
                binding.tvLesson3Title.text = "3. Statistical Inference"
            }
            "Programming" -> {
                binding.tvLesson1Title.text = "1. Programming Basics"
                binding.tvLesson2Title.text = "2. Data Structures"
                binding.tvLesson3Title.text = "3. Algorithms"
            }
            else -> {
                // Default to Operating System lessons
                binding.tvLesson1Title.text = "1. Introduction to OS"
                binding.tvLesson2Title.text = "2. Process Management"
                binding.tvLesson3Title.text = "3. Memory Management"
            }
        }
    }

    private fun setupClickListeners() {
        // Lesson 1 click
        binding.llLesson1.setOnClickListener {
            navigateToNotesForLesson(binding.tvLesson1Title.text.toString())
        }

        // Lesson 2 click
        binding.llLesson2.setOnClickListener {
            navigateToNotesForLesson(binding.tvLesson2Title.text.toString())
        }

        // Lesson 3 click
        binding.llLesson3.setOnClickListener {
            navigateToNotesForLesson(binding.tvLesson3Title.text.toString())
        }
    }

    private fun navigateToNotesForLesson(lessonTitle: String) {
        // Create bundle to pass lesson and subject info to the notes content fragment
        val bundle = Bundle().apply {
            putString("subject_name", subjectName)
            putString("lesson_title", lessonTitle)
        }
        
        // Navigate to notes content fragment
        val studentNotesFragment = StudentNotesFragment().apply {
            arguments = bundle
        }
        
        val transaction: FragmentTransaction = parentFragmentManager.beginTransaction()
        transaction.replace(R.id.fragment_container, studentNotesFragment)
        transaction.addToBackStack(null)
        transaction.commit()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
