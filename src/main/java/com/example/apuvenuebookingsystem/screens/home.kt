package com.example.apuvenuebookingsystem.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.apuvenuebookingsystem.Data.HomeCategory
import com.example.apuvenuebookingsystem.ViewModel.BookingViewModel
import com.example.apuvenuebookingsystem.ViewModel.UserViewModel
import com.example.apuvenuebookingsystem.model.Affirmation
import com.example.apuvenuebookingsystem.model.Bookings
import com.example.apuvenuebookingsystem.screens.manageBookings.BookingCard
import com.example.apuvenuebookingsystem.ui.theme.APUBlue
import com.example.apuvenuebookingsystem.ui.theme.LightBlue

@Composable
fun Home(userViewModel: UserViewModel, bookingViewModel: BookingViewModel = viewModel()) {
    val username = userViewModel.username
    val annotatedText = buildAnnotatedString {
        withStyle(style = SpanStyle(LightBlue)) {
            append("AP")
        }
        withStyle(style = SpanStyle(color = Color.White)) { // Assuming 0xFF1344A8 is your blue color
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
                        textAlign = TextAlign.Center, // Center the text
                        color = Color.White,
                        maxLines = 1
                    )
                    Spacer(modifier = Modifier.height(8.dp)) // Adjust spacer height as needed
                    Text(
                        text = "H O M E",
                        fontSize = 18.sp, // Adjust font size as needed
                        color = Color.White,
                        textAlign = TextAlign.Center, // Center the text
                        maxLines = 1
                    )
                }
            },
            backgroundColor = APUBlue,
            contentColor = Color.White,
            modifier = Modifier.height(100.dp) // Set the TopAppBar height
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "Welcome, $username", // Display the username
            fontSize = 18.sp,
            color = Color.Gray,
            fontWeight = FontWeight.Bold, // Set the text to be bold
            modifier = Modifier.padding(start = 16.dp, bottom = 4.dp)
        )
        Spacer(modifier = Modifier.height(4.dp))
        CategoriesList()
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "My Upcoming Bookings",
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold, // Set the text to be bold
            color = Color.Gray,
            modifier = Modifier
                .padding(start = 16.dp, bottom = 4.dp)
        )

        val ongoingBookings = remember { mutableStateListOf<Pair<Bookings, String>>() }
        val isLoadingOngoingBookings = remember { mutableStateOf(true) }
        val errorMessageOngoingBookings = remember { mutableStateOf<String?>(null) }

        LaunchedEffect(key1 = userViewModel.username) {
            try {
                // Fetch ongoing bookings for the logged-in user
                bookingViewModel.fetchBookingsForUser(userViewModel.username) { bookings ->
                    val ongoingBookingsList = bookings.filter { it.bookingStatus == "Ongoing" }
                    val venueIds = ongoingBookingsList.map { it.venueId }.distinct()

                    bookingViewModel.fetchVenueNames(venueIds) { venueNames ->
                        ongoingBookings.clear()
                        ongoingBookings.addAll(ongoingBookingsList.map { booking ->
                            booking to (venueNames[booking.venueId] ?: "Unknown Venue")
                        })
                    }
                }
            } catch (e: Exception) {
                errorMessageOngoingBookings.value = "Error fetching ongoing bookings: ${e.message}"
            } finally {
                isLoadingOngoingBookings.value = false
            }
        }

        // Display ongoing bookings
        when {
            isLoadingOngoingBookings.value -> {
                CircularProgressIndicator(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp)
                )
            }

            errorMessageOngoingBookings.value != null -> {
                Text(errorMessageOngoingBookings.value!!)
            }

            else -> {
                // LazyColumn with ongoing bookings
                LazyColumn(
                    contentPadding = PaddingValues(bottom = bottomNavigationBarHeight) // Add padding at the bottom
                ) {
                    items(ongoingBookings) { (booking, venueName) ->
                        BookingCard(booking, venueName)
                    }
                    item {
                        Spacer(modifier = Modifier.height(bottomNavigationBarHeight)) // Adds space at the end of the list
                    }
                }
            }
        }
    }
}

@Composable
fun CategoryItem(affirmation: Affirmation) {
    Card(
        backgroundColor = Color.Transparent,
        elevation = 0.dp,
        modifier = Modifier
            .padding(8.dp)
            .size(100.dp) // Adjust the size of the card if necessary
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier.fillMaxSize() // Ensure the Column fills the Card
        ) {
            Box(
                modifier = Modifier
                    .size(80.dp) // The size of the circle clip
                    .padding(2.dp) // Add padding for the border, adjust as needed
                    .clip(CircleShape) // Clips the Box content to a circle
                    .border(
                        border = BorderStroke(1.dp, Color.Gray), // Set the border stroke and color
                        shape = CircleShape
                    ),
                contentAlignment = Alignment.Center // Centers the content of the Box
            ) {
                Image(
                    painter = painterResource(id = affirmation.imageResourceId),
                    contentDescription = stringResource(id = affirmation.stringResourceId),
                    modifier = Modifier
                        .size(30.dp) // Smaller size of the image itself
                )
            }
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = stringResource(id = affirmation.stringResourceId),
                fontSize= 12.sp,
                textAlign = TextAlign.Center // Center the text horizontally
            )
        }
    }
}
@Composable
fun CategoriesList() {
    val categories = HomeCategory().loadCategories()
    Text(
        text = "Categories",
        fontSize = 20.sp,
        fontWeight = FontWeight.Bold, // Set the text to be bold
        color = Color.Gray,
        modifier = Modifier
            .padding(top=8.dp, bottom = 4.dp, start = 16.dp, end= 8.dp)
    )
    LazyRow(
        horizontalArrangement = Arrangement.spacedBy(-20.dp), // Reduce space between items
        contentPadding = PaddingValues(horizontal = 0.dp, vertical = 8.dp) // You can adjust this as needed
    ) {
        items(categories.size) { index ->
            CategoryItem(categories[index])
        }
    }
}

@Preview(showBackground = true)
@Composable
fun HomePreview() {
    //Home()
}
