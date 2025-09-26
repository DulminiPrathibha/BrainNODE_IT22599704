package com.example.brainnode.teacher.addnotes

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.example.brainnode.R
import com.example.brainnode.data.models.LessonItem
import com.example.brainnode.data.repository.LessonRepository
import kotlinx.coroutines.launch

class EditLessonFragment : Fragment() {

    private lateinit var etTitle: EditText
    private lateinit var etNoteContent: EditText
    private lateinit var etSummary: EditText
    private lateinit var btnSave: Button
    private lateinit var btnCancel: Button
    
    private val lessonRepository = LessonRepository()
    private var lessonItem: LessonItem? = null

    companion object {
        private const val ARG_LESSON_ID = "lesson_id"
        private const val ARG_LESSON_TITLE = "lesson_title"
        private const val ARG_LESSON_CONTENT = "lesson_content"
        private const val ARG_LESSON_SUMMARY = "lesson_summary"
        private const val ARG_SUBJECT_NAME = "subject_name"

        fun newInstance(lesson: LessonItem): EditLessonFragment {
            val fragment = EditLessonFragment()
            val args = Bundle()
            args.putString(ARG_LESSON_ID, lesson.id)
            args.putString(ARG_LESSON_TITLE, lesson.title)
            args.putString(ARG_LESSON_CONTENT, lesson.content)
            args.putString(ARG_LESSON_SUMMARY, lesson.summary)
            args.putString(ARG_SUBJECT_NAME, lesson.subjectName)
            fragment.arguments = args
            return fragment
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            lessonItem = LessonItem(
                id = it.getString(ARG_LESSON_ID, ""),
                title = it.getString(ARG_LESSON_TITLE, ""),
                content = it.getString(ARG_LESSON_CONTENT, ""),
                summary = it.getString(ARG_LESSON_SUMMARY, ""),
                subjectName = it.getString(ARG_SUBJECT_NAME, "")
            )
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.adding_notes_template, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        initializeViews(view)
        populateFields()
        setupClickListeners()
    }

    private fun initializeViews(view: View) {
        etTitle = view.findViewById(R.id.etTitle)
        etNoteContent = view.findViewById(R.id.etNoteContent)
        etSummary = view.findViewById(R.id.etSummary)
        btnSave = view.findViewById(R.id.btnPublish)
        btnCancel = view.findViewById(R.id.btnBackToSubjects)
        
        // Update button texts for editing context
        btnSave.text = "Save Changes"
        btnCancel.text = "Cancel"
    }

    private fun populateFields() {
        lessonItem?.let { lesson ->
            etTitle.setText(lesson.title)
            etNoteContent.setText(lesson.content)
            etSummary.setText(lesson.summary)
        }
    }

    private fun setupClickListeners() {
        btnSave.setOnClickListener {
            saveChanges()
        }

        btnCancel.setOnClickListener {
            navigateBack()
        }
    }

    private fun saveChanges() {
        val title = etTitle.text.toString().trim()
        val content = etNoteContent.text.toString().trim()
        val summary = etSummary.text.toString().trim()

        // Validate input fields
        if (title.isEmpty()) {
            etTitle.error = "Please enter a title"
            etTitle.requestFocus()
            return
        }

        if (content.isEmpty()) {
            etNoteContent.error = "Please enter note content"
            etNoteContent.requestFocus()
            return
        }

        if (summary.isEmpty()) {
            etSummary.error = "Please enter a summary"
            etSummary.requestFocus()
            return
        }

        lessonItem?.let { lesson ->
            updateLessonInFirebase(lesson.copy(
                title = title,
                content = content,
                summary = summary
            ))
        }
    }

    private fun updateLessonInFirebase(updatedLesson: LessonItem) {
        lifecycleScope.launch {
            try {
                // Show loading state
                btnSave.isEnabled = false
                btnSave.text = "Saving..."

                val result = lessonRepository.updateLesson(updatedLesson.id, updatedLesson)
                
                if (result.isSuccess) {
                    Toast.makeText(context, "Lesson updated successfully!", Toast.LENGTH_SHORT).show()
                    navigateBack()
                } else {
                    val error = result.exceptionOrNull()
                    Toast.makeText(context, "Failed to update lesson: ${error?.message}", Toast.LENGTH_LONG).show()
                }
            } catch (e: Exception) {
                Toast.makeText(context, "Error: ${e.message}", Toast.LENGTH_LONG).show()
            } finally {
                // Reset button state
                btnSave.isEnabled = true
                btnSave.text = "Save Changes"
            }
        }
    }

    private fun navigateBack() {
        // Navigate back to lesson subject list
        val lessonListFragment = LessonSubjectListFragment.newInstance(lessonItem?.subjectName ?: "")
        parentFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, lessonListFragment)
            .addToBackStack(null)
            .commit()
    }
}
