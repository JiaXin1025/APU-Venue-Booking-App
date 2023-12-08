package com.example.apuvenuebookingsystem.screens.manageBookings

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.apuvenuebookingsystem.ViewModel.BookingViewModel
import com.example.apuvenuebookingsystem.ViewModel.UserViewModel
import com.example.apuvenuebookingsystem.model.Bookings
import com.example.apuvenuebookingsystem.ui.theme.APUBlue
import com.example.apuvenuebookingsystem.ui.theme.LightBlue

@Composable
fun MyBookingsScreen(userViewModel: UserViewModel, bookingViewModel: BookingViewModel = viewModel()) {
    val bookingsWithVenueNames = remember { mutableStateListOf<Pair<Bookings, String>>() }
    val isLoading = remember { mutableStateOf(true) }
    val errorMessage = remember { mutableStateOf<String?>(null) }

    LaunchedEffect(key1 = userViewModel.username) {
        try {
            bookingViewModel.fetchBookingsWithVenueNames(userViewModel.username) { fetchedBookings ->
                // Use the onResult callback to handle the fetched bookings
                bookingsWithVenueNames.clear()
                bookingsWithVenueNames.addAll(fetchedBookings)
            }
        } catch (e: Exception) {
            errorMessage.value = "Error fetching bookings: ${e.message}"
        } finally {
            isLoading.value = false
        }
    }

    val annotatedText = buildAnnotatedString {
        withStyle(style = SpanStyle(LightBlue)) {
            append("AP")
        }
        withStyle(style = SpanStyle(color = Color.White)) {
            append("BOOK")
        }
    }
    val bottomNavigationBarHeight = 40.dp

    Column(modifier = Modifier.fillMaxSize()) {
        TopAppBar(
            title = {
                Column(
                    modifier = Modifier
                        .fillMaxHeight()
                        .fillMaxWidth(),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = annotatedText,
                        fontSize = 36.sp,
                        textAlign = TextAlign.Center,
                        color = Color.White,
                        maxLines = 1
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "M Y  B O O K I N G S ",
                        fontSize = 18.sp,
                        color = Color.White,
                        textAlign = TextAlign.Center,
                        maxLines = 1
                    )
                }
            },
            backgroundColor = APUBlue,
            contentColor = Color.White,
            modifier = Modifier.height(100.dp)
        )
        Spacer(modifier = Modifier.height(8.dp))

        when {
            isLoading.value -> {
                CircularProgressIndicator(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp)
                )
            }
            errorMessage.value != null -> {
                Text(errorMessage.value!!)
            }
            else -> {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(bottom = bottomNavigationBarHeight) // Add padding at the bottom
                ) {
                    items(bookingsWithVenueNames) { (booking, venueName) ->
                        BookingCard(booking, venueName)
                    }
                    item {
                        // This adds a space after the last card equivalent to the bottom navigation bar's height.
                        Spacer(modifier = Modifier.height(bottomNavigationBarHeight))
                    }
                }
            }
        }
    }
}

@Composable
fun BookingCard(booking: Bookings, venueName: String) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        shape = RoundedCornerShape(8.dp),
        elevation = 4.dp
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text("${booking.eventName}", fontWeight = FontWeight.Bold)
            Text("Venue: ${venueName}")
            Text("Date: ${booking.date}")
            Text("Time: ${booking.startTime} - ${booking.endTime}")
            Text("Status: ${booking.bookingStatus}")
        }
    }
}
