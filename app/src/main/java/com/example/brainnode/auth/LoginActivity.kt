package com.example.brainnode.auth

import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.brainnode.R
import com.example.brainnode.data.repository.AuthRepository
import com.example.brainnode.data.models.UserType
import com.example.brainnode.student.home.StudentMainActivity
import com.example.brainnode.teacher.TeacherMainActivity
import com.example.brainnode.utils.GoogleSignInHelper
import kotlinx.coroutines.launch

class LoginActivity : AppCompatActivity() {
    
    private lateinit var etEmail: EditText
    private lateinit var etPassword: EditText
    private lateinit var btnContinue: Button
    private lateinit var tvSignUp: TextView
    private lateinit var ivGoogleLogin: ImageView
    
    private val authRepository = AuthRepository()
    private lateinit var googleSignInHelper: GoogleSignInHelper
    
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
        ivGoogleLogin = findViewById(R.id.ivGoogleLogin)
        
        // Initialize Google Sign-In helper
        googleSignInHelper = GoogleSignInHelper(this)
    }
    
    private fun setupClickListeners() {
        btnContinue.setOnClickListener {
            handleLogin()
        }
        
        tvSignUp.setOnClickListener {
            val intent = Intent(this, SignupActivity::class.java)
            startActivity(intent)
        }
        
        // Google Sign-In click listener
        ivGoogleLogin.setOnClickListener {
            handleGoogleSignIn()
        }
    }
    
    private fun prefillEmailIfAvailable() {
        // Pre-fill email if coming from signup
        val email = intent.getStringExtra("email")
        if (!email.isNullOrEmpty()) {
            etEmail.setText(email)
            etPassword.requestFocus()
        } else {
            // For testing - pre-fill with test account
            etEmail.setText("test@teacher.com")
            etPassword.setText("123456")
        }
    }
    
    private fun handleLogin() {
        val email = etEmail.text.toString().trim()
        val password = etPassword.text.toString().trim()
        
        // Validation
        if (!validateInput(email, password)) {
            return
        }
        
        // Check network connectivity
        if (!isNetworkAvailable()) {
            Toast.makeText(this, "No internet connection. Please check your network.", Toast.LENGTH_LONG).show()
            return
        }
        
        // Show loading state
        btnContinue.isEnabled = false
        btnContinue.text = "Signing In..."
        
        // Sign in with Firebase
        lifecycleScope.launch {
            try {
                val result = authRepository.signIn(email, password)
                
                result.fold(
                    onSuccess = { user ->
                        Toast.makeText(this@LoginActivity, "Welcome back, ${user.name}!", Toast.LENGTH_SHORT).show()
                        
                        // Navigate based on user type
                        navigateToHome(user.userType)
                    },
                    onFailure = { exception ->
                        val errorMessage = when {
                            exception.message?.contains("network", ignoreCase = true) == true -> 
                                "Network error. Please check your internet connection and try again."
                            exception.message?.contains("password", ignoreCase = true) == true -> 
                                "Invalid email or password. Please try again."
                            exception.message?.contains("user", ignoreCase = true) == true -> 
                                "No account found with this email. Please sign up first."
                            else -> "Login failed: ${exception.message}"
                        }
                        
                        Toast.makeText(this@LoginActivity, errorMessage, Toast.LENGTH_LONG).show()
                        
                        // Reset button state
                        btnContinue.isEnabled = true
                        btnContinue.text = "Continue"
                    }
                )
            } catch (e: Exception) {
                Toast.makeText(this@LoginActivity, "Unexpected error: ${e.message}", Toast.LENGTH_LONG).show()
                
                // Reset button state
                btnContinue.isEnabled = true
                btnContinue.text = "Continue"
            }
        }
    }
    
    private fun isNetworkAvailable(): Boolean {
        val connectivityManager = getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val network = connectivityManager.activeNetwork ?: return false
        val activeNetwork = connectivityManager.getNetworkCapabilities(network) ?: return false
        
        return when {
            activeNetwork.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> true
            activeNetwork.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> true
            activeNetwork.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) -> true
            else -> false
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
    
    private fun handleGoogleSignIn() {
        val signInIntent = googleSignInHelper.getSignInIntent()
        startActivityForResult(signInIntent, GoogleSignInHelper.RC_SIGN_IN)
    }
    
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        
        if (requestCode == GoogleSignInHelper.RC_SIGN_IN) {
            val account = googleSignInHelper.handleSignInResult(data)
            if (account != null) {
                // Check if user already exists in Firebase
                checkExistingUserOrShowRoleDialog(account.displayName ?: account.email ?: "User", account.idToken ?: "")
            } else {
                Toast.makeText(this, "Google Sign-In failed", Toast.LENGTH_SHORT).show()
            }
        }
    }
    
    private fun checkExistingUserOrShowRoleDialog(userName: String, idToken: String) {
        lifecycleScope.launch {
            // Check if user profile exists in Firestore
            val userCheckResult = authRepository.checkIfUserExists(idToken)
            
            userCheckResult.fold(
                onSuccess = { existingUser ->
                    if (existingUser != null) {
                        // User exists, navigate to appropriate home
                        navigateToHome(existingUser.userType)
                    } else {
                        // User authenticated with Google but no profile exists (new user)
                        // Sign them out and redirect to signup
                        googleSignInHelper.signOut()
                        Toast.makeText(this@LoginActivity, "Account not found. Please sign up first.", Toast.LENGTH_LONG).show()
                        
                        // Redirect to SignupActivity
                        val intent = Intent(this@LoginActivity, SignupActivity::class.java)
                        startActivity(intent)
                    }
                },
                onFailure = { exception ->
                    Toast.makeText(this@LoginActivity, "Authentication failed: ${exception.message}", Toast.LENGTH_LONG).show()
                }
            )
        }
    }
    
}