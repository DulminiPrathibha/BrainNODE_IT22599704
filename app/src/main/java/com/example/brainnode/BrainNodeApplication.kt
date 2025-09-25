package com.example.brainnode

import android.app.Application
import com.google.firebase.FirebaseApp

class BrainNodeApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        
        // Initialize Firebase
        FirebaseApp.initializeApp(this)
    }
}
