package com.example.brainnode.student.home

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.example.brainnode.R
import com.example.brainnode.student.notes.NotesSubjectSelectionFragment
import com.example.brainnode.student.profile.ProfileFragment
import com.example.brainnode.student.quizzes.QuizzesFragment
import com.google.android.material.bottomnavigation.BottomNavigationView

class StudentMainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_student_main)
        
        // Load StudentHomeFragment by default
        if (savedInstanceState == null) {
            loadFragment(StudentHomeFragment())
        }
        
        // Set up bottom navigation
        val bottomNavigation = findViewById<BottomNavigationView>(R.id.bottom_navigation_student)
        bottomNavigation.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.navigation_student_home -> {
                    loadFragment(StudentHomeFragment())
                    true
                }
                R.id.navigation_student_notes -> {
                    loadFragment(NotesSubjectSelectionFragment())
                    true
                }
                R.id.navigation_student_quiz -> {
                    loadFragment(QuizzesFragment())
                    true
                }
                R.id.navigation_student_profile -> {
                    loadFragment(ProfileFragment())
                    true
                }
                else -> false
            }
        }
    }
    
    private fun loadFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, fragment)
            .commit()
    }
}