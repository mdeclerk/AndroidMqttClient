package com.example.mqttclient.ui.connect

import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.map
import androidx.lifecycle.viewModelScope
import com.example.mqttclient.data.api.MqttApiClient
import com.example.mqttclient.data.local.recent_brokers.RecentBrokersDao
import com.example.mqttclient.domain.RecentBroker
import com.example.mqttclient.domain.MqttClient
import com.example.mqttclient.domain.toRecentBrokerDTO
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class ConnectViewModel(
    private val brokerDao: RecentBrokersDao,
    private val mqttClient: MqttClient,
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO,
) : ViewModel() {

    val host = MutableLiveData<String>()
    val port = MutableLiveData<String>()

    val connectEnabled = MediatorLiveData<Boolean>()

    val clientId = mqttClient.clientId
    val clientState = mqttClient.state
    val isConnecting = mqttClient.state.map { it == MqttClient.State.Connecting }
    val brokerList = mqttClient.recentBrokersList

    init {
        setDefaultHostAndPort()
        setLatestHostAndPort()

        connectEnabled.apply {
            addSource(host) { value = isConnectEnabled() }
            addSource(port) { value = isConnectEnabled() }
            addSource(mqttClient.state) { value = isConnectEnabled() }
        }
    }

    private fun validateHostAndPort(): Boolean {
        val hostName = host.value!!
        val portNumber = port.value!!.toUShortOrNull()
        return hostName.isNotEmpty() && !hostName[0].isDigit()
                && portNumber != null && portNumber >= 0u && portNumber <= UShort.MAX_VALUE
    }

    private fun isConnectEnabled() =
        validateHostAndPort() && mqttClient.state.value!! == MqttClient.State.Disconnected

    fun connect() {
        if (isConnectEnabled()) {
            val recentBroker = RecentBroker(host.value!!, port.value!!.toInt())
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
                host.postValue(it.host)
                port.postValue(it.port.toString())
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