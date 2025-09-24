package com.example.brainnode.auth

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.brainnode.R
import com.example.brainnode.data.repository.AuthRepository
import com.example.brainnode.data.models.UserType
import com.example.brainnode.utils.GoogleSignInHelper
import com.example.brainnode.utils.RoleSelectionDialog
import com.example.brainnode.student.home.StudentMainActivity
import com.example.brainnode.teacher.TeacherMainActivity
import kotlinx.coroutines.launch

class SignupActivity : AppCompatActivity() {
    
    private lateinit var etFullName: EditText
    private lateinit var etEmail: EditText
    private lateinit var etPassword: EditText
    private lateinit var etConfirmPassword: EditText
    private lateinit var btnStudent: Button
    private lateinit var btnTeacher: Button
    private lateinit var btnContinue: Button
    private lateinit var ivGoogleLogin: ImageView
    
    private val authRepository = AuthRepository()
    private lateinit var googleSignInHelper: GoogleSignInHelper
    private var selectedUserType: UserType = UserType.STUDENT
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_signup)
        
        initializeViews()
        setupClickListeners()
    }
    
    private fun initializeViews() {
        etFullName = findViewById(R.id.etFullName)
        etEmail = findViewById(R.id.etEmail)
        etPassword = findViewById(R.id.etPassword)
        etConfirmPassword = findViewById(R.id.etConfirmPassword)
        btnStudent = findViewById(R.id.btnStudent)
        btnTeacher = findViewById(R.id.btnTeacher)
        btnContinue = findViewById(R.id.btnContinue)
        ivGoogleLogin = findViewById(R.id.ivGoogleLogin)
        
        // Initialize Google Sign-In helper
        googleSignInHelper = GoogleSignInHelper(this)
        
        // Set default selection to Student
        selectUserType(UserType.STUDENT)
    }
    
    private fun setupClickListeners() {
        btnStudent.setOnClickListener {
            selectUserType(UserType.STUDENT)
        }
        
        btnTeacher.setOnClickListener {
            selectUserType(UserType.TEACHER)
        }
        
        btnContinue.setOnClickListener {
            handleSignUp()
        }
        
        // Navigate to login if user already has account
        findViewById<android.widget.TextView>(R.id.tvLogIn).setOnClickListener {
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
        }
        
        // Google Sign-In click listener
        ivGoogleLogin.setOnClickListener {
            handleGoogleSignIn()
        }
    }
    
    private fun selectUserType(userType: UserType) {
        selectedUserType = userType
        
        // Update button appearances
        if (userType == UserType.STUDENT) {
            btnStudent.setBackgroundResource(R.drawable.continue_button_background)
            btnStudent.setTextColor(getColor(android.R.color.white))
            btnTeacher.setBackgroundResource(R.drawable.input_field_background)
            btnTeacher.setTextColor(getColor(R.color.black))
        } else {
            btnTeacher.setBackgroundResource(R.drawable.continue_button_background)
            btnTeacher.setTextColor(getColor(android.R.color.white))
            btnStudent.setBackgroundResource(R.drawable.input_field_background)
            btnStudent.setTextColor(getColor(R.color.black))
        }
    }
    
    private fun handleSignUp() {
        val fullName = etFullName.text.toString().trim()
        val email = etEmail.text.toString().trim()
        val password = etPassword.text.toString().trim()
        val confirmPassword = etConfirmPassword.text.toString().trim()
        
        // Validation
        if (!validateInput(fullName, email, password, confirmPassword)) {
            return
        }
        
        // Show loading state
        btnContinue.isEnabled = false
        btnContinue.text = "Creating Account..."
        
        // Create account with Firebase
        lifecycleScope.launch {
            val result = authRepository.signUp(email, password, fullName, selectedUserType)
            
            result.fold(
                onSuccess = { user ->
                    Toast.makeText(this@SignupActivity, "Account created successfully!", Toast.LENGTH_SHORT).show()
                    
                    // Navigate to login activity
                    val intent = Intent(this@SignupActivity, LoginActivity::class.java)
                    intent.putExtra("email", email) // Pre-fill email in login
                    startActivity(intent)
                    finish()
                },
                onFailure = { exception ->
                    Toast.makeText(this@SignupActivity, "Sign up failed: ${exception.message}", Toast.LENGTH_LONG).show()
                    
                    // Reset button state
                    btnContinue.isEnabled = true
                    btnContinue.text = "Continue"
                }
            )
        }
    }
    
    private fun validateInput(fullName: String, email: String, password: String, confirmPassword: String): Boolean {
        if (fullName.isEmpty()) {
            etFullName.error = "Full name is required"
            etFullName.requestFocus()
            return false
        }
        
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
        
        if (password.length < 6) {
            etPassword.error = "Password must be at least 6 characters"
            etPassword.requestFocus()
            return false
        }
        
        if (password != confirmPassword) {
            etConfirmPassword.error = "Passwords do not match"
            etConfirmPassword.requestFocus()
            return false
        }
        
        return true
    }
    
    private fun handleGoogleSignIn() {
        val signInIntent = googleSignInHelper.getSignInIntent()
        startActivityForResult(signInIntent, GoogleSignInHelper.RC_SIGN_IN)
    }
    
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        
        if (requestCode == GoogleSignInHelper.RC_SIGN_IN) {
            val account = googleSignInHelper.handleSignInResult(data)
            if (account != null) {
                // Show role selection dialog
                showRoleSelectionDialog(account.displayName ?: account.email ?: "User", account.idToken ?: "")
            } else {
                Toast.makeText(this, "Google Sign-In failed", Toast.LENGTH_SHORT).show()
            }
        }
    }
    
    private fun showRoleSelectionDialog(userName: String, idToken: String) {
        val dialog = RoleSelectionDialog(this) { selectedRole ->
            // Proceed with Google Sign-In using selected role
            proceedWithGoogleSignIn(userName, idToken, selectedRole)
        }
        dialog.show()
    }
    
    private fun proceedWithGoogleSignIn(userName: String, idToken: String, userType: UserType) {
        lifecycleScope.launch {
            val result = authRepository.signInWithGoogle(idToken, userName, userType)
            
            result.fold(
                onSuccess = { user ->
                    // Navigate to appropriate home page
                    val intent = when (userType) {
                        UserType.STUDENT -> Intent(this@SignupActivity, StudentMainActivity::class.java)
                        UserType.TEACHER -> Intent(this@SignupActivity, TeacherMainActivity::class.java)
                    }
                    
                    startActivity(intent)
                    finish()
                },
                onFailure = { exception ->
                    Toast.makeText(this@SignupActivity, "Google Sign-In failed: ${exception.message}", Toast.LENGTH_LONG).show()
                }
            )
        }
    }
}