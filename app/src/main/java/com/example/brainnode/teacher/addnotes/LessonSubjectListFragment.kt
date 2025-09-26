package com.example.brainnode.teacher.addnotes

import android.app.AlertDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.brainnode.R
import com.example.brainnode.data.models.LessonItem
import com.example.brainnode.data.repository.LessonRepository
import com.google.android.material.floatingactionbutton.FloatingActionButton
import kotlinx.coroutines.launch

class LessonSubjectListFragment : Fragment() {

    private lateinit var rvLessons: RecyclerView
    private lateinit var fabAddLesson: FloatingActionButton
    private lateinit var lessonAdapter: LessonCardAdapter
    private val lessons = mutableListOf<LessonItem>()
    private val lessonRepository = LessonRepository()

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
        loadLessonsFromFirebase()
    }

    override fun onResume() {
        super.onResume()
        // Refresh lessons when returning to this fragment
        loadLessonsFromFirebase()
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

    private fun loadLessonsFromFirebase() {
        lifecycleScope.launch {
            try {
                val result = lessonRepository.getLessonsByTeacher()
                if (result.isSuccess) {
                    val allLessons = result.getOrNull() ?: emptyList()
                    
                    // Debug logging
                    println("DEBUG: Total lessons loaded: ${allLessons.size}")
                    allLessons.forEach { lesson ->
                        println("DEBUG: Lesson - ID: ${lesson.id}, Title: ${lesson.title}, Subject: ${lesson.subjectName}")
                    }
                    
                    // Filter lessons by subject name if provided
                    val filteredLessons = if (subjectName.isNotEmpty()) {
                        val filtered = allLessons.filter { it.subjectName == subjectName }
                        println("DEBUG: Filtered lessons for subject '$subjectName': ${filtered.size}")
                        filtered
                    } else {
                        println("DEBUG: No subject filter applied, showing all lessons")
                        allLessons
                    }
                    
                    lessons.clear()
                    lessons.addAll(filteredLessons)
                    lessonAdapter.notifyDataSetChanged()
                    
                    if (filteredLessons.isEmpty()) {
                        Toast.makeText(context, "No lessons found", Toast.LENGTH_SHORT).show()
                    } else {
                        Toast.makeText(context, "Loaded ${filteredLessons.size} lessons", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    val error = result.exceptionOrNull()
                    println("DEBUG: Error loading lessons: ${error?.message}")
                    println("DEBUG: Full error: $error")
                    Toast.makeText(context, "Failed to load lessons: ${error?.message}", Toast.LENGTH_LONG).show()
                }
            } catch (e: Exception) {
                println("DEBUG: Exception loading lessons: ${e.message}")
                println("DEBUG: Full exception: $e")
                Toast.makeText(context, "Error loading lessons: ${e.message}", Toast.LENGTH_LONG).show()
            }
        }
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
        lifecycleScope.launch {
            try {
                val updatedLesson = lesson.copy(title = newTitle)
                val result = lessonRepository.updateLesson(lesson.id, updatedLesson)
                
                if (result.isSuccess) {
                    // Update local list and notify adapter
                    val position = lessons.indexOf(lesson)
                    if (position != -1) {
                        lessons[position] = updatedLesson
                        lessonAdapter.notifyItemChanged(position)
                    }
                    Toast.makeText(context, "Lesson name updated", Toast.LENGTH_SHORT).show()
                } else {
                    val error = result.exceptionOrNull()
                    Toast.makeText(context, "Failed to update lesson: ${error?.message}", Toast.LENGTH_LONG).show()
                }
            } catch (e: Exception) {
                Toast.makeText(context, "Error updating lesson: ${e.message}", Toast.LENGTH_LONG).show()
            }
        }
    }

    private fun deleteLesson(lesson: LessonItem) {
        lifecycleScope.launch {
            try {
                val result = lessonRepository.deleteLesson(lesson.id)
                
                if (result.isSuccess) {
                    // Remove from local list and notify adapter
                    val position = lessons.indexOf(lesson)
                    if (position != -1) {
                        lessons.removeAt(position)
                        lessonAdapter.notifyItemRemoved(position)
                    }
                    Toast.makeText(context, "Lesson deleted successfully", Toast.LENGTH_SHORT).show()
                } else {
                    val error = result.exceptionOrNull()
                    Toast.makeText(context, "Failed to delete lesson: ${error?.message}", Toast.LENGTH_LONG).show()
                }
            } catch (e: Exception) {
                Toast.makeText(context, "Error deleting lesson: ${e.message}", Toast.LENGTH_LONG).show()
            }
        }
    }

    private fun navigateToLessonDetails(lesson: LessonItem) {
        // Navigate to edit lesson screen
        val editLessonFragment = EditLessonFragment.newInstance(lesson)
        parentFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, editLessonFragment)
            .addToBackStack(null)
            .commit()
    }

    private fun navigateToAddNotes() {
        // If no subject name is set, show subject selection dialog
        if (subjectName.isEmpty()) {
            showSubjectSelectionDialog()
        } else {
            // Navigate to AddingNotesTemplateFragment with existing subject
            println("DEBUG LessonSubjectList: Navigating to AddNotes with subjectName = '$subjectName'")
            val addNotesFragment = AddingNotesTemplateFragment.newInstance(subjectName, "")
            parentFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, addNotesFragment)
                .addToBackStack(null)
                .commit()
        }
    }

    private fun showSubjectSelectionDialog() {
        val editText = EditText(context)
        editText.hint = "Enter subject name (e.g., Operating System, Mathematics)"
        
        AlertDialog.Builder(context)
            .setTitle("Select Subject")
            .setMessage("Enter the subject name for this lesson:")
            .setView(editText)
            .setPositiveButton("Continue") { _, _ ->
                val selectedSubject = editText.text.toString().trim()
                if (selectedSubject.isNotEmpty()) {
                    // Navigate to AddingNotesTemplateFragment with selected subject
                    val addNotesFragment = AddingNotesTemplateFragment.newInstance(selectedSubject, "")
                    parentFragmentManager.beginTransaction()
                        .replace(R.id.fragment_container, addNotesFragment)
                        .addToBackStack(null)
                        .commit()
                } else {
                    Toast.makeText(context, "Please enter a subject name", Toast.LENGTH_SHORT).show()
                }
            }
            .setNegativeButton("Cancel", null)
            .show()
    }
}
