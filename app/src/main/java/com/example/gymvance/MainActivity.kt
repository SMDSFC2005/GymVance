// MainActivity.kt
package com.example.gymvance

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.navigation.compose.rememberNavController
import com.example.gymvance.ui.theme.GymVanceTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            GymVanceTheme {
                val navController = rememberNavController()
                AppNavigation(navController = navController)
            }
        }
    }
}