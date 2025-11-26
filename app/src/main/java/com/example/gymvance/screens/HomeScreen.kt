package com.example.gymvance.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.text.font.FontWeight
import androidx.navigation.NavController
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

@Composable
fun HomeScreen(navController: NavController) {

    val auth = FirebaseAuth.getInstance()
    val firestore = FirebaseFirestore.getInstance()

    var username by remember { mutableStateOf<String?>(null) }

    // Obtener el username
    LaunchedEffect(Unit) {
        val uid = auth.currentUser?.uid
        if (uid != null) {
            firestore.collection("users").document(uid).get()
                .addOnSuccessListener { doc ->
                    username = doc.getString("username") ?: "Usuario"
                }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(20.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Top
    ) {

        Spacer(modifier = Modifier.height(20.dp))

        // Bienvenida
        Text(
            text = "Hola, ${username ?: "..."}",
            style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold)
        )

        Spacer(modifier = Modifier.height(40.dp))


        // Tarjeta: MIS RUTINAS
        HomeCard(
            text = "Mis Rutinas",
            onClick = { navController.navigate("routines") }
        )

        Spacer(modifier = Modifier.height(20.dp))

        // Tarjeta: PROGRESO
        HomeCard(
            text = "Progreso Mensual",
            onClick = { navController.navigate("progress") }
        )

        Spacer(modifier = Modifier.height(20.dp))

        // Tarjeta: PERFIL
        HomeCard(
            text = "Perfil",
            onClick = { navController.navigate("profile") }
        )
        Spacer(modifier = Modifier.height(20.dp))

        Button(
            onClick = {
                auth.signOut()
                navController.navigate("login") {
                }
            },
        ) {
            Text("Cerrar sesión")
        }

    }
}

@Composable
fun HomeCard(text: String, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(90.dp),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        ),
        onClick = onClick
    ) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = text,
                style = MaterialTheme.typography.titleLarge.copy(
                    fontWeight = FontWeight.SemiBold
                )
            )
        }
    }
}
