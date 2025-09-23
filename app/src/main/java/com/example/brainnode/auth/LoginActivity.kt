package com.example.brainnode.auth

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.brainnode.R
import com.example.brainnode.student.home.StudentMainActivity
import com.google.firebase.firestore.FirebaseFirestore
import android.util.Log

class LoginActivity : AppCompatActivity() {
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        
        // Test Firebase connection
        testFirebaseConnection()
        
        // Set up Continue button click listener
        val btnContinue = findViewById<Button>(R.id.btnContinue)
        btnContinue.setOnClickListener {
            // Navigate to Student Home
            val intent = Intent(this, StudentMainActivity::class.java)
            startActivity(intent)
            finish() // Close login activity
        }
        
        // Set up Sign Up click listener
        val tvSignUp = findViewById<TextView>(R.id.tvSignUp)
        tvSignUp.setOnClickListener {
            val intent = Intent(this, SignupActivity::class.java)
            startActivity(intent)
        }
    }
    
    private fun testFirebaseConnection() {
        FirebaseFirestore.getInstance()
            .collection("test")
            .add(mapOf("message" to "Hello Firebase!", "timestamp" to System.currentTimeMillis()))
            .addOnSuccessListener { documentReference ->
                Log.d("Firebase", "SUCCESS: Document added with ID: ${documentReference.id}")
            }
            .addOnFailureListener { e ->
                Log.e("Firebase", "ERROR: Failed to add document", e)
            }
    }
}