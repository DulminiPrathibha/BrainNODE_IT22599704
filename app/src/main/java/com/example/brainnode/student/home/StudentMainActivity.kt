package com.example.brainnode.student.home

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.FragmentTransaction
import com.example.brainnode.R

class StudentMainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_student_main)
        
        // Load StudentHomeFragment by default
        if (savedInstanceState == null) {
            val studentHomeFragment = StudentHomeFragment()
            val transaction: FragmentTransaction = supportFragmentManager.beginTransaction()
            transaction.replace(R.id.fragment_container, studentHomeFragment)
            transaction.commit()
        }
    }
}