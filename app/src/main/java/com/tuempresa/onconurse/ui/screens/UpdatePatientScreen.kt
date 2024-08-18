package com.tuempresa.onconurse.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.Alignment
import com.google.firebase.firestore.FirebaseFirestore
import com.tuempresa.onconurse.models.Patient

@Composable
fun UpdatePatientScreen(patientId: String, onPatientUpdated: () -> Unit) {
    var name by remember { mutableStateOf("") }
    var age by remember { mutableStateOf("") }
    var diagnosis by remember { mutableStateOf("") }
    var currentTreatment by remember { mutableStateOf("") }
    var treatmentName by remember { mutableStateOf("") }
    var dose by remember { mutableStateOf("") }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var isLoading by remember { mutableStateOf(true) }

    // Recuperar los datos actuales del paciente
    LaunchedEffect(patientId) {
        if (patientId.isNotEmpty()) {
            fetchPatientForUpdate(patientId) { patient, error ->
                isLoading = false
                if (error != null) {
                    errorMessage = "Error al recuperar el paciente: $error"
                } else if (patient != null) {
                    name = patient.name ?: ""  // Asegurarse de que no sean nulos
                    age = patient.age.toString()
                    diagnosis = patient.diagnosis ?: ""
                    currentTreatment = patient.currentTreatment ?: ""
                    treatmentName = patient.treatmentName ?: ""
                    dose = patient.dose ?: "" // Asegurarse de que no sea nulo
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
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        if (isLoading) {
            Text(text = "Cargando detalles del paciente...")
        } else if (!errorMessage.isNullOrEmpty()) {  // Verificación actualizada aquí
            Spacer(modifier = Modifier.height(16.dp))
            Text(text = errorMessage ?: "", color = MaterialTheme.colorScheme.error)  // Verificación adicional con Elvis operator
        } else {
            Text(text = "Actualizar Datos del Paciente", style = MaterialTheme.typography.headlineSmall)
            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = name,
                onValueChange = { name = it },
                label = { Text("Nombre Completo") },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(
                value = age,
                onValueChange = { age = it },
                label = { Text("Edad") },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(
                value = diagnosis,
                onValueChange = { diagnosis = it },
                label = { Text("Diagnóstico") },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(
                value = currentTreatment,
                onValueChange = { currentTreatment = it },
                label = { Text("Tratamiento Actual") },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(
                value = treatmentName,
                onValueChange = { treatmentName = it },
                label = { Text("Nombre del Tratamiento") },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(
                value = dose,
                onValueChange = { dose = it },
                label = { Text("Dosis") },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(16.dp))

            Button(onClick = {
                if (name.isNotEmpty() && age.isNotEmpty() && diagnosis.isNotEmpty() && currentTreatment.isNotEmpty() && treatmentName.isNotEmpty() && dose.isNotEmpty()) {
                    val updatedPatient = Patient(
                        id = patientId,
                        name = name,
                        age = age.toIntOrNull() ?: 0,
                        diagnosis = diagnosis,
                        currentTreatment = currentTreatment,
                        treatmentName = treatmentName,
                        dose = dose // Actualizar con el valor de dosis
                    )
                    updatePatient(patientId, updatedPatient)
                    onPatientUpdated()
                } else {
                    errorMessage = "Por favor, complete todos los campos"
                }
            }) {
                Text(text = "Guardar Cambios")
            }

            if (!errorMessage.isNullOrEmpty()) {
                Spacer(modifier = Modifier.height(16.dp))
                Text(text = errorMessage ?: "", color = MaterialTheme.colorScheme.error)
            }
        }
    }
}

// Función para recuperar un paciente por ID
fun fetchPatientForUpdate(patientId: String, onPatientRetrieved: (Patient?, String?) -> Unit) {
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

// Función para actualizar un paciente
fun updatePatient(patientId: String, updatedPatient: Patient) {
    val db = FirebaseFirestore.getInstance()
    db.collection("patients")
        .document(patientId)
        .set(updatedPatient)
        .addOnSuccessListener {
            println("Paciente actualizado con éxito")
        }
        .addOnFailureListener { e ->
            println("Error al actualizar el paciente: $e")
        }
}
