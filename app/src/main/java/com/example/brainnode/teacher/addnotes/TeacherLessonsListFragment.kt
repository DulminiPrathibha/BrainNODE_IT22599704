package com.example.brainnode.teacher.addnotes

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.example.brainnode.R
import com.google.android.material.floatingactionbutton.FloatingActionButton

class TeacherLessonsListFragment : Fragment() {

    private lateinit var tvSubjectTitle: TextView
    private lateinit var llLesson1: LinearLayout
    private lateinit var llLesson2: LinearLayout
    private lateinit var llLesson3: LinearLayout
    private lateinit var llDynamicLessons: LinearLayout
    private lateinit var fabAddLesson: FloatingActionButton
    private lateinit var noteRepository: NoteRepository

    private var subjectName: String? = null
    private var subjectCode: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            subjectName = it.getString("subject_name")
            subjectCode = it.getString("subject_code")
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_teacher_lessons_list, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        initializeViews(view)
        setupData()
        setupClickListeners()
        loadDynamicLessons()
    }

    private fun initializeViews(view: View) {
        tvSubjectTitle = view.findViewById(R.id.tvSubjectTitle)
        llLesson1 = view.findViewById(R.id.llLesson1)
        llLesson2 = view.findViewById(R.id.llLesson2)
        llLesson3 = view.findViewById(R.id.llLesson3)
        llDynamicLessons = view.findViewById(R.id.llDynamicLessons)
        fabAddLesson = view.findViewById(R.id.fabAddLesson)
        noteRepository = NoteRepository(requireContext())
    }

    private fun setupData() {
        // Set the subject title
        subjectName?.let { subject ->
            tvSubjectTitle.text = subject
            
            // Update lesson titles based on subject
            updateLessonTitles(subject)
        }
    }

    private fun updateLessonTitles(subject: String) {
        val lesson1Title = view?.findViewById<TextView>(R.id.tvLesson1Title)
        val lesson2Title = view?.findViewById<TextView>(R.id.tvLesson2Title)
        val lesson3Title = view?.findViewById<TextView>(R.id.tvLesson3Title)

        when (subject) {
            "Operating System" -> {
                lesson1Title?.text = "1. Introduction to OS"
                lesson2Title?.text = "2. Process Management"
                lesson3Title?.text = "3. Memory Management"
            }
            "Statistics" -> {
                lesson1Title?.text = "1. Descriptive Statistics"
                lesson2Title?.text = "2. Probability Theory"
                lesson3Title?.text = "3. Statistical Inference"
            }
            "Database" -> {
                lesson1Title?.text = "1. Database Fundamentals"
                lesson2Title?.text = "2. SQL Queries"
                lesson3Title?.text = "3. Database Design"
            }
            "Programming" -> {
                lesson1Title?.text = "1. Programming Basics"
                lesson2Title?.text = "2. Data Structures"
                lesson3Title?.text = "3. Algorithms"
            }
        }
    }

    private fun setupClickListeners() {
        // Static lesson clicks
        llLesson1.setOnClickListener {
            // TODO: Navigate to lesson detail view
            openLessonDetail("1")
        }

        llLesson2.setOnClickListener {
            openLessonDetail("2")
        }

        llLesson3.setOnClickListener {
            openLessonDetail("3")
        }

        // FAB click to add new lesson
        fabAddLesson.setOnClickListener {
            navigateToAddNoteForm()
        }
    }

    private fun loadDynamicLessons() {
        // Load lessons from repository based on subject
        subjectCode?.let { code ->
            val notes = noteRepository.getNotesBySubject(code)
            
            // Clear existing dynamic lessons
            llDynamicLessons.removeAllViews()
            
            // Add dynamic lessons starting from lesson 4
            notes.forEachIndexed { index, note ->
                val lessonNumber = index + 4 // Start from lesson 4
                addDynamicLessonCard(lessonNumber, note)
            }
        }
    }

    private fun addDynamicLessonCard(lessonNumber: Int, note: Note) {
        val inflater = LayoutInflater.from(context)
        
        // Create a row container if it's an odd lesson number
        val isOddLesson = lessonNumber % 2 == 1
        val rowContainer = if (isOddLesson) {
            val row = LinearLayout(context).apply {
                orientation = LinearLayout.HORIZONTAL
                layoutParams = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
                ).apply {
                    bottomMargin = 48 // 16dp converted to pixels
                }
            }
            llDynamicLessons.addView(row)
            row
        } else {
            llDynamicLessons.getChildAt(llDynamicLessons.childCount - 1) as LinearLayout
        }

        // Create lesson card
        val lessonCard = LinearLayout(context).apply {
            orientation = LinearLayout.VERTICAL
            layoutParams = LinearLayout.LayoutParams(0, 420).apply { // 140dp height
                weight = 1f
                if (isOddLesson) {
                    rightMargin = 24 // 8dp
                } else {
                    leftMargin = 24 // 8dp
                }
            }
            setPadding(48, 48, 48, 48) // 16dp padding
            setBackgroundResource(R.drawable.subject_card_background)
            isClickable = true
            isFocusable = true
        }

        // Add lesson icon
        val lessonIcon = inflater.inflate(android.R.layout.simple_list_item_1, null) as TextView
        lessonIcon.apply {
            layoutParams = LinearLayout.LayoutParams(144, 144).apply { // 48dp
                bottomMargin = 36 // 12dp
            }
            setBackgroundResource(R.drawable.lesson_card_icon)
        }

        // Add lesson title
        val lessonTitle = TextView(context).apply {
            text = "$lessonNumber. ${note.subjectCode} Lesson"
            textSize = 12f
            setTextColor(resources.getColor(android.R.color.black, null))
            gravity = android.view.Gravity.CENTER
            maxLines = 2
        }

        lessonCard.addView(lessonIcon)
        lessonCard.addView(lessonTitle)
        
        // Set click listener
        lessonCard.setOnClickListener {
            openLessonDetail(lessonNumber.toString(), note)
        }

        rowContainer.addView(lessonCard)

        // Add empty space if it's an even lesson number and the last one
        if (!isOddLesson) {
            val emptySpace = View(context).apply {
                layoutParams = LinearLayout.LayoutParams(0, 420).apply {
                    weight = 1f
                    leftMargin = 24
                }
            }
            rowContainer.addView(emptySpace)
        }
    }

    private fun openLessonDetail(lessonNumber: String, note: Note? = null) {
        // TODO: Navigate to lesson detail fragment
        // For now, just show the add note form
        navigateToAddNoteForm()
    }

    private fun navigateToAddNoteForm() {
        val fragment = TeacherAddNoteFormFragment.newInstance(subjectName ?: "")
        
        parentFragmentManager.beginTransaction()
            .replace(R.id.fragment_teacher_home, fragment)
            .addToBackStack(null)
            .commit()
    }

    override fun onResume() {
        super.onResume()
        // Refresh lessons when returning to this fragment
        loadDynamicLessons()
    }

    companion object {
        fun newInstance(subjectName: String, subjectCode: String): TeacherLessonsListFragment {
            val fragment = TeacherLessonsListFragment()
            val args = Bundle()
            args.putString("subject_name", subjectName)
            args.putString("subject_code", subjectCode)
            fragment.arguments = args
            return fragment
        }
    }
}
