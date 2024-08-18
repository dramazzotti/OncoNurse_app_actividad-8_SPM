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
import androidx.compose.ui.tooling.preview.Preview
import com.google.firebase.firestore.FirebaseFirestore
import com.tuempresa.onconurse.models.Patient
import com.tuempresa.onconurse.ui.theme.OncoNurseTheme

@Composable
fun RegisterPatientScreen(onPatientRegistered: () -> Unit = {}) {
    var name by remember { mutableStateOf("") }
    var age by remember { mutableStateOf("") }
    var diagnosis by remember { mutableStateOf("") }
    var currentTreatment by remember { mutableStateOf("") }
    var treatmentName by remember { mutableStateOf("") }
    var dose by remember { mutableStateOf("") }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    val db = FirebaseFirestore.getInstance()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(text = "Registrar Paciente", style = MaterialTheme.typography.headlineSmall)
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
                val patient = Patient(
                    id = db.collection("patients").document().id, // Generar un ID único para el paciente
                    name = name,
                    age = age.toIntOrNull() ?: 0, // Convertir la edad a Int
                    diagnosis = diagnosis,
                    currentTreatment = currentTreatment,
                    treatmentName = treatmentName,
                    dose = dose
                )

                db.collection("patients").document(patient.id)
                    .set(patient)
                    .addOnSuccessListener {
                        errorMessage = "Paciente registrado con éxito"
                        onPatientRegistered() // Llamar a la función de callback para navegar o realizar otra acción
                    }
                    .addOnFailureListener { e ->
                        errorMessage = "Error al registrar paciente: $e"
                    }
            } else {
                errorMessage = "Por favor, complete todos los campos"
            }
        }) {
            Text(text = "Registrar Paciente")
        }

        if (errorMessage != null) {
            Spacer(modifier = Modifier.height(16.dp))
            Text(text = errorMessage ?: "", color = MaterialTheme.colorScheme.error)
        }
    }
}

@Preview(showBackground = true)
@Composable
fun RegisterPatientScreenPreview() {
    OncoNurseTheme {
        RegisterPatientScreen()
    }
}
