package com.devtorres.taskalarm.ui.navigation

sealed class Destinations(val route: String) {
    data object Home : Destinations("home")
    data object Settings : Destinations("settings")

    // agregar mas si es necesario (no creo)
}