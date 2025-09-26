package com.example.brainnode.teacher.addnotes

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import android.widget.Button
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.example.brainnode.R
import com.example.brainnode.teacher.addnotes.TeacherAddNotesFragment
import com.example.brainnode.data.repository.LessonRepository
import com.example.brainnode.data.models.LessonItem
import android.widget.EditText
import kotlinx.coroutines.launch

class AddingNotesTemplateFragment : Fragment() {

    private lateinit var etTitle: EditText
    private lateinit var etNoteContent: EditText
    private lateinit var etSummary: EditText
    private lateinit var btnBackToSubjects: Button
    private lateinit var btnPublish: Button


    private var subjectName: String = ""
    private var subjectCode: String = ""
    private val lessonRepository = LessonRepository()

    companion object {
        private const val ARG_SUBJECT_NAME = "subject_name"
        private const val ARG_SUBJECT_CODE = "subject_code"

        fun newInstance(subjectName: String, subjectCode: String): AddingNotesTemplateFragment {
            val fragment = AddingNotesTemplateFragment()
            val args = Bundle()
            args.putString(ARG_SUBJECT_NAME, subjectName)
            args.putString(ARG_SUBJECT_CODE, subjectCode)
            fragment.arguments = args
            return fragment
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            subjectName = it.getString(ARG_SUBJECT_NAME, "")
            subjectCode = it.getString(ARG_SUBJECT_CODE, "")
        }
        
        // Debug logging
        println("DEBUG AddingNotesTemplate: subjectName = '$subjectName'")
        println("DEBUG AddingNotesTemplate: subjectCode = '$subjectCode'")
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
        setupClickListeners()
    }

    private fun initializeViews(view: View) {
        etTitle = view.findViewById(R.id.etTitle)
        etNoteContent = view.findViewById(R.id.etNoteContent)
        etSummary = view.findViewById(R.id.etSummary)
        btnBackToSubjects = view.findViewById(R.id.btnBackToSubjects)
        btnPublish = view.findViewById(R.id.btnPublish)
    }

    private fun setupClickListeners() {
        btnBackToSubjects.setOnClickListener {
            navigateBackToSubjects()
        }

        btnPublish.setOnClickListener {
            publishNote()
        }
    }

    private fun navigateBackToSubjects() {
        // Navigate back to the lesson subject list screen
        val lessonListFragment = LessonSubjectListFragment.newInstance(subjectName)
        parentFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, lessonListFragment)
            .addToBackStack(null)
            .commit()
    }

    private fun publishNote() {
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

        // Save lesson to Firebase
        saveNoteToFirebase(title, content, summary)
    }

    private fun saveNoteToFirebase(title: String, content: String, summary: String) {
        lifecycleScope.launch {
            try {
                // Show loading state
                btnPublish.isEnabled = false
                btnPublish.text = "Publishing..."

                // Determine lesson number by counting existing lessons for this subject
                val existingLessonsResult = lessonRepository.getLessonsByTeacher()
                val lessonNumber = if (existingLessonsResult.isSuccess) {
                    val existingLessons = existingLessonsResult.getOrNull() ?: emptyList()
                    val subjectLessons = existingLessons.filter { it.subjectName == subjectName }
                    subjectLessons.size + 1
                } else {
                    1
                }

                // Create lesson object
                val lesson = LessonItem(
                    title = title,
                    subjectName = subjectName.ifEmpty { "General" },
                    lessonNumber = lessonNumber,
                    content = content,
                    summary = summary
                )

                println("DEBUG: Creating lesson with data:")
                println("  - title: '$title'")
                println("  - subjectName: '${lesson.subjectName}'")
                println("  - lessonNumber: $lessonNumber")
                println("  - content length: ${content.length}")
                println("  - summary length: ${summary.length}")

                // Save to Firebase
                val result = lessonRepository.createLesson(lesson)
                
                if (result.isSuccess) {
                    Toast.makeText(context, "Lesson published successfully!", Toast.LENGTH_SHORT).show()
                    // Navigate back to lesson subject list
                    navigateBackToSubjects()
                } else {
                    val error = result.exceptionOrNull()
                    Toast.makeText(context, "Failed to publish lesson: ${error?.message}", Toast.LENGTH_LONG).show()
                }
            } catch (e: Exception) {
                Toast.makeText(context, "Error: ${e.message}", Toast.LENGTH_LONG).show()
            } finally {
                // Reset button state
                btnPublish.isEnabled = true
                btnPublish.text = "Publish"
            }
        }
    }

    private fun getSubjectCode(subjectName: String): String {
        return when (subjectName) {
            "Operating System" -> "OS"
            "Statistics" -> "STAT"
            "Database" -> "DB"
            "Programming" -> "PROG"
            else -> ""
        }
    }
}
