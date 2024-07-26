package com.devtorres.taskalarm.ui.navigation

import androidx.navigation.ActivityNavigator

sealed class Destinations(val route: String) {
    object Home : Destinations("home")
    object Theme : Destinations("theme")

    // agregar mas si es necesario (no creo)
}