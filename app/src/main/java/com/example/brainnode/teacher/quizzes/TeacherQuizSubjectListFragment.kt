package com.example.brainnode.teacher.quizzes

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
// Removed RecyclerView imports as we're now using LinearLayout
import com.example.brainnode.R
import com.example.brainnode.teacher.addquizzes.TeacherAddQuizzesFragment
import com.example.brainnode.teacher.quizzes.adapters.QuizSubjectAdapter
import com.example.brainnode.teacher.quizzes.models.QuizSubject
import com.google.android.material.floatingactionbutton.FloatingActionButton

class TeacherQuizSubjectListFragment : Fragment() {

    private val viewModel: TeacherQuizListViewModel by viewModels()
    private lateinit var adapter: QuizSubjectAdapter
    private lateinit var llQuizContainer: LinearLayout
    private lateinit var emptyStateLayout: LinearLayout
    private lateinit var fabAddQuiz: FloatingActionButton

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_teacher_quiz_list, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        initViews(view)
        setupRecyclerView()
        setupClickListeners()
        observeViewModel()
    }

    private fun initViews(view: View) {
        llQuizContainer = view.findViewById(R.id.llQuizContainer)
        emptyStateLayout = view.findViewById(R.id.emptyStateLayout)
        fabAddQuiz = view.findViewById(R.id.fabAddQuiz)
    }

    private fun setupRecyclerView() {
        adapter = QuizSubjectAdapter { subject ->
            onSubjectClick(subject)
        }
        
        // Note: This fragment now uses LinearLayout instead of RecyclerView
        // The adapter functionality would need to be reimplemented for LinearLayout
        // For now, we'll disable this functionality
    }

    private fun setupClickListeners() {
        fabAddQuiz.setOnClickListener {
            navigateToAddQuiz()
        }
    }

    private fun observeViewModel() {
        viewModel.subjects.observe(viewLifecycleOwner) { subjects ->
            // adapter.updateSubjects(subjects) // Disabled for now
        }

        viewModel.isEmpty.observe(viewLifecycleOwner) { isEmpty ->
            if (isEmpty) {
                llQuizContainer.visibility = View.GONE
                emptyStateLayout.visibility = View.VISIBLE
            } else {
                llQuizContainer.visibility = View.VISIBLE
                emptyStateLayout.visibility = View.GONE
            }
        }

        viewModel.isLoading.observe(viewLifecycleOwner) { isLoading ->
            // TODO: Show/hide loading indicator if needed
        }
    }

    private fun onSubjectClick(subject: QuizSubject) {
        // TODO: Navigate to quiz details for this subject
        // For now, just navigate to add quiz for this subject
        navigateToAddQuiz(subject.name)
    }

    private fun navigateToAddQuiz(subjectName: String = "") {
        val addQuizFragment = TeacherAddQuizzesFragment()
        
        // Pass subject name if available
        if (subjectName.isNotEmpty()) {
            val bundle = Bundle()
            bundle.putString("subject_name", subjectName)
            addQuizFragment.arguments = bundle
        }

        parentFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, addQuizFragment)
            .addToBackStack(null)
            .commit()
    }

    companion object {
        fun newInstance(): TeacherQuizSubjectListFragment {
            return TeacherQuizSubjectListFragment()
        }
    }
}
