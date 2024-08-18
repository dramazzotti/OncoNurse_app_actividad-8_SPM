package com.tuempresa.onconurse

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.tuempresa.onconurse.ui.screens.AppNavigation
import com.tuempresa.onconurse.ui.theme.OncoNurseTheme

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            OncoNurseTheme {
                AppNavigation()  // Aquí estamos cargando la navegación
            }
        }
    }
}
