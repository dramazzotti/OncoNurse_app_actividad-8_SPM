package com.tuempresa.onconurse.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.Text
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.Alignment
import androidx.navigation.NavHostController
import com.google.firebase.firestore.FirebaseFirestore
import com.tuempresa.onconurse.models.Patient
import androidx.compose.material3.Button
import androidx.compose.runtime.*

@Composable
fun PatientDetailsScreen(patientId: String, navController: NavHostController, onPatientDeleted: () -> Unit) {
    var patient by remember { mutableStateOf<Patient?>(null) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var isLoading by remember { mutableStateOf(true) }

    LaunchedEffect(patientId) {
        if (patientId.isNotEmpty()) {
            getPatientDetails(patientId) { retrievedPatient, error ->
                isLoading = false
                if (error != null) {
                    errorMessage = "Error al recuperar el paciente: $error"
                } else if (retrievedPatient != null) {
                    patient = retrievedPatient
                } else {
                    errorMessage = "No se encontró el paciente"
                }
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.Start
    ) {
        if (isLoading) {
            Text(text = "Cargando detalles del paciente...")
        } else if (errorMessage != null) {
            Text(text = errorMessage ?: "", color = MaterialTheme.colorScheme.error)
        } else if (patient != null) {
            Text(text = "Detalles del Paciente", style = MaterialTheme.typography.headlineSmall)
            Spacer(modifier = Modifier.height(16.dp))
            Text(text = "Nombre: ${patient!!.name}")
            Text(text = "Edad: ${patient!!.age} años")
            Text(text = "Diagnóstico: ${patient!!.diagnosis}")
            Text(text = "Tratamiento actual: ${patient!!.currentTreatment}")
            Text(text = "Nombre del Tratamiento: ${patient!!.treatmentName}")
            Text(text = "Dosis: ${patient!!.dose}")
            Spacer(modifier = Modifier.height(16.dp))

            // Botón para actualizar la información del paciente
            Button(onClick = {
                navController.navigate("update_patient/$patientId")
            }) {
                Text(text = "Actualizar Información")
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Botón para eliminar el paciente
            Button(onClick = {
                deletePatient(patientId)
                onPatientDeleted()
            }) {
                Text(text = "Eliminar Paciente")
            }
        }
    }
}

// Función para recuperar un paciente por ID
fun getPatientDetails(patientId: String, onPatientRetrieved: (Patient?, String?) -> Unit) {
    val db = FirebaseFirestore.getInstance()
    db.collection("patients")
        .document(patientId)
        .get()
        .addOnSuccessListener { document ->
            val patient = document.toObject(Patient::class.java)
            onPatientRetrieved(patient, null)
        }
        .addOnFailureListener { e ->
            onPatientRetrieved(null, e.message)
        }
}

// Función para eliminar un paciente
fun deletePatient(patientId: String) {
    val db = FirebaseFirestore.getInstance()
    db.collection("patients")
        .document(patientId)
        .delete()
        .addOnSuccessListener {
            println("Paciente eliminado con éxito")
        }
        .addOnFailureListener { e ->
            println("Error al eliminar el paciente: $e")
        }
}
