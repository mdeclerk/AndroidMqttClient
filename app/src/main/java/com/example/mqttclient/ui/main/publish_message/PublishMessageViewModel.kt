package com.example.mqttclient.ui.main.publish_message

import androidx.lifecycle.ViewModel
import com.example.mqttclient.domain.MqttClient
import com.example.mqttclient.domain.MqttMessage
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

data class PublishMessageUiState(
    val topic: String = "testtopic",
    val payload: String = "testmsg",
    val qos: String = "0",
    val retain: Boolean = false,
    val isInputValid: Boolean = false,
)

class PublishMessageViewModel(private val mqtt: MqttClient) : ViewModel() {

    private val _uiState = MutableStateFlow(PublishMessageUiState().withValidation())
    val uiState: StateFlow<PublishMessageUiState> = _uiState.asStateFlow()

    fun onTopicChange(value: String) = update { it.copy(topic = value) }

    fun onPayloadChange(value: String) = update { it.copy(payload = value) }

    fun onQosChange(value: String) = update { it.copy(qos = value) }

    fun onRetainChange(value: Boolean) = update { it.copy(retain = value) }

    private inline fun update(block: (PublishMessageUiState) -> PublishMessageUiState) {
        _uiState.update { block(it).withValidation() }
    }

    fun send() {
        val state = _uiState.value
        // NOTE: preserves the original behaviour of publishing with default qos/retain.
        val msg = MqttMessage(state.topic, state.payload)
        mqtt.publish(msg)
    }
}

private fun PublishMessageUiState.withValidation(): PublishMessageUiState {
    val qosValue = qos.toIntOrNull()
    val valid = topic.isNotBlank() && payload.isNotBlank() && qosValue != null && qosValue in 0..2
    return copy(isInputValid = valid)
}
