package com.example.apuvenuebookingsystem

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.apuvenuebookingsystem.NavigationBar.MainScreen
import com.example.apuvenuebookingsystem.ViewModel.UserViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MyApp()
        }
    }
    @Composable
    fun MyApp() {
        // Initialize UserViewModel
        val userViewModel: UserViewModel = viewModel()

        // Call MainScreen with UserViewModel
        MainScreen(userViewModel)
    }
}