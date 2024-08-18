package com.tuempresa.onconurse.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.Text
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.Alignment
import com.google.firebase.firestore.FirebaseFirestore

@Composable
fun NotificationsScreen() {
    var notifications by remember { mutableStateOf<List<String>>(emptyList()) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(Unit) {
        val db = FirebaseFirestore.getInstance()
        db.collection("notifications")
            .get()
            .addOnSuccessListener { documents ->
                notifications = documents.mapNotNull { it.getString("message") }
            }
            .addOnFailureListener { exception ->
                errorMessage = "Error al recuperar notificaciones: ${exception.message}"
            }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.Start
    ) {
        if (errorMessage != null) {
            Text(text = errorMessage ?: "", color = MaterialTheme.colorScheme.error)
        } else if (notifications.isNotEmpty()) {
            notifications.forEach { notification ->
                Text(text = notification)
                Spacer(modifier = Modifier.height(8.dp))
            }
        } else {
            Text(text = "No hay notificaciones registradas.")
        }
    }
}
