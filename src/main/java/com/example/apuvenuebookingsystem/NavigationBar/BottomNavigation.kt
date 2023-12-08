package com.example.apuvenuebookingsystem.NavigationBar

import ProfileScreen
import android.annotation.SuppressLint
import androidx.compose.material.Icon
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.apuvenuebookingsystem.ViewModel.UserViewModel
import com.example.apuvenuebookingsystem.screens.Home
import com.example.apuvenuebookingsystem.screens.authentication.LoginScreen
import com.example.apuvenuebookingsystem.screens.manageBookings.BookingFormScreen
import com.example.apuvenuebookingsystem.screens.manageBookings.MyBookingsScreen
import com.example.apuvenuebookingsystem.screens.manageBookings.VenueListScreen

@SuppressLint("UnusedMaterialScaffoldPaddingParameter")
@Composable
fun MainScreen(userViewModel: UserViewModel) {
    val navController = rememberNavController() // Correctly instantiated here for the entire app's navigation
    Scaffold(
        bottomBar = { BottomNavigationBar(navController, userViewModel) }
    ) {
        NavigationHost(navController, userViewModel)
    }
}


@Composable
fun NavigationHost(navController: NavHostController, userViewModel: UserViewModel) {
    NavHost(navController = navController, startDestination = NavRoutes.Login.route) {
        composable(NavRoutes.Login.route) {
            LoginScreen(navController, userViewModel = userViewModel)
        }
        composable(NavRoutes.Home.route) {
            Home(userViewModel)
        }
        composable(NavRoutes.Bookings.route) {
            MyBookingsScreen(userViewModel, bookingViewModel = viewModel())
        }
        composable(NavRoutes.NewBooking.route) {
            VenueListScreen(navController, userViewModel)
        }
        composable(
            route = "bookingForm/{venueId}/{imageUrl}/{venueName}/{username}",
            arguments = listOf(
                navArgument("venueId") { type = NavType.StringType },
                navArgument("imageUrl") { type = NavType.StringType },
                navArgument("venueName") { type = NavType.StringType },
                navArgument("username") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            BookingFormScreen(
                userViewModel = userViewModel,
                navController = navController,
                venueId = backStackEntry.arguments?.getString("venueId") ?: "",
                imageUrl = backStackEntry.arguments?.getString("imageUrl") ?: "",
                venueName = backStackEntry.arguments?.getString("venueName") ?: ""
            )
        }
        composable(NavRoutes.Inbox.route) {
            // Your Inbox Screen
        }
        composable(NavRoutes.Profile.route) {
            ProfileScreen(navController, userViewModel)
        }
    }
}

@Composable
fun BottomNavigationBar(navController: NavHostController, userViewModel: UserViewModel) {
    val currentRoute = navController.currentBackStackEntryAsState().value?.destination?.route
    val username = userViewModel.username

    if (currentRoute != NavRoutes.Login.route) {
        NavigationBar {
            NavBarItems.BarItems.forEach { navItem ->
                val isSelected = currentRoute == navItem.route
                val iconColor = if (isSelected) Color.Blue else Color.Gray // Define iconColor here

                NavigationBarItem(
                    selected = isSelected,
                    onClick = {
                        if (navItem.route == NavRoutes.Home.routeWithoutArgs) {
                            val homeRouteWithUsername = NavRoutes.Home.createRoute(username)
                            navController.navigate(homeRouteWithUsername) {
                                popUpTo(navController.graph.findStartDestination().id) {
                                    saveState = true
                                }
                                launchSingleTop = true
                                restoreState = true
                            }
                        } else {
                            navController.navigate(navItem.route) {
                                popUpTo(navController.graph.findStartDestination().id) {
                                    saveState = true
                                }
                                launchSingleTop = true
                                restoreState = true
                            }
                        }
                    },
                    icon = {
                        Icon(
                            imageVector = navItem.image,
                            contentDescription = navItem.title,
                            tint = iconColor // Apply the icon color
                        )
                    },
                    label = {
                        Text(text = navItem.title, color = iconColor) // Apply the label color
                    }
                )
            }
        }
    }
}
