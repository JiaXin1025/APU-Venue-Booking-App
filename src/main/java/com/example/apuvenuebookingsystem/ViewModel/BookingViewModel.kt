package com.example.apuvenuebookingsystem.ViewModel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.apuvenuebookingsystem.model.Bookings
import com.example.apuvenuebookingsystem.model.TimeSlot
import com.example.apuvenuebookingsystem.model.Venues
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.Locale
import java.util.concurrent.CountDownLatch

class BookingViewModel : ViewModel() {
    private val db = FirebaseFirestore.getInstance()

    fun createBooking(
        username: String,
        venueId: String,
        eventName: String,
        date: String,
        startTime: String,
        endTime: String,
        onResult: (Boolean, String) -> Unit
    ) {
        viewModelScope.launch {
            Log.d("BookingVM", "Creating booking for user: $username at venue: $venueId")

            val booking = Bookings(
                username = username,
                venueId = venueId,
                eventName = eventName,
                date = date,
                startTime = startTime,
                endTime = endTime
            )

            checkAvailabilityAndBook(booking) { success, message ->
                Log.d("BookingVM", "Booking result: Success: $success, Message: $message")
                onResult(success, message)
            }
        }
    }
    fun fetchBookingsWithVenueNames(
        username: String,
        onResult: (List<Pair<Bookings, String>>) -> Unit
    ) {
        fetchBookingsForUser(username) { bookings ->
            val venueIds = bookings.map { it.venueId }.distinct()
            fetchVenueNames(venueIds) { venueNames ->
                val bookingsWithVenueNames = bookings.map { booking ->
                    booking to (venueNames[booking.venueId] ?: "Unknown Venue")
                }
                onResult(bookingsWithVenueNames)
            }
        }
    }

    fun fetchVenueNames(venueIds: List<String>, onResult: (Map<String, String>) -> Unit) {
        val db = FirebaseFirestore.getInstance()
        val venueNames = mutableMapOf<String, String>()
        val countDownLatch = CountDownLatch(venueIds.size)

        venueIds.forEach { venueId ->
            db.collection("Venue")
                .document(venueId)
                .get()
                .addOnSuccessListener { documentSnapshot ->
                    val venueName = documentSnapshot.getString("venue") ?: "Unknown Venue"
                    venueNames[venueId] = venueName
                    countDownLatch.countDown()
                }
                .addOnFailureListener { e ->
                    Log.e("BookingVM", "Error fetching venue name: ${e.message}")
                    venueNames[venueId] = "Error Fetching Name"
                    countDownLatch.countDown()
                }
        }

        // Wait for all callbacks to complete
        viewModelScope.launch(Dispatchers.IO) {
            try {
                countDownLatch.await()
                withContext(Dispatchers.Main) {
                    onResult(venueNames)
                }
            } catch (e: InterruptedException) {
                Log.e("BookingVM", "fetchVenueNames interrupted: ${e.message}")
            }
        }
    }

    private fun checkAvailabilityAndBook(
        booking: Bookings,
        onResult: (Boolean, String) -> Unit
    ) {
        val venueRef = db.collection("Venue").document(booking.venueId)

        db.runTransaction { transaction ->
            val venueSnapshot = transaction.get(venueRef)
            val venue = venueSnapshot.toObject(Venues::class.java)
            val dateAvailability = venue?.availability?.get(booking.date) ?: emptyList()

            if (isTimeSlotAvailable(dateAvailability, booking.startTime, booking.endTime)) {
                val updatedAvailability = updateAvailability(
                    dateAvailability,
                    booking.startTime,
                    booking.endTime,
                    booking.date
                )
                transaction.update(venueRef, "availability.${booking.date}", updatedAvailability)
                val newBookingRef = db.collection("Booking").document()
                transaction.set(newBookingRef, booking)
                true
            } else {
                onResult(false, "Booking slot is taken") // Specific message for overlap
            }
        }.addOnSuccessListener {
            onResult(true, "Booking successful")
        }.addOnFailureListener { e ->
            onResult(false, "Booking failed: ${e.message}")
        }
    }

    private fun isTimeSlotAvailable(
        dateAvailability: List<TimeSlot>,
        startTime: String,
        endTime: String
    ): Boolean {
        val dateFormat = SimpleDateFormat("HH:mm", Locale.getDefault())
        val bookingStart = dateFormat.parse(startTime) ?: return false
        val bookingEnd = dateFormat.parse(endTime) ?: return false

        return dateAvailability.none { slot ->
            val slotStart = dateFormat.parse(slot.startTime) ?: return@none false
            val slotEnd = dateFormat.parse(slot.endTime) ?: return@none false

            bookingStart.before(slotEnd) && bookingEnd.after(slotStart)
        }
    }

    private fun updateAvailability(
        dateAvailability: List<TimeSlot>,
        startTime: String,
        endTime: String,
        date: String
    ): List<TimeSlot> {
        val updatedList = ArrayList(dateAvailability)
        updatedList.add(TimeSlot(startTime, endTime))
        return updatedList
    }

    fun fetchBookingsForUser(username: String, onResult: (List<Bookings>) -> Unit) {
        db.collection("Booking")
            .whereEqualTo("username", username)
            .get()
            .addOnSuccessListener { querySnapshot ->
                val bookingsList = querySnapshot.toObjects(Bookings::class.java)
                onResult(bookingsList)
            }
            .addOnFailureListener { e ->
                Log.e("BookingVM", "Error fetching bookings: ${e.message}")
                onResult(emptyList()) // or handle the error as needed
            }
    }
}


