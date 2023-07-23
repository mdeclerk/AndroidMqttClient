package com.example.mqttclient.ui.main.publish_message

import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.mqttclient.domain.MqttClient
import com.example.mqttclient.domain.MqttMessage

class PublishMessageViewModel(private val mqtt: MqttClient) : ViewModel() {

    val topic = MutableLiveData("testtopic")
    val payload = MutableLiveData("testmsg")
    val qos = MutableLiveData("0")
    val retain = MutableLiveData(false)

    val isInputValid = MediatorLiveData<Boolean>(false)

    init {
        isInputValid.apply {
            addSource(topic) { value = validateInputs() }
            addSource(payload) { value = validateInputs() }
            addSource(qos) { value = validateInputs() }
        }
    }

    fun validateInputs() =
        !topic.value!!.isNullOrBlank() && !payload.value!!.isNullOrBlank() && 0 <= qos.value!!.toInt() && qos.value!!.toInt() <= 2

    fun send() {
        val msg = MqttMessage(topic.value!!, payload.value!!)
        mqtt.publish(msg)
    }
}