package com.example.apuvenuebookingsystem.Data

import com.example.apuvenuebookingsystem.model.Affirmation
import com.example.apuvenuebookingsystem.R

class HomeCategory() {
    fun loadCategories(): List<Affirmation> {
        return listOf<Affirmation>(
            Affirmation(R.string.Category1, R.drawable.audi),
            Affirmation(R.string.Category2, R.drawable.classroom1),
            Affirmation(R.string.Category3, R.drawable.computer_lab),
            Affirmation(R.string.Category4, R.drawable.meeting),
            Affirmation(R.string.Category5,R.drawable.atrium ),
            Affirmation(R.string.Category6, R.drawable.booth),
            Affirmation(R.string.Category7, R.drawable.sports))
    }
}