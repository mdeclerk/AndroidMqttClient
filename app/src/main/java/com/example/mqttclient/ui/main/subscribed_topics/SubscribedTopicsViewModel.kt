package com.example.mqttclient.ui.main.subscribed_topics

import androidx.lifecycle.ViewModel
import androidx.lifecycle.asFlow
import androidx.lifecycle.viewModelScope
import com.example.mqttclient.domain.MqttClient
import com.example.mqttclient.domain.SubscribedTopic
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

data class SubscribedTopicsUiState(
    val topics: List<SubscribedTopic> = emptyList(),
    val newTopicName: String = DEFAULT_TOPIC_NAME,
) {
    companion object {
        const val DEFAULT_TOPIC_NAME = "testtopic/#"
    }
}

class SubscribedTopicsViewModel(
    private val mqtt: MqttClient,
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO,
) : ViewModel() {

    private val newTopicName = MutableStateFlow(SubscribedTopicsUiState.DEFAULT_TOPIC_NAME)

    val uiState: StateFlow<SubscribedTopicsUiState> =
        combine(mqtt.subscribedTopicList.asFlow(), newTopicName) { topics, name ->
            SubscribedTopicsUiState(topics = topics, newTopicName = name)
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = SubscribedTopicsUiState(),
        )

    private fun validateTopicName(topicName: String) = topicName.isNotBlank()

    fun onNewTopicNameChange(value: String) {
        newTopicName.value = value
    }

    fun createNewTopic() {
        val name = newTopicName.value
        if (validateTopicName(name)) {
            viewModelScope.launch(ioDispatcher) {
                mqtt.subscribe(name)
            }
            newTopicName.value = SubscribedTopicsUiState.DEFAULT_TOPIC_NAME
        }
    }

    fun toggleSubscription(topic: SubscribedTopic) {
        viewModelScope.launch(ioDispatcher) {
            if (topic.isSubscribed) {
                mqtt.unsubscribe(topic.name)
            } else if (validateTopicName(topic.name)) {
                mqtt.subscribe(topic.name)
            }
        }
    }

    fun deleteTopic(topic: SubscribedTopic) {
        viewModelScope.launch(ioDispatcher) {
            mqtt.unsubscribe(topic.name, deleteFromDb = true)
        }
    }
}
