package com.example.mqttclient.ui.components

import androidx.compose.material3.ButtonColors
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.TopAppBarColors
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable

/**
 * Tinted top app bar using the Material 3 `primaryContainer` role — a tonal,
 * spec-aligned way to give the bar a branded color without filling it with the
 * high-emphasis `primary`. The container also paints behind the status bar.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun appTopAppBarColors(): TopAppBarColors = TopAppBarDefaults.topAppBarColors(
    containerColor = MaterialTheme.colorScheme.primaryContainer,
    titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer,
    actionIconContentColor = MaterialTheme.colorScheme.onPrimaryContainer,
    navigationIconContentColor = MaterialTheme.colorScheme.onPrimaryContainer,
)

/**
 * Text-button colors for actions placed in the tinted top app bar. A plain
 * TextButton would draw its label in `primary`, which is low-contrast on the
 * `primaryContainer` background.
 */
@Composable
fun appTopBarTextButtonColors(): ButtonColors = ButtonDefaults.textButtonColors(
    contentColor = MaterialTheme.colorScheme.onPrimaryContainer,
)
