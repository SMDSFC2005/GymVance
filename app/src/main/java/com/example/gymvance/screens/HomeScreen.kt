package com.example.gymvance.screens

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController

@Composable
fun HomeScreen(navController: NavController) {
    Box(
        modifier = Modifier.Companion
            .fillMaxSize()
            .padding(16.dp),
        contentAlignment = Alignment.Companion.Center
    ) {
        Text(
            text = "¡Hola Mundo!",
            style = MaterialTheme.typography.headlineLarge
        )
    }
}