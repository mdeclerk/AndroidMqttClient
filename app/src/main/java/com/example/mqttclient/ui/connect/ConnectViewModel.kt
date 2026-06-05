package com.example.mqttclient.ui.connect

import androidx.lifecycle.ViewModel
import androidx.lifecycle.asFlow
import androidx.lifecycle.viewModelScope
import com.example.mqttclient.data.api.MqttApiClient
import com.example.mqttclient.data.local.recent_brokers.RecentBrokersDao
import com.example.mqttclient.domain.MqttClient
import com.example.mqttclient.domain.RecentBroker
import com.example.mqttclient.domain.toRecentBrokerDTO
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

data class ConnectUiState(
    val host: String = "",
    val port: String = "",
    val clientId: String = "",
    val isConnecting: Boolean = false,
    val connectEnabled: Boolean = false,
    val recentBrokers: List<RecentBroker> = emptyList(),
)

sealed interface ConnectEffect {
    data object NavigateToMain : ConnectEffect
    data class ShowError(val message: String) : ConnectEffect
}

class ConnectViewModel(
    private val brokerDao: RecentBrokersDao,
    private val mqttClient: MqttClient,
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO,
) : ViewModel() {

    private val host = MutableStateFlow(MqttApiClient.DEFAULT_HOST)
    private val port = MutableStateFlow(MqttApiClient.DEFAULT_PORT.toString())

    private val _effects = Channel<ConnectEffect>(Channel.BUFFERED)
    val effects = _effects.receiveAsFlow()

    val uiState: StateFlow<ConnectUiState> =
        combine(
            host,
            port,
            mqttClient.state.asFlow(),
            mqttClient.recentBrokersList.asFlow(),
        ) { host, port, state, brokers ->
            ConnectUiState(
                host = host,
                port = port,
                clientId = mqttClient.clientId,
                isConnecting = state == MqttClient.State.Connecting,
                connectEnabled = isConnectEnabled(host, port, state),
                recentBrokers = brokers,
            )
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = ConnectUiState(
                host = host.value,
                port = port.value,
                clientId = mqttClient.clientId,
            ),
        )

    init {
        setLatestHostAndPort()
        observeClientState()
    }

    private fun observeClientState() {
        viewModelScope.launch {
            mqttClient.state.asFlow().collect { state ->
                when (state) {
                    is MqttClient.State.Connected -> _effects.send(ConnectEffect.NavigateToMain)
                    is MqttClient.State.Error -> {
                        _effects.send(ConnectEffect.ShowError(state.message))
                        disconnect()
                    }
                    else -> Unit
                }
            }
        }
    }

    private fun validateHostAndPort(host: String, port: String): Boolean {
        val portNumber = port.toUShortOrNull()
        return host.isNotBlank() && portNumber != null && portNumber > 0u
    }

    private fun isConnectEnabled(host: String, port: String, state: MqttClient.State) =
        validateHostAndPort(host, port) && state == MqttClient.State.Disconnected

    fun onHostChange(value: String) {
        host.value = value
    }

    fun onPortChange(value: String) {
        port.value = value
    }

    fun applyBroker(broker: RecentBroker) {
        host.value = broker.host
        port.value = broker.port.toString()
    }

    fun clearHostAndPort() {
        host.value = ""
        port.value = ""
    }

    fun connect() {
        val current = uiState.value
        if (current.connectEnabled) {
            val recentBroker = RecentBroker(current.host, current.port.toInt())
            viewModelScope.launch(ioDispatcher) {
                mqttClient.connect(recentBroker)
            }
        }
    }

    fun disconnect() = viewModelScope.launch(ioDispatcher) {
        mqttClient.disconnect()
    }

    fun setDefaultHostAndPort() {
        host.value = MqttApiClient.DEFAULT_HOST
        port.value = MqttApiClient.DEFAULT_PORT.toString()
    }

    private fun setLatestHostAndPort() {
        viewModelScope.launch(ioDispatcher) {
            brokerDao.getLast()?.let {
                host.value = it.host
                port.value = it.port.toString()
            }
        }
    }

    fun deleteRecentBroker(recentBroker: RecentBroker) {
        viewModelScope.launch(ioDispatcher) {
            brokerDao.delete(recentBroker.toRecentBrokerDTO())
        }
    }

    fun deleteAllRecentBrokers() {
        viewModelScope.launch(ioDispatcher) {
            brokerDao.deleteAll()
        }
    }
}
