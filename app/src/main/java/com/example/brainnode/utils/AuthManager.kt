package com.example.brainnode.utils

import android.content.Context
import android.content.SharedPreferences
import com.example.brainnode.data.models.User
import com.example.brainnode.data.models.UserType
import com.google.gson.Gson

class AuthManager(context: Context) {
    
    private val sharedPreferences: SharedPreferences = 
        context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
    private val gson = Gson()
    
    companion object {
        private const val PREF_NAME = "BrainNodeAuth"
        private const val KEY_USER_DATA = "user_data"
        private const val KEY_IS_LOGGED_IN = "is_logged_in"
    }
    
    fun saveUserData(user: User) {
        val userJson = gson.toJson(user)
        sharedPreferences.edit()
            .putString(KEY_USER_DATA, userJson)
            .putBoolean(KEY_IS_LOGGED_IN, true)
            .apply()
    }
    
    fun getCurrentUser(): User? {
        val userJson = sharedPreferences.getString(KEY_USER_DATA, null)
        return if (userJson != null) {
            try {
                gson.fromJson(userJson, User::class.java)
            } catch (e: Exception) {
                null
            }
        } else {
            null
        }
    }
    
    fun isLoggedIn(): Boolean {
        return sharedPreferences.getBoolean(KEY_IS_LOGGED_IN, false)
    }
    
    fun getUserType(): UserType? {
        return getCurrentUser()?.userType
    }
    
    fun clearUserData() {
        sharedPreferences.edit()
            .remove(KEY_USER_DATA)
            .putBoolean(KEY_IS_LOGGED_IN, false)
            .apply()
    }
    
    fun updateUserData(user: User) {
        saveUserData(user)
    }
}
