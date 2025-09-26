package com.example.brainnode.student

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.brainnode.R
import com.example.brainnode.data.models.LessonItem
import com.google.android.material.floatingactionbutton.FloatingActionButton

class StudentNewLessonSubjectListFragment : Fragment() {

    private lateinit var rvStudentLessons: RecyclerView
    private lateinit var fabAddStudentLesson: FloatingActionButton
    private lateinit var studentLessonAdapter: StudentLessonCardAdapter
    private val lessons = mutableListOf<LessonItem>()

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
        loadAvailableLessons()
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
            // For students, this might navigate to a request lesson screen
            // or show available lessons to add
            Toast.makeText(context, "Request new lesson feature", Toast.LENGTH_SHORT).show()
        }
    }

    private fun loadAvailableLessons() {
        // Load lessons available to the student
        // This would typically fetch from Firebase based on student's enrolled subjects
        val availableLessons = listOf(
            LessonItem(
                id = "1",
                title = "Introduction to Operating Systems",
                subjectName = "Operating System",
                lessonNumber = 1,
                content = "Learn the basics of operating systems",
                summary = "Introduction to OS concepts"
            ),
            LessonItem(
                id = "2",
                title = "Process Management",
                subjectName = "Operating System",
                lessonNumber = 2,
                content = "Understanding processes and threads",
                summary = "Process management concepts"
            ),
            LessonItem(
                id = "3",
                title = "Basic Statistics",
                subjectName = "Statistics",
                lessonNumber = 1,
                content = "Introduction to statistical concepts",
                summary = "Statistics fundamentals"
            )
        )
        
        lessons.addAll(availableLessons)
        studentLessonAdapter.notifyDataSetChanged()
    }

    private fun openLesson(lesson: LessonItem) {
        // Navigate to lesson content view for students
        // This would open the lesson content, notes, and possibly quizzes
        Toast.makeText(context, "Opening lesson: ${lesson.title}", Toast.LENGTH_SHORT).show()
        
        // TODO: Navigate to student lesson content fragment
        // val lessonContentFragment = StudentLessonContentFragment.newInstance(lesson.id)
        // parentFragmentManager.beginTransaction()
        //     .replace(R.id.fragment_container, lessonContentFragment)
        //     .addToBackStack(null)
        //     .commit()
    }
}
