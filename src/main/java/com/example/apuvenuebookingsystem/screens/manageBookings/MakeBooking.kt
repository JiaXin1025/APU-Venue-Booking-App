package com.example.apuvenuebookingsystem.screens.manageBookings

import android.annotation.SuppressLint
import android.util.Log
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.MaterialTheme
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Scaffold
import androidx.compose.material.Snackbar
import androidx.compose.material.SnackbarHost
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.material.rememberScaffoldState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import coil.compose.AsyncImage
import com.example.apuvenuebookingsystem.ViewModel.BookingViewModel
import com.example.apuvenuebookingsystem.ViewModel.UserViewModel
import com.example.apuvenuebookingsystem.ui.theme.APUBlue
import kotlinx.coroutines.launch
import java.net.URLDecoder
import java.nio.charset.StandardCharsets
import java.time.LocalDate
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeParseException

@SuppressLint("UnusedMaterialScaffoldPaddingParameter")
@Composable
fun BookingFormScreen(
    userViewModel: UserViewModel,
    navController: NavHostController,
    venueId: String,
    imageUrl: String,
    venueName: String,
    bookingViewModel: BookingViewModel = viewModel()
) {
    Log.d(
        "BookingFormScreen",
        "Received venueId: $venueId, imageUrl: $imageUrl, venueName: $venueName, username: ${userViewModel.username}"
    )
    val decodedImageUrl = URLDecoder.decode(imageUrl, StandardCharsets.UTF_8.toString())

    var eventName by remember { mutableStateOf("") }
    var eventDate by remember { mutableStateOf("") }
    var eventStartTime by remember { mutableStateOf("") }
    var eventEndTime by remember { mutableStateOf("") }
    val scaffoldState = rememberScaffoldState()
    val coroutineScope = rememberCoroutineScope()
    var isButtonEnabled by remember { mutableStateOf(true) }

    fun validateAndBook() {
        try {
            val bookingDate = LocalDate.parse(eventDate, DateTimeFormatter.ISO_LOCAL_DATE)
            val bookingStartTime =
                LocalTime.parse(eventStartTime, DateTimeFormatter.ofPattern("HH:mm"))
            val currentDate = LocalDate.now()
            val currentTime = LocalTime.now()

            if (bookingDate.isBefore(currentDate) ||
                (bookingDate.isEqual(currentDate) && bookingStartTime.isBefore(currentTime))
            ) {
                coroutineScope.launch {
                    scaffoldState.snackbarHostState.showSnackbar("Booking date and time must be in the future.")
                    isButtonEnabled = true
                }
                return
            }

            // Call the createBooking function if the date and time are valid
            bookingViewModel.createBooking(
                userViewModel.username,
                venueId,
                eventName,
                eventDate,
                eventStartTime,
                eventEndTime
            ) { success, message ->
                coroutineScope.launch {
                    if (success) {
                        scaffoldState.snackbarHostState.showSnackbar(message)
                        navController.navigate("BookingHistory")
                    } else {
                        isButtonEnabled = true
                        if (message == "Booking slot is taken") {
                            scaffoldState.snackbarHostState.showSnackbar("Booking date and time for this slot is taken.")
                            navController.navigate("NewBooking")
                        } else {
                            scaffoldState.snackbarHostState.showSnackbar(message)
                        }
                    }
                }
            }
        } catch (e: DateTimeParseException) {
            coroutineScope.launch {
                scaffoldState.snackbarHostState.showSnackbar("Please enter a valid date and time.")
                isButtonEnabled = true
            }
        }
    }
    Scaffold(
        scaffoldState = scaffoldState,
        topBar = {
            TopAppBar(
                title = {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp),
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "V E N U E  B O O K I N G",
                            color = Color.White,
                            style = MaterialTheme.typography.subtitle1
                        )
                    }
                },
                backgroundColor = APUBlue,
                contentColor = Color.White,
                modifier = Modifier
                    .height(40.dp),
                elevation = 12.dp
            )
        },
        snackbarHost = { snackbarHostState ->
            SnackbarHost(hostState = snackbarHostState) { snackbarData ->
                // Custom layout for Snackbar
                Snackbar(
                    snackbarData = snackbarData,
                    modifier = Modifier.padding(bottom = 64.dp) // Adjust this value as needed
                )
            }
        },
        content = { innerPadding ->
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(
                        top = innerPadding.calculateTopPadding(),
                        bottom = innerPadding.calculateBottomPadding()
                    )
            ) {
                item {
                    AsyncImage(
                        model = decodedImageUrl,
                        contentDescription = "Venue Image",
                        modifier = Modifier
                            .fillMaxWidth() // Fill the width of the container
                            .height(200.dp)
                            .padding(top = 4.dp), // Set a fixed height
                        contentScale = ContentScale.Crop
                    )
                }
                item {
                    Text(
                        text = " $venueName",
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(8.dp),
                        style = MaterialTheme.typography.subtitle1,
                        textAlign = TextAlign.Center
                    )
                }

                item {
                    OutlinedTextField(
                        value = eventName,
                        onValueChange = { eventName = it },
                        label = { Text("Event Name") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 8.dp)
                    )
                }
                item {
                    OutlinedTextField(
                        value = eventDate,
                        onValueChange = { eventDate = it },
                        label = { Text("Date (YYYY-MM-DD)") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 8.dp),
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Text,
                            imeAction = ImeAction.Next
                        )
                    )
                }

                item {
                    OutlinedTextField(
                        value = eventStartTime,
                        onValueChange = { eventStartTime = it },
                        label = { Text("Start Time (HH:mm)") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 8.dp),
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Text,
                            imeAction = ImeAction.Next
                        )
                    )
                }

                item {
                    OutlinedTextField(
                        value = eventEndTime,
                        onValueChange = { eventEndTime = it },
                        label = { Text("End Time (HH:mm)") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 8.dp),
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Text,
                            imeAction = ImeAction.Done
                        )
                    )
                }

                item {
                    Button(
                        onClick = {
                            isButtonEnabled = false
                            validateAndBook() // Call the validate and book function when the button is clicked
                        },
                        enabled = isButtonEnabled,
                        colors = ButtonDefaults.buttonColors(backgroundColor = if (isButtonEnabled) APUBlue else Color.Gray),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp)
                    ) {
                        Text(
                            "Book Now",
                            color = Color.White
                        )
                    }
                }
            }
        }
    )
}
