package com.example.apuvenuebookingsystem.model

data class Bookings(
    var username: String = "",
    var venueId: String = "",
    var eventName: String = "",
    var date: String = "",
    var startTime: String = "",
    var endTime: String = "",
    var bookingStatus: String = "Ongoing"
)