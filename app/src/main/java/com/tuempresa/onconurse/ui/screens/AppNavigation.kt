package com.tuempresa.onconurse.ui.screens

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController

@Composable
fun AppNavigation() {
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = "login") {
        composable("login") {
            LoginScreen(onLoginClick = { _, _ ->
                navController.navigate("dashboard")
            })
        }
        composable("dashboard") {
            DashboardScreen(
                onPatientClick = { patientId ->
                    navController.navigate("patient_details/$patientId")
                },
                onRegisterPatientClick = {
                    navController.navigate("register_patient")
                },
                onGenerateReportClick = {
                    navController.navigate("reports")
                },
                onViewNotificationsClick = {
                    navController.navigate("notifications")
                }
            )
        }
        composable("patient_details/{patientId}") { backStackEntry ->
            val patientId = backStackEntry.arguments?.getString("patientId") ?: ""
            PatientDetailsScreen(
                patientId = patientId,
                navController = navController,
                onPatientDeleted = {
                    navController.popBackStack()
                }
            )
        }
        composable("update_patient/{patientId}") { backStackEntry ->
            val patientId = backStackEntry.arguments?.getString("patientId") ?: ""
            UpdatePatientScreen(
                patientId = patientId,
                onPatientUpdated = {
                    navController.popBackStack() // Navegar de regreso después de la actualización
                }
            )
        }
        composable("treatment_form") {
            TreatmentFormScreen()
        }
        composable("reports") {
            ReportsScreen()
        }
        composable("notifications") {
            NotificationsScreen()
        }
        composable("register_patient") {
            RegisterPatientScreen(onPatientRegistered = {
                navController.popBackStack()
            })
        }
    }
}
