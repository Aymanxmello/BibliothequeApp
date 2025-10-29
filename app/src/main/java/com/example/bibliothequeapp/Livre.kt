package com.example.bibliothequeapp

data class Livre(
    val titre: String,
    val prix: Double,
    val imageUrl: String,
    var disponible: Boolean
)