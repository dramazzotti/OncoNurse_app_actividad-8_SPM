package com.tuempresa.onconurse.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.Alignment
import com.tuempresa.onconurse.ui.theme.OncoNurseTheme
import com.google.firebase.firestore.FirebaseFirestore

@Composable
fun ReportsScreen() {
    var reportResult by remember { mutableStateOf("") }
    var errorMessage by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.Start
    ) {
        Text(text = "Generar Reportes", style = MaterialTheme.typography.headlineSmall)
        Spacer(modifier = Modifier.height(16.dp))

        Button(onClick = {
            errorMessage = ""
            reportResult = ""
            generateReport { report, error ->
                if (error != null) {
                    errorMessage = error
                } else {
                    reportResult = report ?: "No se encontraron registros de pacientes."
                }
            }
        }) {
            Text(text = "Generar Reporte")
        }

        Spacer(modifier = Modifier.height(16.dp))

        if (errorMessage.isNotEmpty()) {
            Text(text = "Error: $errorMessage", color = MaterialTheme.colorScheme.error)
        } else if (reportResult.isNotEmpty()) {
            Text(text = "Resultado del Reporte:", style = MaterialTheme.typography.headlineSmall)
            Text(text = reportResult)
        }
    }
}

fun generateReport(onReportGenerated: (String?, String?) -> Unit) {
    val db = FirebaseFirestore.getInstance()

    db.collection("patients")
        .get()
        .addOnSuccessListener { documents ->
            val report = StringBuilder()
            for (document in documents) {
                val data = document.data
                report.append("Paciente: ${data["name"]}\n")
                report.append("Edad: ${data["age"]} años\n")
                report.append("Diagnóstico: ${data["diagnosis"]}\n")
                report.append("Tratamiento: ${data["currentTreatment"]}\n\n")
            }
            onReportGenerated(report.toString(), null)
        }
        .addOnFailureListener { e ->
            onReportGenerated(null, "Error al generar el reporte: ${e.message}")
        }
}

@Preview(showBackground = true)
@Composable
fun ReportsScreenPreview() {
    OncoNurseTheme {
        ReportsScreen()
    }
}
