package com.tuempresa.onconurse.models

data class Patient(
    val id: String = "",
    val name: String = "",
    val age: Int = 0,
    val diagnosis: String = "",
    val currentTreatment: String = "",
    val treatmentName: String = "",
    val dose: String = "" // Agregar este campo para la dosis
)

