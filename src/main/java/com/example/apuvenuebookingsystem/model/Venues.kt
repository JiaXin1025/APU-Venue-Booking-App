package com.example.apuvenuebookingsystem.model

data class Venues(
    val venueId: String = "", // This will store the document ID
    val venue: String = "", // Provide default values
    val location: String = "",
    val capacity: String = "",
    val type: String = "",
    val facilities: List<String> = listOf(),
    val availability: Map<String, List<TimeSlot>> = mapOf(), // Assuming TimeSlot is another data class
    val imageUrl: String = ""
)

