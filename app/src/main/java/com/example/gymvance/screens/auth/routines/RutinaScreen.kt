package com.example.gymvance.screens.routines

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

data class Exercise(
    val id: String = "",
    val name: String = "",
    val muscle: String = ""
)

@Composable
fun RutinaScreen(navController: NavController, routineId: String) {

    val auth = FirebaseAuth.getInstance()
    val firestore = FirebaseFirestore.getInstance()
    val uid = auth.currentUser!!.uid

    var routineDay by remember { mutableStateOf("") }
    var routineName by remember { mutableStateOf("") }
    var exercises by remember { mutableStateOf(listOf<Exercise>()) }

    var showModal by remember { mutableStateOf(false) } // ← modal predeterminados

    // Cargar datos de la rutina
    LaunchedEffect(routineId) {
        firestore.collection("users")
            .document(uid)
            .collection("routines")
            .document(routineId)
            .addSnapshotListener { snapshot, _ ->
                if (snapshot != null) {
                    routineDay = snapshot.getString("day") ?: ""
                    routineName = snapshot.getString("name") ?: ""
                }
            }

        // Cargar ejercicios de la rutina
        firestore.collection("users")
            .document(uid)
            .collection("routines")
            .document(routineId)
            .collection("exercises")
            .addSnapshotListener { snapshot, _ ->
                if (snapshot != null) {
                    exercises = snapshot.documents.map { doc ->
                        Exercise(
                            id = doc.id,
                            name = doc.getString("name") ?: "",
                            muscle = doc.getString("muscle") ?: ""
                        )
                    }
                }
            }
    }

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(onClick = { showModal = true }) {
                Text("+")
            }
        }
    ) { padding ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
        ) {

            // Título de la rutina
            Text(
                text = "$routineDay – $routineName",
                style = MaterialTheme.typography.headlineMedium
            )

            Spacer(modifier = Modifier.height(20.dp))

            if (exercises.isEmpty()) {
                Text("No hay ejercicios en esta rutina todavía.")
            } else {
                LazyColumn {
                    items(exercises) { exercise ->
                        ExerciseItem(exercise)
                        Spacer(Modifier.height(12.dp))
                    }
                }
            }
        }
    }

    // MODAL DE EJERCICIOS PREDETERMINADOS (vacío por ahora)
    if (showModal) {
        AlertDialog(
            onDismissRequest = { showModal = false },
            title = { Text("Añadir ejercicio") },
            text = { Text("Aquí irá la lista de ejercicios predeterminados.") },
            confirmButton = {
                TextButton(onClick = { showModal = false }) {
                    Text("Cerrar")
                }
            }
        )
    }
}

@Composable
fun ExerciseItem(exercise: Exercise) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(MaterialTheme.colorScheme.surfaceVariant),
        elevation = CardDefaults.cardElevation(defaultElevation = 3.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = exercise.name,
                style = MaterialTheme.typography.titleMedium
            )
            if (exercise.muscle.isNotBlank()) {
                Spacer(Modifier.height(4.dp))
                Text(
                    text = exercise.muscle,
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }
    }
}
