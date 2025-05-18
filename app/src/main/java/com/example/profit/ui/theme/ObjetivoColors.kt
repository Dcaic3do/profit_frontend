package com.example.profit.ui.theme

import androidx.compose.ui.graphics.Color

fun objetivoColor(nombre: String): Color {
    return when (nombre.lowercase()) {
        "perder peso" -> ObjetivoPerderPeso
        "mantener peso" -> ObjetivoMantener
        "ganar masa muscular" -> ObjetivoGanarMasa
        else -> Primary
    }
}
