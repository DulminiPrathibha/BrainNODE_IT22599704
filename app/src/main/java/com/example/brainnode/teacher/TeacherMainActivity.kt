package com.example.brainnode.teacher

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.example.brainnode.R
import com.example.brainnode.teacher.addnotes.TeacherAddNotesFragment
import com.example.brainnode.teacher.addquizzes.TeacherAddQuizzesFragment
import com.example.brainnode.teacher.home.TeacherHomeFragment
import com.example.brainnode.teacher.progress.ManageStudentsProgressFragment
import com.google.android.material.bottomnavigation.BottomNavigationView

class TeacherMainActivity : AppCompatActivity() {
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_teacher_main)
        
        // Load TeacherHomeFragment by default
        if (savedInstanceState == null) {
            loadFragment(TeacherHomeFragment())
        }
        
        // Set up bottom navigation
        val bottomNavigation = findViewById<BottomNavigationView>(R.id.bottom_navigation_teacher)
        bottomNavigation.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.navigation_teacher_home -> {
                    loadFragment(TeacherHomeFragment())
                    true
                }
                R.id.navigation_teacher_add_notes -> {
                    loadFragment(TeacherAddNotesFragment())
                    true
                }
                R.id.navigation_teacher_add_quiz -> {
                    loadFragment(TeacherAddQuizzesFragment())
                    true
                }
                R.id.navigation_teacher_progress -> {
                    loadFragment(ManageStudentsProgressFragment())
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