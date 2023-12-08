package com.example.apuvenuebookingsystem.screens.manageBookings

import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Card
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import coil.compose.rememberAsyncImagePainter
import com.example.apuvenuebookingsystem.NavigationBar.BottomNavigationBar
import com.example.apuvenuebookingsystem.ViewModel.UserViewModel
import com.example.apuvenuebookingsystem.model.Venues
import com.example.apuvenuebookingsystem.ui.theme.APUBlue
import com.example.apuvenuebookingsystem.ui.theme.LightBlue
import com.google.firebase.Firebase
import com.google.firebase.firestore.firestore
import java.net.URLEncoder
import java.nio.charset.StandardCharsets

@Composable
fun VenueListScreen(navController: NavHostController, userViewModel: UserViewModel) {
    val username = userViewModel.username
    val annotatedText = buildAnnotatedString {
        withStyle(style = SpanStyle(LightBlue)) {
            append("AP")
        }
        withStyle(style = SpanStyle(color = Color.White)) { // Assuming 0xFF1344A8 is your blue color
            append("BOOK")
        }
    }
    Scaffold(
        topBar = {
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
                            text = "V E N U E S",
                            fontSize = 18.sp,
                            color = Color.White,
                            textAlign = TextAlign.Center,
                            maxLines = 1
                        )
                    }
                },
                backgroundColor = APUBlue,
                contentColor = Color.White,
                modifier = Modifier.height(100.dp) // Set the TopAppBar height
            )
        },
        bottomBar = {
            BottomNavigationBar(navController = navController,userViewModel)
        }
    ) { innerPadding ->
        // Pass innerPadding to the content to ensure it respects the space taken by bars
        VenueListContent(innerPadding, navController, username)
    }
}

fun fetchVenues(onResult: (List<Venues>) -> Unit) {
    val db = Firebase.firestore
    db.collection("Venue")
        .get()
        .addOnSuccessListener { documents ->
            val venues = documents.map { document ->
                val venue = document.toObject(Venues::class.java)
                venue.copy(venueId = document.id) // Set the document ID
            }
            Log.d("FirestoreDebug", "Venues: $venues")
            onResult(venues)
        }
        .addOnFailureListener { exception ->
            Log.e("FirestoreDebug", "Error fetching venues", exception)
        }
}

@Composable
fun VenueListContent(paddingValues: PaddingValues, navController: NavHostController, username: String) {
    val venues = remember { mutableStateListOf<Venues>() }
    LocalContext.current

    // When the composable enters the composition
    LaunchedEffect(key1 = Unit) {
        fetchVenues { fetchedVenues ->
            venues.clear()
            venues.addAll(fetchedVenues)
        }
    }

    if (venues.isEmpty()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            CircularProgressIndicator()
        }
    } else {
        LazyColumn(contentPadding = paddingValues) {
            items(venues) { venue ->
            VenueCard(venue) {
                val encodedImageUrl = URLEncoder.encode(venue.imageUrl, StandardCharsets.UTF_8.toString())
                val route = "bookingForm/${venue.venueId}/$encodedImageUrl/${venue.venue}/${username}"
                navController.navigate(route)
                }
            }
        }
    }
}

@Composable
fun VenueCard(venue: Venues, onBookClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        elevation = 4.dp,
        shape = RoundedCornerShape(16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Image(
                painter = rememberAsyncImagePainter(venue.imageUrl),
                contentDescription = "Venue Image",
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .height(220.dp)
                    .width(150.dp)
            )

            Spacer(modifier = Modifier.width(2.dp)) // Add spacing between the image and content

            Column(
                modifier = Modifier.padding(16.dp) // Add padding to the content
            ) {
                Text(text = venue.venue, style = MaterialTheme.typography.h6)
                Spacer(modifier = Modifier.height(8.dp))
                Text(text = "Location: ${venue.location}")
                Spacer(modifier = Modifier.height(2.dp))
                Text(text = "Capacity: ${venue.capacity}")
                Spacer(modifier = Modifier.height(2.dp))
                Text(text = "Type: ${venue.type}")
                Spacer(modifier = Modifier.height(2.dp))
                // Display facilities as a comma-separated list
                Text(
                    text = "Facilities: ${venue.facilities.joinToString(", ")}",
                    maxLines = 2, // Adjust as needed
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(modifier = Modifier.height(8.dp))
                Button(
                    onClick = {
                        onBookClick()
                    },
                    colors = ButtonDefaults.buttonColors(
                        backgroundColor = APUBlue,
                        contentColor = Color.White
                    ),
                    modifier = Modifier.padding(start = 105.dp)
                ) {
                    Text("Book")
                }
            }
        }
    }
}


