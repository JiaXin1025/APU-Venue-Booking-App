package com.example.apuvenuebookingsystem.model

data class Users(
    val fullname: String = "", // Default values are necessary for Firebase
    val password: String = "",
    var username: String = ""
)
