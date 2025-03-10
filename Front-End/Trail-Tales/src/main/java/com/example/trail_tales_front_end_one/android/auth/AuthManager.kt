package com.example.trail_tales_front_end_one.android.auth

import android.app.Activity
import android.content.Intent
import com.google.android.gms.auth.api.signin.*
import com.google.firebase.auth.*
import kotlinx.coroutines.tasks.await

class AuthManager(private val activity: Activity) {
    private val auth = FirebaseAuth.getInstance()
    private val googleSignInClient = GoogleSignIn.getClient(
        activity,
        GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken("324587025006-rseaf0c3tqnjd9ip15k2cep4mfkia8lr.apps.googleusercontent.com")
            .requestEmail()
            .build()
    )

    private var pendingOnSuccess: ((FirebaseUser?) -> Unit)? = null
    private var pendingOnError: ((Exception) -> Unit)? = null

    init {
        UserSession.updateUser(auth.currentUser)
    }

    fun signInWithGoogle(onSuccess: (FirebaseUser?) -> Unit, onError: (Exception) -> Unit) {
        pendingOnSuccess = onSuccess
        pendingOnError = onError
        val signInIntent = googleSignInClient.signInIntent
        activity.startActivityForResult(signInIntent, RC_SIGN_IN)
    }

    fun handleSignInResult(data: Intent?) {
        val task = GoogleSignIn.getSignedInAccountFromIntent(data)
        try {
            val account = task.getResult()
            val credential = GoogleAuthProvider.getCredential(account.idToken, null)
            auth.signInWithCredential(credential)
                .addOnCompleteListener { authTask ->
                    if (authTask.isSuccessful) {
                        pendingOnSuccess?.invoke(auth.currentUser)
                    } else {
                        pendingOnError?.invoke(authTask.exception ?: Exception("Unknown error"))
                    }
                }
        } catch (e: Exception) {
            pendingOnError?.invoke(e)
        }
    }

    suspend fun signInWithEmail(email: String, password: String): Result<FirebaseUser> {
        return try {
            UserSession.setLoading(true)
            val result = auth.signInWithEmailAndPassword(email, password).await()
            UserSession.updateUser(result.user)
            Result.success(result.user!!)
        } catch (e: Exception) {
            Result.failure(e)
        } finally {
            UserSession.setLoading(false)
        }
    }

    suspend fun registerWithEmail(email: String, password: String): Result<FirebaseUser> {
        return try {
            UserSession.setLoading(true)
            val result = auth.createUserWithEmailAndPassword(email, password).await()
            UserSession.updateUser(result.user)
            Result.success(result.user!!)
        } catch (e: Exception) {
            Result.failure(e)
        } finally {
            UserSession.setLoading(false)
        }
    }

    fun sendEmailVerification(user: FirebaseUser, callback: (Boolean) -> Unit) {
        user.sendEmailVerification()
            .addOnCompleteListener { task ->
                callback(task.isSuccessful)
            }
    }

    fun sendPasswordResetEmail(email: String, callback: (Boolean) -> Unit) {
        auth.sendPasswordResetEmail(email)
            .addOnCompleteListener { task ->
                callback(task.isSuccessful)
            }
    }

    fun signOut() {
        auth.signOut()
        googleSignInClient.signOut()
        UserSession.updateUser(null)
    }

    companion object {
        const val RC_SIGN_IN = 9001
    }
}