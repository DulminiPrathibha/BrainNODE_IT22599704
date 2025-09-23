package com.example.brainnode.data.repository

import com.example.brainnode.data.firebase.FirebaseAuthService
import com.example.brainnode.data.models.User
import com.example.brainnode.data.models.UserType

class AuthRepository {
    private val authService = FirebaseAuthService()
    
    suspend fun signUp(email: String, password: String, name: String, userType: UserType): Result<User> {
        return authService.signUp(email, password, name, userType)
    }
    
    suspend fun signIn(email: String, password: String): Result<User> {
        return authService.signIn(email, password)
    }
    
    suspend fun signOut(): Result<Unit> {
        return authService.signOut()
    }
    
    suspend fun getCurrentUser(): Result<User?> {
        return authService.getCurrentUserData()
    }
    
    suspend fun updateUserProfile(user: User): Result<Unit> {
        return authService.updateUserProfile(user)
    }
    
    suspend fun resetPassword(email: String): Result<Unit> {
        return authService.resetPassword(email)
    }
    
    fun isUserLoggedIn(): Boolean {
        return authService.isUserLoggedIn()
    }
}
