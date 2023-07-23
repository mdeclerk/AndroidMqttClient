package com.example.mqttclient.ui.main.subscribed_topics

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mqttclient.domain.MqttClient
import com.example.mqttclient.domain.SubscribedTopic
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class SubscribedTopicsViewModel(
    private val mqtt: MqttClient,
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO
) : ViewModel() {

    val topicName = MutableLiveData("testtopic/#")
    val topicList = mqtt.subscribedTopicList

    private fun validateTopicName(topicName: String) =
        topicName.isNotEmpty() && !topicName[0].isDigit()

    fun createNewTopicWithTopicName() {
        topicName.value?.let {
            if (validateTopicName(it)) {
                viewModelScope.launch(ioDispatcher) {
                    mqtt.subscribe(it)
                }
            }
        }
    }

    fun subscribeTopic(subscribedTopic: SubscribedTopic) {
        if (validateTopicName(subscribedTopic.name)) {
            viewModelScope.launch(ioDispatcher) {
                mqtt.subscribe(subscribedTopic.name)
            }
        }
    }

    fun unsubscribeTopic(subscribedTopic: SubscribedTopic) {
        viewModelScope.launch(ioDispatcher) {
            mqtt.unsubscribe(subscribedTopic.name)
        }
    }

    fun deleteTopicFromDb(subscribedTopic: SubscribedTopic) {
        viewModelScope.launch(ioDispatcher) {
            mqtt.unsubscribe(subscribedTopic.name, true)
        }
    }
}