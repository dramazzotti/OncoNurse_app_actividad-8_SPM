package com.tuempresa.onconurse.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.tuempresa.onconurse.ui.theme.OncoNurseTheme
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

@Composable
fun LoginScreen(
    onLoginClick: (String, String) -> Unit
) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    val auth = FirebaseAuth.getInstance()
    val db = FirebaseFirestore.getInstance()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(text = "OncoNurse", style = MaterialTheme.typography.headlineMedium)
        Spacer(modifier = Modifier.height(16.dp))
        OutlinedTextField(
            value = email,
            onValueChange = { email = it },
            label = { Text("Email") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(8.dp))
        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            label = { Text("Password") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(16.dp))

        if (isLoading) {
            CircularProgressIndicator()
        } else {
            Button(onClick = {
                isLoading = true
                errorMessage = null
                if (email.isNotEmpty() && password.isNotEmpty()) {
                    auth.signInWithEmailAndPassword(email, password)
                        .addOnCompleteListener { task ->
                            isLoading = false
                            if (task.isSuccessful) {
                                val user = auth.currentUser
                                user?.let {
                                    db.collection("users").document(it.uid).get()
                                        .addOnSuccessListener { document ->
                                            if (document != null && document.exists()) {
                                                val role = document.getString("role")
                                                val name = document.getString("name")
                                                println("Usuario: $name, Rol: $role")
                                                onLoginClick(email, password)
                                            } else {
                                                errorMessage = "No se encontró información del usuario"
                                            }
                                        }
                                        .addOnFailureListener { e ->
                                            errorMessage = "Error al obtener el rol: ${e.localizedMessage}"
                                        }
                                }
                            } else {
                                errorMessage = "Error de autenticación: ${task.exception?.localizedMessage}"
                            }
                        }
                } else {
                    isLoading = false
                    errorMessage = "Por favor, ingrese email y contraseña"
                }
            }) {
                Text(text = "Login")
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        errorMessage?.let {
            Text(text = it, color = MaterialTheme.colorScheme.error)
        }
    }
}

@Preview(showBackground = true)
@Composable
fun LoginScreenPreview() {
    OncoNurseTheme {
        LoginScreen(onLoginClick = { _, _ -> })
    }
}
