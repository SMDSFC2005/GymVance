package com.example.gymvance.screens.routines

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

data class Routine(
    val id: String = "",
    val day: String = "",
    val name: String = ""
)

@Composable
fun RutinasScreen(navController: NavController) {

    val auth = FirebaseAuth.getInstance()
    val firestore = FirebaseFirestore.getInstance()
    val uid = auth.currentUser!!.uid

    var routines by remember { mutableStateOf(listOf<Routine>()) }

    // Estados para el diálogo de crear rutina
    var showDialog by remember { mutableStateOf(false) }
    var newRoutineName by remember { mutableStateOf("") }
    var selectedDay by remember { mutableStateOf("Lunes") }
    var dayMenuExpanded by remember { mutableStateOf(false) }

    // Estados para el diálogo de editar rutina
    var showEditDialog by remember { mutableStateOf(false) }
    var routineToEdit by remember { mutableStateOf<Routine?>(null) }
    var editRoutineName by remember { mutableStateOf("") }
    var editRoutineDay by remember { mutableStateOf("Lunes") }
    var editDayMenuExpanded by remember { mutableStateOf(false) }

    val daysOfWeek = listOf(
        "Lunes", "Martes", "Miércoles",
        "Jueves", "Viernes", "Sábado", "Domingo"
    )

    // Cargar rutinas del usuario en tiempo real
    LaunchedEffect(Unit) {
        firestore.collection("users")
            .document(uid)
            .collection("routines")
            .addSnapshotListener { snapshot, _ ->
                if (snapshot != null) {
                    routines = snapshot.documents.map { doc ->
                        Routine(
                            id = doc.id,
                            day = doc.getString("day") ?: "",
                            name = doc.getString("name") ?: ""
                        )
                    }
                }
            }
    }

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(onClick = { showDialog = true }) {
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
            Text(
                text = "Mis Rutinas",
                style = MaterialTheme.typography.headlineMedium
            )

            Spacer(modifier = Modifier.height(20.dp))

            if (routines.isEmpty()) {
                Text("No has creado ninguna rutina aún.")
            } else {
                LazyColumn {
                    items(routines) { routine ->
                        RoutineItem(
                            routine = routine,
                            onClick = {
                                navController.navigate("routine/${routine.id}")
                            },
                            onEdit = {
                                routineToEdit = routine
                                editRoutineName = routine.name
                                editRoutineDay = routine.day
                                showEditDialog = true
                            },
                            onDelete = {
                                firestore.collection("users")
                                    .document(uid)
                                    .collection("routines")
                                    .document(routine.id)
                                    .delete()
                            }
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                    }
                }
            }
        }
    }

    // DIALOG PARA CREAR RUTINA
    if (showDialog) {
        AlertDialog(
            onDismissRequest = { showDialog = false },
            title = { Text("Nueva rutina") },
            text = {
                Column {

                    // Selector de día
                    Text("Día de la semana")
                    Spacer(Modifier.height(4.dp))
                    OutlinedButton(
                        onClick = { dayMenuExpanded = true },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(selectedDay)
                    }

                    DropdownMenu(
                        expanded = dayMenuExpanded,
                        onDismissRequest = { dayMenuExpanded = false }
                    ) {
                        daysOfWeek.forEach { day ->
                            DropdownMenuItem(
                                text = { Text(day) },
                                onClick = {
                                    selectedDay = day
                                    dayMenuExpanded = false
                                }
                            )
                        }
                    }

                    Spacer(Modifier.height(12.dp))

                    // Nombre de la rutina
                    OutlinedTextField(
                        value = newRoutineName,
                        onValueChange = { newRoutineName = it },
                        label = { Text("Nombre de rutina (ej: Pecho / Hombro)") },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (newRoutineName.isNotBlank()) {
                            val data = mapOf(
                                "day" to selectedDay,
                                "name" to newRoutineName.trim()
                            )

                            firestore.collection("users")
                                .document(uid)
                                .collection("routines")
                                .add(data)

                            newRoutineName = ""
                            selectedDay = "Lunes"
                            showDialog = false
                        }
                    }
                ) {
                    Text("Crear")
                }
            },
            dismissButton = {
                TextButton(onClick = { showDialog = false }) {
                    Text("Cancelar")
                }
            }
        )
    }

    // DIALOG PARA EDITAR RUTINA
    if (showEditDialog && routineToEdit != null) {
        AlertDialog(
            onDismissRequest = { showEditDialog = false },
            title = { Text("Editar rutina") },
            text = {
                Column {

                    // Selector de día
                    Text("Día de la semana")
                    Spacer(Modifier.height(4.dp))
                    OutlinedButton(
                        onClick = { editDayMenuExpanded = true },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(editRoutineDay)
                    }

                    DropdownMenu(
                        expanded = editDayMenuExpanded,
                        onDismissRequest = { editDayMenuExpanded = false }
                    ) {
                        daysOfWeek.forEach { day ->
                            DropdownMenuItem(
                                text = { Text(day) },
                                onClick = {
                                    editRoutineDay = day
                                    editDayMenuExpanded = false
                                }
                            )
                        }
                    }

                    Spacer(Modifier.height(12.dp))

                    // Nombre de rutina
                    OutlinedTextField(
                        value = editRoutineName,
                        onValueChange = { editRoutineName = it },
                        label = { Text("Nombre de rutina") },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        val routineId = routineToEdit!!.id

                        firestore.collection("users")
                            .document(uid)
                            .collection("routines")
                            .document(routineId)
                            .update(
                                mapOf(
                                    "day" to editRoutineDay,
                                    "name" to editRoutineName.trim()
                                )
                            )

                        showEditDialog = false
                    }
                ) {
                    Text("Guardar")
                }
            },
            dismissButton = {
                TextButton(onClick = { showEditDialog = false }) {
                    Text("Cancelar")
                }
            }
        )
    }
}

@Composable
fun RoutineItem(
    routine: Routine,
    onClick: () -> Unit,
    onEdit: () -> Unit,
    onDelete: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth(),
        colors = CardDefaults.cardColors(MaterialTheme.colorScheme.surfaceVariant),
        elevation = CardDefaults.cardElevation(defaultElevation = 3.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
                .clickable { onClick() },
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {

            // IZQUIERDA: Datos de la rutina
            Column {
                Text(
                    text = routine.day,
                    style = MaterialTheme.typography.titleMedium
                )

                if (routine.name.isNotBlank()) {
                    Spacer(Modifier.height(4.dp))
                    Text(
                        text = routine.name,
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }

            // DERECHA: Iconos editar / eliminar
            Row {
                IconButton(onClick = onEdit) {
                    Icon(Icons.Default.Edit, contentDescription = "Editar rutina")
                }
                IconButton(onClick = onDelete) {
                    Icon(Icons.Default.Delete, contentDescription = "Eliminar rutina")
                }
            }
        }
    }
}
