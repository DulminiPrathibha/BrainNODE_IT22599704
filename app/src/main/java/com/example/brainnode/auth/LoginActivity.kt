package com.example.brainnode.auth

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.brainnode.R
import com.example.brainnode.data.repository.AuthRepository
import com.example.brainnode.data.models.UserType
import com.example.brainnode.student.home.StudentMainActivity
import com.example.brainnode.teacher.TeacherMainActivity
import kotlinx.coroutines.launch

class LoginActivity : AppCompatActivity() {
    
    private lateinit var etEmail: EditText
    private lateinit var etPassword: EditText
    private lateinit var btnContinue: Button
    private lateinit var tvSignUp: TextView
    
    private val authRepository = AuthRepository()
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        
        initializeViews()
        setupClickListeners()
        prefillEmailIfAvailable()
    }
    
    private fun initializeViews() {
        etEmail = findViewById(R.id.etEmail)
        etPassword = findViewById(R.id.etPassword)
        btnContinue = findViewById(R.id.btnContinue)
        tvSignUp = findViewById(R.id.tvSignUp)
    }
    
    private fun setupClickListeners() {
        btnContinue.setOnClickListener {
            handleLogin()
        }
        
        tvSignUp.setOnClickListener {
            val intent = Intent(this, SignupActivity::class.java)
            startActivity(intent)
        }
    }
    
    private fun prefillEmailIfAvailable() {
        // Pre-fill email if coming from signup
        val email = intent.getStringExtra("email")
        if (!email.isNullOrEmpty()) {
            etEmail.setText(email)
            etPassword.requestFocus()
        }
    }
    
    private fun handleLogin() {
        val email = etEmail.text.toString().trim()
        val password = etPassword.text.toString().trim()
        
        // Validation
        if (!validateInput(email, password)) {
            return
        }
        
        // Show loading state
        btnContinue.isEnabled = false
        btnContinue.text = "Signing In..."
        
        // Sign in with Firebase
        lifecycleScope.launch {
            val result = authRepository.signIn(email, password)
            
            result.fold(
                onSuccess = { user ->
                    Toast.makeText(this@LoginActivity, "Welcome back, ${user.name}!", Toast.LENGTH_SHORT).show()
                    
                    // Navigate based on user type
                    navigateToHome(user.userType)
                },
                onFailure = { exception ->
                    Toast.makeText(this@LoginActivity, "Login failed: ${exception.message}", Toast.LENGTH_LONG).show()
                    
                    // Reset button state
                    btnContinue.isEnabled = true
                    btnContinue.text = "Continue"
                }
            )
        }
    }
    
    private fun validateInput(email: String, password: String): Boolean {
        if (email.isEmpty()) {
            etEmail.error = "Email is required"
            etEmail.requestFocus()
            return false
        }
        
        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            etEmail.error = "Please enter a valid email"
            etEmail.requestFocus()
            return false
        }
        
        if (password.isEmpty()) {
            etPassword.error = "Password is required"
            etPassword.requestFocus()
            return false
        }
        
        return true
    }
    
    private fun navigateToHome(userType: UserType) {
        val intent = when (userType) {
            UserType.STUDENT -> Intent(this, StudentMainActivity::class.java)
            UserType.TEACHER -> Intent(this, TeacherMainActivity::class.java)
        }
        
        startActivity(intent)
        finish() // Close login activity
    }
}