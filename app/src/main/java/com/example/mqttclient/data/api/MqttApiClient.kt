package com.example.mqttclient.data.api

import com.hivemq.client.mqtt.MqttClient
import com.hivemq.client.mqtt.mqtt5.Mqtt5BlockingClient
import com.hivemq.client.mqtt.mqtt5.message.publish.Mqtt5PayloadFormatIndicator
import java.util.UUID
import kotlin.Exception

typealias OnConnectedListener = () -> Unit
typealias OnDisconnectedListener = () -> Unit
typealias OnMessageReceiveListener = (MqttApiMessage) -> Unit
typealias OnErrorListener = (String) -> Unit

class MqttApiClient(
    val clientId: String = "client-${UUID.randomUUID().toString()}"
) {
    companion object {
        const val DEFAULT_HOST = "broker.hivemq.com"
        const val DEFAULT_PORT = 1883
    }

    private lateinit var client: Mqtt5BlockingClient

    private var isConnecting = false

    var isConnected = false
        private set

    private var onConnectedListener: OnConnectedListener? = null
    private var onDisconnectedListener: OnDisconnectedListener? = null
    private var onMessageReceiveListener: OnMessageReceiveListener? = null
    private var onErrorListener: OnErrorListener? = null

    fun setOnConnectedListener(l: OnConnectedListener?) {
        onConnectedListener = l
    }

    fun setOnDisconnectedListener(l: OnDisconnectedListener?) {
        onDisconnectedListener = l
    }

    fun setOnMessageReceiveListener(l: OnMessageReceiveListener?) {
        onMessageReceiveListener = l
    }

    fun setOnErrorListener(l: OnErrorListener?) {
        onErrorListener = l
    }

    fun connect(host: String = DEFAULT_HOST, port: Int = DEFAULT_PORT) {
        try {
            synchronized(this) {
                if (isConnected || isConnecting) {
                    return
                }
                isConnecting = true

                client = MqttClient.builder()
                    .useMqttVersion5()
                    .identifier(clientId)
                    .serverHost(host)
                    .serverPort(port)
                    .addDisconnectedListener {
                        if (isConnected) {
                            isConnected = false
                            onDisconnectedListener?.invoke()
                        }
                    }
                    .buildBlocking()

                client.connectWith()
                    .cleanStart(false)
                    .keepAlive(10)
                    .send()

                isConnecting = false
                isConnected = true
            }

            onConnectedListener?.invoke()
        } catch (e: Exception) {
            isConnecting = false

            val err = e.message ?: "Unknown error"
            onErrorListener?.invoke(err)
        }
    }

    fun disconnect() {
        try {
            synchronized(this) {
                isConnected = false
                client.disconnectWith()
                    .send()
            }
        } catch (e: Exception) {
            //
        }

        onDisconnectedListener?.invoke()
    }

    fun subscribe(topic: String) {
        if (!isConnected) {
            return
        }

        client.toAsync().subscribeWith()
            .topicFilter(topic)
            .callback {
                val msg = MqttApiMessage(
                    topic = it.topic.toString(),
                    payload = it.payloadAsBytes.toString(Charsets.UTF_8),
                    retain = it.isRetain,
                    qos = it.qos
                )
                onMessageReceiveListener?.invoke(msg)
            }
            .send()
    }

    fun unsubscribe(topic: String) {
        if (!isConnected) {
            return
        }

        client.unsubscribeWith()
            .topicFilter(topic)
            .send()
    }

    fun publish(message: MqttApiMessage) {
        if (!isConnected) {
            return
        }

        try {
            client.publishWith()
                .topic(message.topic)
                .payload(message.payload.toByteArray(Charsets.UTF_8))
                .payloadFormatIndicator(Mqtt5PayloadFormatIndicator.UTF_8)
                .contentType("text/plain")
                .qos(message.qos)
                .retain(message.retain)
                .send()
        } catch (e: Exception) {
            val err = e.message ?: "Unknown error"
            onErrorListener?.invoke(err)
        }
    }
}