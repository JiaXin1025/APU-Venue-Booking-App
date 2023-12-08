package com.example.apuvenuebookingsystem.screens.authentication

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.example.apuvenuebookingsystem.NavigationBar.NavRoutes
import com.example.apuvenuebookingsystem.R
import com.example.apuvenuebookingsystem.ViewModel.LoginViewModel
import com.example.apuvenuebookingsystem.ViewModel.UserViewModel
import com.example.apuvenuebookingsystem.ui.theme.APUBlue

@Composable
fun LoginScreen(navController: NavHostController, loginViewModel: LoginViewModel = viewModel(), userViewModel: UserViewModel) {
    val loginState by loginViewModel.loginState.collectAsState(initial = null)
    val viewModel: LoginViewModel = viewModel()
    val apKeyState = remember { mutableStateOf(TextFieldValue()) }
    val passwordState = remember { mutableStateOf(TextFieldValue()) }
    val scrollState = rememberScrollState()

    //For Robo Test
    if (loginViewModel.isTestingMode) {
        LaunchedEffect(Unit) {
            val testUsername = "TP062856" // Use a test username
            userViewModel.username = testUsername
            val route = NavRoutes.Home.createRoute(testUsername)
            navController.navigate(route) {
                popUpTo(NavRoutes.Login.route) { inclusive = true }
            }
        }
    } else {
        // Normal login screen UI and logic
        Box(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(scrollState)
        ) {

            Column(
                modifier = Modifier
                    .fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Top
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(color = APUBlue)
                        .padding(16.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "LOGIN",
                        color = Color.White,
                        fontSize = 24.sp

                    )
                }
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color.White)
                        .padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Top
                ) {
                    Spacer(modifier = Modifier.height(50.dp))

                    // App Logo
                    Image(
                        painter = painterResource(id = R.drawable.apulogo),
                        contentDescription = "Local Image",
                        modifier = Modifier.size(150.dp, 150.dp)
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // Construct the AnnotatedString
                    val annotatedText = buildAnnotatedString {
                        withStyle(style = SpanStyle(color = APUBlue)) {
                            append("AP")
                        }
                        withStyle(style = SpanStyle(color = Color.Gray)) {
                            append("BOOK")
                        }
                    }

                    Text(
                        text = annotatedText,
                        fontSize = 28.sp,
                        modifier = Modifier.padding(top = 16.dp, bottom = 8.dp)
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Sign in to continue",
                        fontSize = 16.sp,
                        color = Color.Black
                    )

                    Spacer(modifier = Modifier.height(24.dp))
                    Text(
                        text = "AP KEY",
                        fontSize = 12.sp,
                        color = Color.Black,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 0.dp),
                        textAlign = TextAlign.Start
                    )
                    // AP Key Input Field
                    OutlinedTextField(
                        value = apKeyState.value,
                        onValueChange = { apKeyState.value = it },
                        label = { Text("AP Key") },
                        modifier = Modifier.fillMaxWidth(),
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Text,
                            imeAction = ImeAction.Next
                        )
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Text(
                        text = "PASSWORD",
                        fontSize = 12.sp,
                        color = Color.Black,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 0.dp),
                        textAlign = TextAlign.Start
                    )
                    // Password Input Field
                    OutlinedTextField(
                        value = passwordState.value,
                        onValueChange = { passwordState.value = it },
                        label = { Text("Password") },
                        visualTransformation = PasswordVisualTransformation(),
                        modifier = Modifier.fillMaxWidth(),
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Text,
                            imeAction = ImeAction.Done
                        )

                    )

                    Spacer(modifier = Modifier.height(16.dp))
                    when (loginState) {
                        is LoginViewModel.LoginState.Success -> {
                            LaunchedEffect(Unit) {
                                val user = (loginState as LoginViewModel.LoginState.Success).user
                                userViewModel.username = user.username // Update UserViewModel
                                val route = NavRoutes.Home.createRoute(user.username)
                                navController.navigate(route) {
                                    popUpTo(NavRoutes.Login.route) { inclusive = true }
                                }
                            }
                        }

                        is LoginViewModel.LoginState.Failure -> {
                            Text(
                                (loginState as LoginViewModel.LoginState.Failure).message,
                                color = Color.Red
                            )
                            Spacer(modifier = Modifier.height(4.dp))

                            // Retry Button
                            Button(
                                onClick = {
                                    viewModel.login(apKeyState.value.text, passwordState.value.text)
                                },
                                modifier = Modifier
                                    .height(48.dp)
                                    .fillMaxWidth(),
                                // Customize button colors here
                                colors = ButtonDefaults.buttonColors(
                                    backgroundColor = APUBlue,
                                    contentColor = Color.White
                                )
                            ) {
                                Text(
                                    text = "RETRY LOGIN",
                                    fontSize = 16.sp
                                )
                            }
                        }

                        null -> {
                            // Login Button
                            Button(
                                onClick = {
                                    viewModel.login(apKeyState.value.text, passwordState.value.text)
                                },
                                modifier = Modifier
                                    .height(48.dp)
                                    .fillMaxWidth(),
                                colors = ButtonDefaults.buttonColors(
                                    backgroundColor = APUBlue,
                                    contentColor = Color.White
                                )
                            ) {
                                Text(
                                    text = "LOG IN",
                                    fontSize = 16.sp
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}