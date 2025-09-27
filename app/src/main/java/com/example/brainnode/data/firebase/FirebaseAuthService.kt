package com.example.brainnode.data.firebase

import com.example.brainnode.data.models.User
import com.example.brainnode.data.models.UserType
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class FirebaseAuthService {
    private val auth = FirebaseAuth.getInstance()
    private val firestore = FirebaseFirestore.getInstance()
    
    fun getCurrentUser(): FirebaseUser? = auth.currentUser
    
    fun isUserLoggedIn(): Boolean = auth.currentUser != null
    
    suspend fun signUp(email: String, password: String, name: String, userType: UserType): Result<User> {
        return try {
            val authResult = auth.createUserWithEmailAndPassword(email, password).await()
            val firebaseUser = authResult.user
            
            if (firebaseUser != null) {
                val user = User(
                    uid = firebaseUser.uid,
                    email = email,
                    name = name,
                    userType = userType,
                    createdAt = System.currentTimeMillis(),
                    lastLoginAt = System.currentTimeMillis()
                )
                
                // Save user data to Firestore
                firestore.collection("users")
                    .document(firebaseUser.uid)
                    .set(user)
                    .await()
                
                Result.success(user)
            } else {
                Result.failure(Exception("Failed to create user"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    suspend fun signIn(email: String, password: String): Result<User> {
        return try {
            val authResult = auth.signInWithEmailAndPassword(email, password).await()
            val firebaseUser = authResult.user
            
            if (firebaseUser != null) {
                // Update last login time
                firestore.collection("users")
                    .document(firebaseUser.uid)
                    .update("lastLoginAt", System.currentTimeMillis())
                    .await()
                
                // Get user data from Firestore
                val userDoc = firestore.collection("users")
                    .document(firebaseUser.uid)
                    .get()
                    .await()
                
                val user = userDoc.toObject(User::class.java)
                if (user != null) {
                    Result.success(user)
                } else {
                    Result.failure(Exception("User data not found"))
                }
            } else {
                Result.failure(Exception("Failed to sign in"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    suspend fun signOut(): Result<Unit> {
        return try {
            auth.signOut()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    suspend fun getCurrentUserData(): Result<User?> {
        return try {
            val firebaseUser = auth.currentUser
            if (firebaseUser != null) {
                val userDoc = firestore.collection("users")
                    .document(firebaseUser.uid)
                    .get()
                    .await()
                
                val user = userDoc.toObject(User::class.java)
                Result.success(user)
            } else {
                Result.success(null)
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    suspend fun updateUserProfile(user: User): Result<Unit> {
        return try {
            val currentUser = auth.currentUser
            if (currentUser != null) {
                firestore.collection("users")
                    .document(currentUser.uid)
                    .set(user)
                    .await()
                Result.success(Unit)
            } else {
                Result.failure(Exception("No user logged in"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    suspend fun resetPassword(email: String): Result<Unit> {
        return try {
            auth.sendPasswordResetEmail(email).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    suspend fun signInWithGoogle(idToken: String, name: String, userType: UserType): Result<User> {
        return try {
            val credential = GoogleAuthProvider.getCredential(idToken, null)
            val authResult = auth.signInWithCredential(credential).await()
            val firebaseUser = authResult.user
            
            if (firebaseUser != null) {
                // Check if user already exists
                val userDoc = firestore.collection("users")
                    .document(firebaseUser.uid)
                    .get()
                    .await()
                
                val user = if (userDoc.exists()) {
                    // Existing user - update last login
                    val existingUser = userDoc.toObject(User::class.java)!!
                    firestore.collection("users")
                        .document(firebaseUser.uid)
                        .update("lastLoginAt", System.currentTimeMillis())
                        .await()
                    existingUser.copy(lastLoginAt = System.currentTimeMillis())
                } else {
                    // New user - create profile
                    val newUser = User(
                        uid = firebaseUser.uid,
                        email = firebaseUser.email ?: "",
                        name = name,
                        userType = userType,
                        createdAt = System.currentTimeMillis(),
                        lastLoginAt = System.currentTimeMillis()
                    )
                    
                    firestore.collection("users")
                        .document(firebaseUser.uid)
                        .set(newUser)
                        .await()
                    
                    newUser
                }
                
                Result.success(user)
            } else {
                Result.failure(Exception("Failed to sign in with Google"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    suspend fun checkIfUserExists(idToken: String): Result<User?> {
        return try {
            val credential = GoogleAuthProvider.getCredential(idToken, null)
            val authResult = auth.signInWithCredential(credential).await()
            val firebaseUser = authResult.user
            
            if (firebaseUser != null) {
                // Check if user profile exists in Firestore
                val userDoc = firestore.collection("users")
                    .document(firebaseUser.uid)
                    .get()
                    .await()
                
                if (userDoc.exists()) {
                    val existingUser = userDoc.toObject(User::class.java)
                    Result.success(existingUser)
                } else {
                    // User authenticated with Google but no profile in Firestore (new user)
                    Result.success(null)
                }
            } else {
                Result.failure(Exception("Failed to authenticate with Google"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    suspend fun getUsersByIds(userIds: List<String>): Result<Map<String, String>> {
        return try {
            val userNames = mutableMapOf<String, String>()
            
            // Fetch users in batches to avoid Firestore limits
            userIds.chunked(10).forEach { batch ->
                val querySnapshot = firestore.collection("users")
                    .whereIn("uid", batch)
                    .get()
                    .await()
                
                querySnapshot.documents.forEach { doc ->
                    val user = doc.toObject(User::class.java)
                    if (user != null) {
                        userNames[user.uid] = user.name.ifEmpty { "Student ${user.uid.take(6)}" }
                    }
                }
            }
            
            // For any missing users, add fallback names
            userIds.forEach { uid ->
                if (!userNames.containsKey(uid)) {
                    userNames[uid] = "Student ${uid.take(6)}"
                }
            }
            
            println("📝 Fetched names for ${userNames.size} users")
            Result.success(userNames)
        } catch (e: Exception) {
            println("❌ Error fetching user names: ${e.message}")
            Result.failure(e)
        }
    }
}
