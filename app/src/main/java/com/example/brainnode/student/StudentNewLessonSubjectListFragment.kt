package com.example.brainnode.student

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.brainnode.R
import com.example.brainnode.data.models.LessonItem
import com.example.brainnode.data.repository.LessonRepository
import com.example.brainnode.student.notes.StudentNotesFragment
import com.google.android.material.floatingactionbutton.FloatingActionButton
import kotlinx.coroutines.launch

class StudentNewLessonSubjectListFragment : Fragment() {

    private lateinit var rvStudentLessons: RecyclerView
    private lateinit var fabAddStudentLesson: FloatingActionButton
    private lateinit var studentLessonAdapter: StudentLessonCardAdapter
    private val lessons = mutableListOf<LessonItem>()
    private val lessonRepository = LessonRepository()

    companion object {
        fun newInstance(): StudentNewLessonSubjectListFragment {
            return StudentNewLessonSubjectListFragment()
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.student_new_lesson_subject_list, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        initializeViews(view)
        setupRecyclerView()
        setupClickListeners()
        loadLessonsFromFirebase()
    }

    private fun initializeViews(view: View) {
        rvStudentLessons = view.findViewById(R.id.rvStudentLessons)
        fabAddStudentLesson = view.findViewById(R.id.fabAddStudentLesson)
    }

    private fun setupRecyclerView() {
        studentLessonAdapter = StudentLessonCardAdapter(
            lessons = lessons,
            onLessonClick = { lesson -> openLesson(lesson) }
        )
        
        rvStudentLessons.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = studentLessonAdapter
        }
    }

    private fun setupClickListeners() {
        fabAddStudentLesson.setOnClickListener {
            // Hide FAB for students - they can't add lessons
            fabAddStudentLesson.visibility = View.GONE
        }
    }

    private fun loadLessonsFromFirebase() {
        lifecycleScope.launch {
            try {
                val result = lessonRepository.getAllLessons()
                if (result.isSuccess) {
                    val allLessons = result.getOrNull() ?: emptyList()
                    
                    println("DEBUG Student: Total lessons loaded: ${allLessons.size}")
                    allLessons.forEach { lesson ->
                        println("DEBUG Student: Lesson - ID: ${lesson.id}, Title: ${lesson.title}, Subject: ${lesson.subjectName}")
                    }
                    
                    lessons.clear()
                    lessons.addAll(allLessons)
                    studentLessonAdapter.notifyDataSetChanged()
                    
                    if (allLessons.isEmpty()) {
                        Toast.makeText(context, "No lessons available yet", Toast.LENGTH_SHORT).show()
                    } else {
                        Toast.makeText(context, "Loaded ${allLessons.size} lessons", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    val error = result.exceptionOrNull()
                    println("DEBUG Student: Error loading lessons: ${error?.message}")
                    Toast.makeText(context, "Failed to load lessons: ${error?.message}", Toast.LENGTH_LONG).show()
                }
            } catch (e: Exception) {
                println("DEBUG Student: Exception loading lessons: ${e.message}")
                Toast.makeText(context, "Error loading lessons: ${e.message}", Toast.LENGTH_LONG).show()
            }
        }
    }

    override fun onResume() {
        super.onResume()
        // Refresh lessons when returning to this fragment
        loadLessonsFromFirebase()
    }

    private fun openLesson(lesson: LessonItem) {
        // Navigate to StudentNotesFragment with lesson data
        println("DEBUG Student: Opening lesson: ${lesson.title} from subject: ${lesson.subjectName}")
        
        val bundle = Bundle().apply {
            putString("subject_name", lesson.subjectName)
            putString("lesson_title", lesson.title)
            putString("lesson_content", lesson.content)
            putString("lesson_summary", lesson.summary)
            putString("lesson_id", lesson.id)
        }
        
        val studentNotesFragment = StudentNotesFragment().apply {
            arguments = bundle
        }
        
        parentFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, studentNotesFragment)
            .addToBackStack(null)
            .commit()
    }
}
