package com.example.trail_tales_front_end_one.android.auth

import androidx.compose.runtime.mutableStateOf
import com.google.firebase.auth.FirebaseUser
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

object UserSession {
    private val _currentUser = MutableStateFlow<FirebaseUser?>(null)
    val currentUser: StateFlow<FirebaseUser?> = _currentUser

    private val _isLoading = mutableStateOf(false)
    val isLoading = _isLoading

    fun updateUser(user: FirebaseUser?) {
        _currentUser.value = user
    }

    fun setLoading(loading: Boolean) {
        _isLoading.value = loading
    }
} 