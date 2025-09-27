package com.example.brainnode.student.notes

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import com.example.brainnode.R
import com.example.brainnode.databinding.FragmentStudentNotesBinding

class StudentNotesFragment : Fragment() {

    private var _binding: FragmentStudentNotesBinding? = null
    private val binding get() = _binding!!
    
    private var subjectName: String = "Operating System"
    private var lessonTitle: String = "1. Introduction to OS"
    private var lessonContent: String = ""
    private var lessonSummary: String = ""
    private var lessonId: String = ""

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentStudentNotesBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        // Get data from arguments
        subjectName = arguments?.getString("subject_name") ?: "Operating System"
        lessonTitle = arguments?.getString("lesson_title") ?: "1. Introduction to OS"
        lessonContent = arguments?.getString("lesson_content") ?: ""
        lessonSummary = arguments?.getString("lesson_summary") ?: ""
        lessonId = arguments?.getString("lesson_id") ?: ""
        
        setupUI()
        setupClickListeners()
    }

    private fun setupUI() {
        // Set the lesson badge text
        val badgeText = "${getSubjectAbbreviation(subjectName)} : ${getLessonNumber(lessonTitle)}"
        binding.tvLessonBadge.text = badgeText
        
        // Set notes content - use Firebase content if available, otherwise fallback to hardcoded content
        binding.tvNotesContent.text = if (lessonContent.isNotEmpty()) {
            lessonContent
        } else {
            getNotesContent(subjectName, lessonTitle)
        }
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

    private fun getNotesContent(subject: String, lesson: String): String {
        return when (subject) {
            "Operating System" -> {
                when {
                    lesson.contains("1.") -> getOSLesson1Content()
                    lesson.contains("2.") -> getOSLesson2Content()
                    lesson.contains("3.") -> getOSLesson3Content()
                    else -> getOSLesson1Content()
                }
            }
            "Statistics" -> {
                when {
                    lesson.contains("1.") -> getStatisticsLesson1Content()
                    lesson.contains("2.") -> getStatisticsLesson2Content()
                    lesson.contains("3.") -> getStatisticsLesson3Content()
                    else -> getStatisticsLesson1Content()
                }
            }
            "Programming" -> {
                when {
                    lesson.contains("1.") -> getProgrammingLesson1Content()
                    lesson.contains("2.") -> getProgrammingLesson2Content()
                    lesson.contains("3.") -> getProgrammingLesson3Content()
                    else -> getProgrammingLesson1Content()
                }
            }
            else -> getOSLesson1Content()
        }
    }

    private fun getOSLesson1Content(): String {
        return "An operating system (OS) is the main software that makes a computer work. It acts as a bridge between the hardware — like the processor, memory, and storage — and the software applications that people use every day. Without an OS, you would have to communicate directly with the hardware in complex code, which would make computers almost impossible for normal users.\n\nThe OS manages hardware resources so that multiple applications can run at the same time without interfering with each other. For example, it decides which program gets CPU time, how memory is divided, and how data moves between devices like hard drives, printers, and keyboards. This resource management ensures efficiency and stability in the system.\n\nAnother important role of the OS is process management. Every task you run — whether opening a browser, playing music, or editing a file — is treated as a process. The OS schedules these processes, switches between them quickly, and keeps them isolated so that one crashing program doesn't break the entire system.\n\nThere are different types of operating systems depending on the need. Windows, Linux, and macOS are widely used on personal computers, while Android and iOS run on mobile devices. Large servers often use Linux because of its stability, and specialized devices use embedded operating systems. Despite their differences, all operating systems share the same core purpose: to make computers usable, efficient, and reliable."
    }

    private fun getOSLesson2Content(): String {
        return "Process management is one of the most critical functions of an operating system. A process is essentially a program in execution, containing the program code and its current activity. When you run an application, the OS creates a process for it, allocating necessary resources like memory and CPU time.\n\nThe OS maintains a process table that keeps track of all running processes. Each entry contains information such as process ID, current state, memory allocation, and priority level. Processes can be in various states: running, ready, waiting, or terminated. The OS scheduler decides which process should run next based on scheduling algorithms.\n\nMultitasking allows multiple processes to run simultaneously by rapidly switching between them. This is called context switching, where the OS saves the current process state and loads another process. Modern systems use preemptive multitasking, where the OS can interrupt a running process to give CPU time to another process.\n\nProcess synchronization is crucial when multiple processes need to access shared resources. The OS provides mechanisms like semaphores, mutexes, and monitors to prevent race conditions and ensure data consistency. Inter-process communication (IPC) allows processes to exchange data and coordinate their activities through pipes, message queues, and shared memory."
    }

    private fun getOSLesson3Content(): String {
        return "Memory management is a fundamental responsibility of the operating system that involves organizing and controlling computer memory usage. The OS must efficiently allocate memory to processes while ensuring system stability and preventing unauthorized access between different programs.\n\nVirtual memory is a key concept that allows the system to use more memory than physically available. The OS creates an illusion of a large, continuous memory space by using a combination of RAM and disk storage. Pages of memory can be swapped between RAM and disk as needed, allowing larger programs to run on systems with limited physical memory.\n\nThe memory hierarchy includes registers, cache, main memory (RAM), and secondary storage. The OS manages this hierarchy to optimize performance by keeping frequently accessed data in faster memory levels. Memory allocation strategies include contiguous allocation, paging, and segmentation, each with their own advantages and trade-offs.\n\nMemory protection ensures that processes cannot access memory belonging to other processes or the operating system itself. This is achieved through hardware support like memory management units (MMUs) that translate virtual addresses to physical addresses while enforcing access permissions. Garbage collection in some systems automatically reclaims memory that is no longer in use, preventing memory leaks."
    }

    private fun getStatisticsLesson1Content(): String {
        return "Descriptive statistics is the branch of statistics that deals with summarizing and describing the main features of a dataset. Unlike inferential statistics, which makes predictions about populations, descriptive statistics focuses on organizing, displaying, and summarizing data in a meaningful way.\n\nMeasures of central tendency include the mean, median, and mode. The mean is the arithmetic average of all values, the median is the middle value when data is arranged in order, and the mode is the most frequently occurring value. Each measure provides different insights into the typical value of a dataset.\n\nMeasures of variability describe how spread out the data points are. The range is the difference between the maximum and minimum values. Variance measures the average squared deviation from the mean, while standard deviation is the square root of variance and is expressed in the same units as the original data.\n\nData visualization techniques like histograms, box plots, and scatter plots help reveal patterns and relationships in data. These graphical representations make it easier to understand distributions, identify outliers, and communicate findings to others. Proper visualization is crucial for effective data analysis and presentation."
    }

    private fun getStatisticsLesson2Content(): String {
        return "Probability theory forms the mathematical foundation for understanding uncertainty and randomness. It provides tools for quantifying the likelihood of events and forms the basis for statistical inference and decision-making under uncertainty.\n\nBasic probability concepts include sample spaces, events, and probability measures. The sample space contains all possible outcomes of an experiment, while events are subsets of the sample space. Probability values range from 0 to 1, where 0 indicates impossibility and 1 indicates certainty.\n\nConditional probability describes the likelihood of an event occurring given that another event has already occurred. This concept is crucial for understanding dependence between events and forms the basis for Bayes' theorem, which allows us to update probabilities based on new information.\n\nProbability distributions describe how probabilities are distributed over the possible values of a random variable. Common discrete distributions include binomial and Poisson, while normal and exponential are important continuous distributions. Understanding these distributions is essential for statistical modeling and analysis."
    }

    private fun getStatisticsLesson3Content(): String {
        return "Statistical inference involves drawing conclusions about populations based on sample data. Since it's often impractical or impossible to study entire populations, we use samples to make educated guesses about population parameters.\n\nHypothesis testing is a formal procedure for making decisions about population parameters. We start with a null hypothesis (usually stating no effect) and an alternative hypothesis. Using sample data, we calculate test statistics and p-values to determine whether to reject the null hypothesis.\n\nConfidence intervals provide a range of plausible values for population parameters. A 95% confidence interval, for example, means that if we repeated the sampling process many times, about 95% of the intervals would contain the true population parameter.\n\nType I and Type II errors are important considerations in hypothesis testing. Type I error occurs when we reject a true null hypothesis (false positive), while Type II error occurs when we fail to reject a false null hypothesis (false negative). The significance level controls the probability of Type I error."
    }

    private fun getProgrammingLesson1Content(): String {
        return "Programming is the process of creating instructions for computers to execute. It involves breaking down complex problems into smaller, manageable steps and expressing these steps in a language that computers can understand.\n\nProgramming languages serve as the medium for communication between humans and computers. High-level languages like Python, Java, and JavaScript are closer to human language and are easier to read and write. Low-level languages like assembly are closer to machine code and provide more control over hardware.\n\nBasic programming concepts include variables, data types, operators, and control structures. Variables store data values, data types define what kind of data can be stored, operators perform operations on data, and control structures determine the flow of program execution.\n\nProblem-solving is at the heart of programming. This involves understanding the problem, designing an algorithm, implementing the solution in code, testing the program, and debugging any issues. Good programming practices include writing clear, readable code, using meaningful variable names, and adding comments to explain complex logic."
    }

    private fun getProgrammingLesson2Content(): String {
        return "Data structures are ways of organizing and storing data in computer memory to enable efficient access and modification. Choosing the right data structure is crucial for writing efficient programs and solving complex problems.\n\nArrays are the most basic data structure, storing elements of the same type in contiguous memory locations. They provide fast access to elements using indices but have fixed size in many languages. Dynamic arrays or lists can grow and shrink during program execution.\n\nLinked lists consist of nodes where each node contains data and a reference to the next node. Unlike arrays, linked lists can grow dynamically and insertion/deletion is efficient, but accessing elements requires traversing from the beginning.\n\nStacks follow the Last-In-First-Out (LIFO) principle, like a stack of plates. They support push (add to top) and pop (remove from top) operations. Queues follow First-In-First-Out (FIFO) principle, like a line of people, supporting enqueue (add to rear) and dequeue (remove from front) operations.\n\nTrees are hierarchical data structures with a root node and child nodes. Binary trees have at most two children per node, while binary search trees maintain a specific ordering property that enables efficient searching, insertion, and deletion operations."
    }

    private fun getProgrammingLesson3Content(): String {
        return "Algorithms are step-by-step procedures for solving problems or performing computations. They form the logical foundation of computer programs and determine how efficiently tasks are completed.\n\nAlgorithm complexity analysis helps us understand how algorithm performance scales with input size. Time complexity measures how execution time grows, while space complexity measures memory usage. Big O notation provides a standardized way to express these complexities.\n\nSorting algorithms arrange data in a specific order. Simple algorithms like bubble sort and insertion sort are easy to understand but inefficient for large datasets. Advanced algorithms like merge sort and quicksort use divide-and-conquer strategies to achieve better performance.\n\nSearching algorithms find specific elements in data structures. Linear search checks each element sequentially, while binary search efficiently finds elements in sorted arrays by repeatedly dividing the search space in half.\n\nRecursion is a programming technique where a function calls itself to solve smaller instances of the same problem. It's particularly useful for problems with recursive structure, like tree traversal and mathematical computations. However, recursive solutions must have a base case to prevent infinite recursion."
    }

    private fun setupClickListeners() {
        // Summarize button click
        binding.cvSummarizeButton.setOnClickListener {
            navigateToSummary()
        }

        // TTS (Text-to-Speech) button click
        binding.cvTTSButton.setOnClickListener {
            // TODO: Implement TTS functionality
            android.util.Log.d("StudentNotes", "TTS clicked for: $subjectName - $lessonTitle")
        }
    }

    private fun navigateToSummary() {
        // Create bundle to pass lesson and subject info to the summary fragment
        val bundle = Bundle().apply {
            putString("subject_name", subjectName)
            putString("lesson_title", lessonTitle)
            putString("lesson_content", lessonContent)
            putString("lesson_summary", lessonSummary)
            putString("lesson_id", lessonId)
        }
        
        // Navigate to summary fragment
        val summaryFragment = StudentNotesSummaryFragment().apply {
            arguments = bundle
        }
        
        val transaction: FragmentTransaction = parentFragmentManager.beginTransaction()
        transaction.replace(R.id.fragment_container, summaryFragment)
        transaction.addToBackStack(null)
        transaction.commit()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
