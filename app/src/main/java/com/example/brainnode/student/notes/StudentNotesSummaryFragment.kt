package com.example.brainnode.student.notes

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.brainnode.databinding.FragmentStudentNotesSummaryBinding

class StudentNotesSummaryFragment : Fragment() {

    private var _binding: FragmentStudentNotesSummaryBinding? = null
    private val binding get() = _binding!!
    
    private var subjectName: String = "Operating System"
    private var lessonTitle: String = "1. Introduction to OS"

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentStudentNotesSummaryBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        // Get data from arguments
        subjectName = arguments?.getString("subject_name") ?: "Operating System"
        lessonTitle = arguments?.getString("lesson_title") ?: "1. Introduction to OS"
        
        setupUI()
        setupClickListeners()
    }

    private fun setupUI() {
        // Set the lesson badge text
        val badgeText = "${getSubjectAbbreviation(subjectName)} : ${getLessonNumber(lessonTitle)}"
        binding.tvLessonBadge.text = badgeText
        
        // Set summary content based on subject and lesson
        binding.tvSummaryContent.text = getSummaryContent(subjectName, lessonTitle)
    }

    private fun getSubjectAbbreviation(subject: String): String {
        return when (subject) {
            "Operating System" -> "OS"
            "Statistics" -> "STAT"
            "Programming" -> "PROG"
            else -> "OS"
        }
    }

    private fun getLessonNumber(lesson: String): String {
        return when {
            lesson.contains("1.") -> "Lesson 1"
            lesson.contains("2.") -> "Lesson 2"
            lesson.contains("3.") -> "Lesson 3"
            else -> "Lesson 1"
        }
    }

    private fun getSummaryContent(subject: String, lesson: String): String {
        return when (subject) {
            "Operating System" -> {
                when {
                    lesson.contains("1.") -> "An operating system (OS) is the core software that connects hardware and applications, making computers usable. It manages resources like CPU, memory, and devices so multiple programs can run smoothly and safely. The OS also handles processes, scheduling tasks and isolating failures, and provides security through user accounts and access control. Different OS types exist for personal computers, mobiles, servers, and embedded systems, but all share the same goal: ensuring efficiency, stability, and usability."
                    lesson.contains("2.") -> "Process management is the OS function that controls program execution. Each running program becomes a process with its own memory space and resources. The OS scheduler decides which process runs when, using algorithms to share CPU time fairly. Context switching allows multiple processes to run simultaneously by rapidly switching between them. The OS also provides synchronization tools like semaphores and mutexes to prevent conflicts when processes share resources, ensuring system stability and data integrity."
                    lesson.contains("3.") -> "Memory management organizes and controls how programs use computer memory. Virtual memory creates the illusion of unlimited memory by using disk storage when RAM is full. The OS divides memory into pages that can be swapped between RAM and disk as needed. Memory protection prevents programs from accessing each other's memory spaces, ensuring security and stability. The memory hierarchy from registers to disk is managed to optimize performance by keeping frequently used data in faster memory levels."
                    else -> "An operating system (OS) is the core software that connects hardware and applications, making computers usable. It manages resources like CPU, memory, and devices so multiple programs can run smoothly and safely."
                }
            }
            "Statistics" -> {
                when {
                    lesson.contains("1.") -> "Descriptive statistics summarizes and describes data features using measures of central tendency (mean, median, mode) and variability (range, variance, standard deviation). These tools help understand typical values and data spread. Data visualization through histograms, box plots, and scatter plots reveals patterns and relationships. Descriptive statistics forms the foundation for data analysis by organizing raw data into meaningful summaries that can be easily interpreted and communicated."
                    lesson.contains("2.") -> "Probability theory quantifies uncertainty and randomness using mathematical frameworks. It deals with sample spaces, events, and probability measures ranging from 0 to 1. Conditional probability describes event likelihood given other events, forming the basis for Bayes' theorem. Probability distributions like binomial, normal, and Poisson describe how probabilities are spread across possible outcomes, providing essential tools for statistical modeling and inference."
                    lesson.contains("3.") -> "Statistical inference draws conclusions about populations from sample data through hypothesis testing and confidence intervals. Hypothesis testing compares null and alternative hypotheses using test statistics and p-values to make decisions. Confidence intervals provide ranges of plausible parameter values. Type I and Type II errors represent false positives and false negatives respectively, with significance levels controlling error rates in statistical decision-making."
                    else -> "Descriptive statistics summarizes and describes data features using measures of central tendency and variability to understand typical values and data spread."
                }
            }
            "Programming" -> {
                when {
                    lesson.contains("1.") -> "Programming creates computer instructions by breaking complex problems into manageable steps. High-level languages like Python and Java are human-readable, while low-level languages provide hardware control. Basic concepts include variables for data storage, data types defining data kinds, operators for data manipulation, and control structures directing program flow. Problem-solving involves understanding requirements, designing algorithms, implementing solutions, testing, and debugging to create reliable software."
                    lesson.contains("2.") -> "Data structures organize and store data for efficient access and modification. Arrays provide fast indexed access but fixed size, while linked lists offer dynamic sizing with sequential access. Stacks use LIFO (Last-In-First-Out) for operations like function calls, and queues use FIFO (First-In-First-Out) for scheduling. Trees create hierarchical structures, with binary search trees enabling efficient searching, insertion, and deletion through ordered organization."
                    lesson.contains("3.") -> "Algorithms are step-by-step problem-solving procedures with complexity analysis measuring performance scaling. Time complexity indicates execution time growth, while space complexity measures memory usage, both expressed in Big O notation. Sorting algorithms like merge sort and quicksort use divide-and-conquer for efficiency. Searching algorithms include linear search for any data and binary search for sorted data. Recursion solves problems by breaking them into smaller similar problems with base cases preventing infinite loops."
                    else -> "Programming creates computer instructions by breaking complex problems into manageable steps using variables, data types, operators, and control structures."
                }
            }
            else -> "An operating system (OS) is the core software that connects hardware and applications, making computers usable."
        }
    }

    private fun setupClickListeners() {
        // TTS (Text-to-Speech) button click
        binding.cvTTSButton.setOnClickListener {
            // TODO: Implement TTS functionality for summary
            android.util.Log.d("StudentNotesSummary", "TTS clicked for summary: $subjectName - $lessonTitle")
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
