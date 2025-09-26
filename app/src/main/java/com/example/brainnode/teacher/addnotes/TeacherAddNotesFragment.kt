package com.example.brainnode.teacher.addnotes

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.brainnode.R

class TeacherAddNotesFragment : Fragment() {

    private lateinit var llOperatingSystem: LinearLayout
    private lateinit var llStatistics: LinearLayout
    private lateinit var llDatabase: LinearLayout
    private lateinit var llProgramming: LinearLayout

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_teacher_addnotes, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        initializeViews(view)
        setupClickListeners()
    }

    private fun initializeViews(view: View) {
        llOperatingSystem = view.findViewById(R.id.llOperatingSystem)
        llStatistics = view.findViewById(R.id.llStatistics)
        llDatabase = view.findViewById(R.id.llDatabase)
        llProgramming = view.findViewById(R.id.llProgramming)
    }

    private fun setupClickListeners() {
        llOperatingSystem.setOnClickListener {
            navigateToAddNoteForm("Operating System")
        }

        llStatistics.setOnClickListener {
            navigateToAddNoteForm("Statistics")
        }

        llDatabase.setOnClickListener {
            navigateToAddNoteForm("Database")
        }

        llProgramming.setOnClickListener {
            navigateToAddNoteForm("Programming")
        }
    }

    private fun navigateToAddNoteForm(subjectName: String) {
        // Navigate to lessons list first, then to add note form
        val subjectCode = when (subjectName) {
            "Operating System" -> "OS"
            "Statistics" -> "STAT"
            "Database" -> "DB"
            "Programming" -> "PROG"
            else -> ""
        }
        
        val fragment = TeacherLessonsListFragment.newInstance(subjectName, subjectCode)
        
        parentFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, fragment)
            .addToBackStack(null)
            .commit()
    }
}