package com.example.brainnode.teacher.addnotes

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.brainnode.R

class TeacherAddNoteFormFragment : Fragment() {

    private lateinit var etSubjectCode: EditText
    private lateinit var etNoteContent: EditText
    private lateinit var etSummary: EditText
    private lateinit var btnBackToLessons: Button
    private lateinit var btnPublish: Button

    private var subjectName: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Get the subject name passed from the previous fragment
        arguments?.let {
            subjectName = it.getString("subject_name")
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_teacher_add_note_form, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        initializeViews(view)
        setupClickListeners()
        setupInitialData()
    }

    private fun initializeViews(view: View) {
        etSubjectCode = view.findViewById(R.id.etSubjectCode)
        etNoteContent = view.findViewById(R.id.etNoteContent)
        etSummary = view.findViewById(R.id.etSummary)
        btnBackToLessons = view.findViewById(R.id.btnBackToLessons)
        btnPublish = view.findViewById(R.id.btnPublish)
    }

    private fun setupClickListeners() {
        btnBackToLessons.setOnClickListener {
            // Navigate back to the lessons list screen
            parentFragmentManager.popBackStack()
        }

        btnPublish.setOnClickListener {
            publishNote()
        }
    }

    private fun setupInitialData() {
        // Pre-fill subject code if subject name is available
        subjectName?.let { subject ->
            val subjectCode = when (subject) {
                "Operating System" -> "OS"
                "Statistics" -> "STAT"
                "Database" -> "DB"
                "Programming" -> "PROG"
                else -> ""
            }
            etSubjectCode.setText(subjectCode)
        }
    }

    private fun publishNote() {
        val subjectCode = etSubjectCode.text.toString().trim()
        val noteContent = etNoteContent.text.toString().trim()
        val summary = etSummary.text.toString().trim()

        // Validate input fields
        if (subjectCode.isEmpty()) {
            etSubjectCode.error = "Subject code is required"
            etSubjectCode.requestFocus()
            return
        }

        if (noteContent.isEmpty()) {
            Toast.makeText(context, "Please add note content", Toast.LENGTH_SHORT).show()
            etNoteContent.requestFocus()
            return
        }

        if (summary.isEmpty()) {
            Toast.makeText(context, "Please add a summary", Toast.LENGTH_SHORT).show()
            etSummary.requestFocus()
            return
        }

        // Create note object and save (this will be implemented with backend integration)
        val note = Note(
            id = System.currentTimeMillis().toString(),
            subjectCode = subjectCode,
            content = noteContent,
            summary = summary,
            createdAt = System.currentTimeMillis(),
            teacherId = getCurrentTeacherId() // This should be implemented based on your auth system
        )

        // TODO: Save note to database/backend
        saveNoteToDatabase(note)

        // Show success message
        Toast.makeText(context, "Note published successfully!", Toast.LENGTH_SHORT).show()

        // Navigate back to lessons
        parentFragmentManager.popBackStack()
    }

    private fun saveNoteToDatabase(note: Note) {
        val repository = NoteRepository(requireContext())
        val success = repository.saveNote(note)
        
        if (!success) {
            Toast.makeText(context, "Failed to save note. Please try again.", Toast.LENGTH_SHORT).show()
        }
    }

    private fun getCurrentTeacherId(): String {
        // TODO: Implement based on your authentication system
        // Return the current logged-in teacher's ID
        return "teacher_123" // Placeholder
    }

    companion object {
        fun newInstance(subjectName: String): TeacherAddNoteFormFragment {
            val fragment = TeacherAddNoteFormFragment()
            val args = Bundle()
            args.putString("subject_name", subjectName)
            fragment.arguments = args
            return fragment
        }
    }
}
