package com.example.mqttclient.ui.main.received_messages

import androidx.lifecycle.ViewModel
import androidx.lifecycle.asFlow
import androidx.lifecycle.viewModelScope
import com.example.mqttclient.domain.MqttClient
import com.example.mqttclient.domain.MqttMessage
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

data class ReceivedMessagesUiState(
    val messages: List<MqttMessage> = emptyList(),
)

class ReceivedMessagesViewModel(private val mqtt: MqttClient) : ViewModel() {

    val uiState: StateFlow<ReceivedMessagesUiState> =
        mqtt.receivedMessagesList.asFlow()
            .map { ReceivedMessagesUiState(it) }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5_000),
                initialValue = ReceivedMessagesUiState(),
            )

    fun deleteAllMessages() = mqtt.clearReceivedMessagesList()
}
