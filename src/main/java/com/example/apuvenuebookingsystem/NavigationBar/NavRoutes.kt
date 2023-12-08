package com.example.apuvenuebookingsystem.NavigationBar

sealed class NavRoutes(val route: String) {
    object Login : NavRoutes("Login")
    object Home : NavRoutes("Home/{username}") {
        fun createRoute(username: String) = "Home/$username"
        val routeWithoutArgs = "Home" // This can be used for navigation without arguments
    }
    object Bookings : NavRoutes("BookingHistory")
    object NewBooking : NavRoutes("NewBooking")
    object Inbox : NavRoutes("Inbox")
    object Profile : NavRoutes("Profile")
}

