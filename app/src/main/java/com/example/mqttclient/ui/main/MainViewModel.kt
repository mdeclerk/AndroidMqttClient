package com.example.mqttclient.ui.main

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mqttclient.domain.MqttClient
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MainViewModel(
    private val mqtt: MqttClient,
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO
) : ViewModel() {

    val clientState = mqtt.state

    fun disconnect() = viewModelScope.launch(ioDispatcher) {
        mqtt.disconnect()
    }
}