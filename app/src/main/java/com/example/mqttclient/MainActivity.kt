package com.example.mqttclient

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.example.mqttclient.ui.navigation.AppNavHost
import com.example.mqttclient.ui.theme.MqttClientTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)
        setContent {
            MqttClientTheme {
                AppNavHost()
            }
        }
    }
}
