package com.example.brainnode.auth

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import com.example.brainnode.R
import com.example.brainnode.teacher.TeacherMainActivity

class SignupActivity : AppCompatActivity() {
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_signup)
        
        val btnContinue = findViewById<Button>(R.id.btnContinue)
        btnContinue.setOnClickListener {
            // Navigate to Teacher Home
            val intent = Intent(this, TeacherMainActivity::class.java)
            startActivity(intent)
            finish() // Optional: finish current activity so user can't go back
        }
    }
}