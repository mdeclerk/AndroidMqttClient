package com.example.mqttclient.ui.main

import androidx.lifecycle.ViewModel
import androidx.lifecycle.asFlow
import androidx.lifecycle.viewModelScope
import com.example.mqttclient.domain.MqttClient
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch

sealed interface MainEffect {
    data object NavigateToConnect : MainEffect
    data class ShowError(val message: String) : MainEffect
}

class MainViewModel(
    private val mqtt: MqttClient,
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO,
) : ViewModel() {

    private val _effects = Channel<MainEffect>(Channel.BUFFERED)
    val effects = _effects.receiveAsFlow()

    init {
        viewModelScope.launch {
            mqtt.state.asFlow().collect { state ->
                when (state) {
                    is MqttClient.State.Disconnected -> _effects.send(MainEffect.NavigateToConnect)
                    is MqttClient.State.Error -> {
                        _effects.send(MainEffect.ShowError(state.message))
                        disconnect()
                    }
                    else -> Unit
                }
            }
        }
    }

    fun disconnect() = viewModelScope.launch(ioDispatcher) {
        mqtt.disconnect()
    }
}
