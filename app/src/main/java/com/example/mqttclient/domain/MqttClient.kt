package com.example.mqttclient.domain

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.map
import com.example.mqttclient.data.api.MqttApiClient
import com.example.mqttclient.data.local.recent_brokers.RecentBrokersDao
import com.example.mqttclient.data.local.subscribed_topics.SubscribedTopicDto
import com.example.mqttclient.data.local.subscribed_topics.SubscribedTopicDao
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers

class MqttClient(
    private val recentBrokersDao: RecentBrokersDao,
    private val subscribedTopicsDao: SubscribedTopicDao,
    private val mqtt: MqttApiClient,
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO,
) {
    val clientId = mqtt.clientId

    sealed class State {
        object Connected : State()
        object Connecting : State()
        object Disconnected : State()
        data class Error(val message: String) : State()
    }

    private val _state = MutableLiveData<State>(State.Disconnected)
    val state: LiveData<State> = _state

    val recentBrokersList = recentBrokersDao.getAllAsLiveData().map {
        it.toRecentBrokerList()
    }

    val subscribedTopicList = subscribedTopicsDao.getAllAsLiveData().map {
        it.toSubscribedTopicList()
    }

    private val _receivedMessagesList = MutableLiveData<List<MqttMessage>>(emptyList())
    val receivedMessagesList: LiveData<List<MqttMessage>> = _receivedMessagesList

    init {
        mqtt.setOnConnectedListener {
            _state.postValue(State.Connected)
        }

        mqtt.setOnDisconnectedListener {
            _state.postValue(State.Disconnected)
        }

        mqtt.setOnMessageReceiveListener {
            val msg = it.toMqttMessage()
            val lst = _receivedMessagesList.value!!
            _receivedMessagesList.postValue(listOf(msg) + lst)
        }

        mqtt.setOnErrorListener {
            _state.postValue(State.Error(it))
        }
    }

    suspend fun connect(recentBroker: RecentBroker) {
        if (_state.value!! == State.Connected || _state.value!! == State.Connecting) {
            return
        }
        _state.postValue(State.Connecting)

        mqtt.connect(recentBroker.host, recentBroker.port)
        if (mqtt.isConnected) {
            clearReceivedMessagesList()

            subscribedTopicsDao.getAll().forEach {
                if (it.isSubscribed) {
                    mqtt.subscribe(it.name)
                }
            }
        }

        recentBrokersDao.insert(recentBroker.toRecentBrokerDTO())
    }

    fun disconnect() = mqtt.disconnect()

    suspend fun subscribe(topicName: String, addToDb: Boolean = true) {
        mqtt.subscribe(topicName)

        val topic = subscribedTopicsDao.findByName(topicName)
        if (topic == null) {
            if (addToDb) {
                val newTopic = SubscribedTopicDto(topicName, true)
                subscribedTopicsDao.insert(newTopic)
            }
        } else {
            subscribedTopicsDao.update(topic.copy(isSubscribed = true))
        }
    }

    suspend fun unsubscribe(topicName: String, deleteFromDb: Boolean = false) {
        mqtt.unsubscribe(topicName)

        subscribedTopicsDao.findByName(topicName)?.let {
            if (deleteFromDb) {
                subscribedTopicsDao.delete(it)
            } else {
                subscribedTopicsDao.update(it.copy(isSubscribed = false))
            }
        }
    }

    fun publish(mqttMessage: MqttMessage) = mqtt.publish(mqttMessage.toMqttApiMessage())

    fun clearReceivedMessagesList() = _receivedMessagesList.postValue(emptyList())
}