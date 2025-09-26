package com.example.brainnode.teacher.addnotes

import android.app.AlertDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.brainnode.R
import com.example.brainnode.data.models.LessonItem
import com.google.android.material.floatingactionbutton.FloatingActionButton

class LessonSubjectListFragment : Fragment() {

    private lateinit var rvLessons: RecyclerView
    private lateinit var fabAddLesson: FloatingActionButton
    private lateinit var lessonAdapter: LessonCardAdapter
    private val lessons = mutableListOf<LessonItem>()

    companion object {
        private const val ARG_SUBJECT_NAME = "subject_name"

        fun newInstance(subjectName: String): LessonSubjectListFragment {
            val fragment = LessonSubjectListFragment()
            val args = Bundle()
            args.putString(ARG_SUBJECT_NAME, subjectName)
            fragment.arguments = args
            return fragment
        }
    }

    private var subjectName: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            subjectName = it.getString(ARG_SUBJECT_NAME, "")
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.lesson_subject_list, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        initializeViews(view)
        setupRecyclerView()
        setupClickListeners()
        loadSampleLessons()
    }

    private fun initializeViews(view: View) {
        rvLessons = view.findViewById(R.id.rvLessons)
        fabAddLesson = view.findViewById(R.id.fabAddLesson)
    }

    private fun setupRecyclerView() {
        lessonAdapter = LessonCardAdapter(
            lessons = lessons,
            onEditClick = { lesson -> showEditLessonDialog(lesson) },
            onDeleteClick = { lesson -> showDeleteConfirmationDialog(lesson) },
            onLessonClick = { lesson -> navigateToLessonDetails(lesson) }
        )
        
        rvLessons.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = lessonAdapter
        }
    }

    private fun setupClickListeners() {
        fabAddLesson.setOnClickListener {
            navigateToAddNotes()
        }
    }

    private fun loadSampleLessons() {
        // Load sample lessons or fetch from Firebase
        val sampleLessons = listOf(
            LessonItem(
                id = "1",
                title = "Introduction to Operating Systems",
                subjectName = subjectName.ifEmpty { "Operating System" },
                lessonNumber = 1,
                content = "Sample content for lesson 1",
                summary = "Sample summary for lesson 1"
            )
        )
        
        lessons.addAll(sampleLessons)
        lessonAdapter.notifyDataSetChanged()
    }

    private fun showEditLessonDialog(lesson: LessonItem) {
        val editText = EditText(context)
        editText.setText(lesson.title)
        
        AlertDialog.Builder(context)
            .setTitle("Edit Lesson Name")
            .setView(editText)
            .setPositiveButton("Save") { _, _ ->
                val newTitle = editText.text.toString().trim()
                if (newTitle.isNotEmpty()) {
                    updateLessonTitle(lesson, newTitle)
                } else {
                    Toast.makeText(context, "Please enter a valid title", Toast.LENGTH_SHORT).show()
                }
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    private fun showDeleteConfirmationDialog(lesson: LessonItem) {
        AlertDialog.Builder(context)
            .setTitle("Delete Lesson")
            .setMessage("Are you sure you want to delete this lesson?")
            .setPositiveButton("Delete") { _, _ ->
                deleteLesson(lesson)
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    private fun updateLessonTitle(lesson: LessonItem, newTitle: String) {
        // Update lesson title in the list and notify adapter
        val position = lessons.indexOf(lesson)
        if (position != -1) {
            lessons[position] = lesson.copy(title = newTitle)
            lessonAdapter.notifyItemChanged(position)
            Toast.makeText(context, "Lesson name updated", Toast.LENGTH_SHORT).show()
        }
    }

    private fun deleteLesson(lesson: LessonItem) {
        val position = lessons.indexOf(lesson)
        if (position != -1) {
            lessons.removeAt(position)
            lessonAdapter.notifyItemRemoved(position)
            Toast.makeText(context, "Lesson deleted", Toast.LENGTH_SHORT).show()
        }
    }

    private fun navigateToLessonDetails(lesson: LessonItem) {
        // Navigate to lesson details or edit screen
        Toast.makeText(context, "Opening lesson: ${lesson.title}", Toast.LENGTH_SHORT).show()
    }

    private fun navigateToAddNotes() {
        // Navigate to AddingNotesTemplateFragment
        val addNotesFragment = AddingNotesTemplateFragment.newInstance(subjectName, "")
        parentFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, addNotesFragment)
            .addToBackStack(null)
            .commit()
    }
}
