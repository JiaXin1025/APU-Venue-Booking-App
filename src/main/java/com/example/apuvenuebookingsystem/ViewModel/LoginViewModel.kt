package com.example.apuvenuebookingsystem.ViewModel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.apuvenuebookingsystem.model.Users
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class LoginViewModel : ViewModel() {

    private val db = Firebase.firestore
    private val _loginState = Channel<LoginState>()
    val loginState = _loginState.receiveAsFlow()
    var isTestingMode = false

    fun login(username: String, password: String) {
        viewModelScope.launch {
            try {
                Log.d("Login",username)
                val querySnapshot = db.collection("Users")
                    .whereEqualTo("username", username)
                    .whereEqualTo("password", password)
                    .get()
                    .await()

                if (querySnapshot.documents.isEmpty()) {
                    _loginState.send(LoginState.Failure("Invalid credentials"))
                } else {
                    // Convert the first document into a Users object (assuming unique usernames)
                    val user = querySnapshot.documents[0].toObject(Users::class.java)
                    if (user != null) {
                        _loginState.send(LoginState.Success(user))

                    } else {
                        _loginState.send(LoginState.Failure("User data parsing error"))
                    }
                }
            } catch (e: Exception) {
                _loginState.send(LoginState.Failure("Error accessing database: ${e.localizedMessage}"))
                Log.e("LoginViewModel", "Error: ", e)
            }
        }
    }

    sealed class LoginState {
        data class Success(val user: Users) : LoginState() // Success now contains a Users object
        data class Failure(val message: String) : LoginState()
    }
}