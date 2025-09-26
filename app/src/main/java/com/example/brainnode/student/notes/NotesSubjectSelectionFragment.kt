package com.example.brainnode.student.notes

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import com.example.brainnode.R
import com.example.brainnode.databinding.FragmentStudentNotesSubjectSelectionBinding

class NotesSubjectSelectionFragment : Fragment() {

    private var _binding: FragmentStudentNotesSubjectSelectionBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentStudentNotesSubjectSelectionBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupClickListeners()
    }

    private fun setupClickListeners() {
        // Operating System card click
        binding.operatingSystemNotesCard.setOnClickListener {
            navigateToNotesForSubject("Operating System")
        }

        // Statistics card click
        binding.statisticsNotesCard.setOnClickListener {
            navigateToNotesForSubject("Statistics")
        }

        // Programming card click
        binding.programmingNotesCard.setOnClickListener {
            navigateToNotesForSubject("Programming")
        }
    }

    private fun navigateToNotesForSubject(subject: String) {
        // Create bundle to pass subject name to the lesson selection fragment
        val bundle = Bundle().apply {
            putString("subject_name", subject)
        }
        
        // Navigate to lesson selection fragment
        val notesLessonSelectionFragment = NotesLessonSelectionFragment().apply {
            arguments = bundle
        }
        
        val transaction: FragmentTransaction = parentFragmentManager.beginTransaction()
        transaction.replace(R.id.fragment_container, notesLessonSelectionFragment)
        transaction.addToBackStack(null)
        transaction.commit()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
