package com.example.gymvance.screens.auth

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.*
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.launch

@Composable
fun RegisterScreen(navController: NavController) {

    val auth = FirebaseAuth.getInstance()
    val firestore = FirebaseFirestore.getInstance()
    val scope = rememberCoroutineScope()

    var username by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }

    var showPassword by remember { mutableStateOf(false) }
    var showConfirmPassword by remember { mutableStateOf(false) }

    var message by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        Text("Crear cuenta", style = MaterialTheme.typography.headlineMedium)
        Spacer(modifier = Modifier.height(24.dp))

        // USERNAME
        OutlinedTextField(
            value = username,
            onValueChange = { username = it },
            label = { Text("Nombre de usuario") },
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(12.dp))

        // EMAIL
        OutlinedTextField(
            value = email,
            onValueChange = { email = it },
            label = { Text("Email") },
            singleLine = true,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(12.dp))

        // PASSWORD
        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            label = { Text("Contraseña") },
            singleLine = true,
            visualTransformation = if (showPassword)
                VisualTransformation.None
            else
                PasswordVisualTransformation(),
            trailingIcon = {
                Text(
                    text = if (showPassword) "Ocultar" else "Mostrar",
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier
                        .padding(end = 8.dp)
                        .clickable { showPassword = !showPassword }
                )
            },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(12.dp))

        // CONFIRM PASSWORD
        OutlinedTextField(
            value = confirmPassword,
            onValueChange = { confirmPassword = it },
            label = { Text("Repetir contraseña") },
            singleLine = true,
            visualTransformation = if (showConfirmPassword)
                VisualTransformation.None
            else
                PasswordVisualTransformation(),
            trailingIcon = {
                Text(
                    text = if (showConfirmPassword) "Ocultar" else "Mostrar",
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier
                        .padding(end = 8.dp)
                        .clickable { showConfirmPassword = !showConfirmPassword }
                )
            },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(20.dp))

        // BUTTON REGISTER
        Button(
            onClick = {
                when {
                    username.isBlank() || email.isBlank() || password.isBlank() || confirmPassword.isBlank() -> {
                        message = "Completa todos los campos"
                        return@Button
                    }
                    password.length < 6 -> {
                        message = "La contraseña debe tener al menos 6 caracteres"
                        return@Button
                    }
                    password != confirmPassword -> {
                        message = "Las contraseñas no coinciden"
                        return@Button
                    }
                }

                isLoading = true
                message = ""

                scope.launch {
                    firestore.collection("users")
                        .whereEqualTo("username", username.trim())
                        .get()
                        .addOnSuccessListener { docs ->
                            if (!docs.isEmpty) {
                                isLoading = false
                                message = "Ese nombre de usuario ya está registrado"
                                return@addOnSuccessListener
                            }

                            auth.createUserWithEmailAndPassword(email.trim(), password)
                                .addOnSuccessListener { result ->
                                    val uid = result.user!!.uid

                                    val userData = mapOf(
                                        "username" to username.trim(),
                                        "email" to email.trim(),
                                        "role" to "user"
                                    )

                                    firestore.collection("users").document(uid)
                                        .set(userData)
                                        .addOnSuccessListener {
                                            navController.navigate("login") {
                                                popUpTo("register") { inclusive = true }
                                            }
                                        }
                                }
                                .addOnFailureListener { e ->
                                    isLoading = false
                                    message = traducirErrorFirebase(e.message)
                                }
                        }
                }
            },
            enabled = !isLoading,
            modifier = Modifier.fillMaxWidth()
        ) {
            if (isLoading) {
                CircularProgressIndicator(
                    modifier = Modifier.size(20.dp),
                    color = MaterialTheme.colorScheme.onPrimary
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text("Creando...")
            } else {
                Text("Registrarse")
            }
        }

        if (message.isNotEmpty()) {
            Spacer(modifier = Modifier.height(12.dp))
            Text(message, color = MaterialTheme.colorScheme.error)
        }

        Spacer(modifier = Modifier.height(16.dp))

        TextButton(onClick = { navController.navigate("login") }) {
            Text("¿Ya tienes cuenta? Inicia sesión")
        }
    }
}

fun traducirErrorFirebase(error: String?): String {
    return when {
        error?.contains("email address is already in use") == true ->
            "Ese correo ya está registrado"

        error?.contains("badly formatted") == true ->
            "Formato de correo no válido"

        error?.contains("blocked all requests") == true ->
            "Demasiados intentos, inténtalo más tarde"

        else -> "Error: ${error ?: "desconocido"}"
    }
}
