package com.tuempresa.onconurse.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.google.firebase.firestore.FirebaseFirestore
import com.tuempresa.onconurse.models.Patient

@Composable
fun DashboardScreen(
    onPatientClick: (String) -> Unit,
    onRegisterPatientClick: () -> Unit,
    onGenerateReportClick: () -> Unit,
    onViewNotificationsClick: (String) -> Unit // Añadimos el ID del paciente como parámetro
) {
    var patients by remember { mutableStateOf<List<Patient>>(emptyList()) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(Unit) {
        val db = FirebaseFirestore.getInstance()
        db.collection("patients")
            .get()
            .addOnSuccessListener { documents ->
                patients = documents.mapNotNull { doc ->
                    doc.toObject(Patient::class.java)?.copy(id = doc.id)
                }
            }
            .addOnFailureListener { exception ->
                errorMessage = "Error al recuperar pacientes: ${exception.localizedMessage}"
            }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(text = "Bienvenido a OncoNurse", style = MaterialTheme.typography.headlineMedium)
        Spacer(modifier = Modifier.height(16.dp))

        if (patients.isEmpty() && errorMessage == null) {
            Text(text = "Cargando pacientes...")
        } else if (errorMessage != null) {
            Text(text = errorMessage!!, color = MaterialTheme.colorScheme.error)
        } else {
            patients.forEach { patient ->
                Text(
                    text = "Paciente ${patient.name}",
                    modifier = Modifier
                        .padding(8.dp)
                        .clickable { onPatientClick(patient.id) }
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Button(onClick = onRegisterPatientClick) {
            Text(text = "Registrar Paciente")
        }
        Spacer(modifier = Modifier.height(8.dp))

        Button(onClick = onGenerateReportClick) {
            Text(text = "Generar Reportes")
        }
        Spacer(modifier = Modifier.height(8.dp))

        // Botón para ver notificaciones, solicitando el ID del paciente
        Button(onClick = {
            if (patients.isNotEmpty()) {
                onViewNotificationsClick(patients.first().id)
            }
        }) {
            Text(text = "Ver Notificaciones")
        }
    }
}
