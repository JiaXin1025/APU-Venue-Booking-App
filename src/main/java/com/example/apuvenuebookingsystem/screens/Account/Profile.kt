import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.MaterialTheme.typography
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Alignment.Companion.CenterHorizontally
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.apuvenuebookingsystem.ViewModel.UserViewModel
import com.example.apuvenuebookingsystem.ui.theme.APUBlue
import com.example.apuvenuebookingsystem.ui.theme.LightBlue
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

@Composable
fun ProfileScreen(navController: NavController, userViewModel: UserViewModel) {
    LocalContext.current

    var displayName by remember { mutableStateOf("") }
    var username by remember { mutableStateOf("") }
    var fullName by remember { mutableStateOf("") }
    val annotatedText = buildAnnotatedString {
        withStyle(style = SpanStyle(LightBlue)) {
            append("AP")
        }
        withStyle(style = SpanStyle(color = Color.White)) { // Assuming 0xFF1344A8 is your blue color
            append("BOOK")
        }
    }

    // Fetch the username from UserViewModel
    username = userViewModel.username

    // Retrieve user information from Firestore
    LaunchedEffect(key1 = username) {
        val userDoc = FirebaseFirestore.getInstance().collection("Users").document(username)

        try {
            val userData = withContext(Dispatchers.IO) {
                userDoc.get().await()
            }
            displayName = userData.getString("fullname") ?: ""

            fullName = userViewModel.getFullNameByUsername(username) ?: ""
        } catch (e: Exception) {
            // Handle any errors
        }
    }

    // Profile Screen UI
    Surface(
        modifier = Modifier.fillMaxSize(),
        color = Color.White
    ) {
        Column(
            modifier = Modifier.fillMaxSize()
        ) {
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
                            text = "P R O F I L E",
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

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "Name: $fullName", // Display the full name
                style = typography.h6,
                modifier = Modifier
                    .padding(start=16.dp)
            )
            Text(
                text = "APKey: $username",
                style = typography.h6,
                modifier = Modifier
                    .padding(start=16.dp)

            )

            Spacer(modifier = Modifier.height(40.dp))

            Button(
                onClick = {
                    navController.navigate("login") // Navigate to the login page
                },
                modifier = Modifier
                    .height(48.dp)
                    .width(200.dp)
                    .align(CenterHorizontally),
                // Customize button colors here
                colors = ButtonDefaults.buttonColors(
                    backgroundColor = APUBlue, // Replace with your desired color
                    contentColor = Color.White // This is the color of the text inside the button
                )
            ) {
                Text(
                    text = "LOG OUT",
                    fontSize = 16.sp
                )
            }
        }
    }
}