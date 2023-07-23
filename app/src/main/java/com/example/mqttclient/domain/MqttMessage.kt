package com.example.mqttclient.domain

import com.example.mqttclient.data.api.MqttApiMessage
import com.hivemq.client.mqtt.datatypes.MqttQos
import java.sql.Timestamp

data class MqttMessage(
    val topic: String,
    val payload: String,
    val retain: Boolean = false,
    val qos: Int = 0,
    val timestamp: Timestamp = Timestamp(System.currentTimeMillis()),
)

fun MqttApiMessage.toMqttMessage() = MqttMessage(
    topic = topic,
    payload = payload,
    retain = retain,
    qos = qos.code,
    timestamp = timestamp,
)

fun convertIntToMqttQos(qos: Int): MqttQos = when(qos) {
    0 -> MqttQos.AT_MOST_ONCE
    1 -> MqttQos.AT_LEAST_ONCE
    2 -> MqttQos.EXACTLY_ONCE
    else -> MqttQos.AT_MOST_ONCE
}

fun MqttMessage.toMqttApiMessage() = MqttApiMessage(
    topic = topic,
    payload = payload,
    retain = retain,
    qos = convertIntToMqttQos(qos),
    timestamp = timestamp,
)