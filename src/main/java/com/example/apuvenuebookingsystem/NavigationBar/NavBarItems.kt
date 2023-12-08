package com.example.apuvenuebookingsystem.NavigationBar

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.AddCircleOutline
import androidx.compose.material.icons.rounded.EventAvailable
import androidx.compose.material.icons.rounded.Home
import androidx.compose.material.icons.rounded.Inbox
import androidx.compose.material.icons.rounded.Person

object NavBarItems {
    var BarItems = listOf(
        BarItem(
            title = "Home",
            image = Icons.Rounded.Home,
            route = NavRoutes.Home.routeWithoutArgs // route without the username argument
        ),
        BarItem(
            title = "Bookings",
            image = Icons.Rounded.EventAvailable, //Replace with own icon
            route = "BookingHistory"
        ),
        BarItem(
            title = "Book",
            image = Icons.Rounded.AddCircleOutline, //Replace with own icon
            route = "NewBooking"
        ),
        BarItem(
            title = "Inbox",
            image = Icons.Rounded.Inbox,//Replace with own icon
            route = "Inbox"
        ),
        BarItem(
            title = "Profile",
            image = Icons.Rounded.Person, //Replace with own icon
            route = "Profile"
        )

    )

}