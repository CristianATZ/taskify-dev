package com.devtorres.taskalarm.ui.navigation

sealed class Destinations(val route: String) {
    object Home : Destinations("home")
    object Settings : Destinations("settings")

    // agregar mas si es necesario (no creo)
}