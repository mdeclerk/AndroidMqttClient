package com.example.mqttclient.data.api

import com.hivemq.client.mqtt.datatypes.MqttQos

data class MqttApiMessage(
    val topic: String,
    val payload: String,
    val retain: Boolean = false,
    val qos: MqttQos = MqttQos.AT_MOST_ONCE,
    val timestamp: Long = System.currentTimeMillis()
)