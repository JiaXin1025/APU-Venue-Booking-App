package com.example.apuvenuebookingsystem.ViewModel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

class UserViewModel : ViewModel() {
    var username: String by mutableStateOf("")

    suspend fun getFullNameByUsername(username: String): String? {
        val userCollection = FirebaseFirestore.getInstance().collection("Users")
        val query = userCollection.whereEqualTo("username", username).limit(1)

        return try {
            val snapshot = query.get().await()
            if (!snapshot.isEmpty) {
                val document = snapshot.documents[0]
                val fullName = document.getString("fullname")
                fullName
            } else {
                null
            }
        } catch (e: Exception) {
            null
        }
    }
}

