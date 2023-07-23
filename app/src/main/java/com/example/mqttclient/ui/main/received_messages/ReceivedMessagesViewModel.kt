package com.example.mqttclient.ui.main.received_messages

import androidx.lifecycle.ViewModel
import com.example.mqttclient.domain.MqttClient

class ReceivedMessagesViewModel(private val mqtt: MqttClient) : ViewModel() {

    val receivedMessagesList = mqtt.receivedMessagesList

    fun deleteAllMessages() = mqtt.clearReceivedMessagesList()
}