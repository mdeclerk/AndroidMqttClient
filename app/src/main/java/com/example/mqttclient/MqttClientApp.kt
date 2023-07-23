package com.example.mqttclient

import android.app.Application
import com.example.mqttclient.data.api.MqttApiClient
import com.example.mqttclient.data.local.LocalDatabase
import com.example.mqttclient.data.local.createLocalDatabase
import com.example.mqttclient.domain.MqttClient
import com.example.mqttclient.ui.connect.ConnectViewModel
import com.example.mqttclient.ui.main.MainViewModel
import com.example.mqttclient.ui.main.received_messages.ReceivedMessagesViewModel
import com.example.mqttclient.ui.main.publish_message.PublishMessageViewModel
import com.example.mqttclient.ui.main.subscribed_topics.SubscribedTopicsViewModel
import org.koin.android.ext.koin.androidContext
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.core.context.startKoin
import org.koin.dsl.module

class MqttClientApp : Application() {
    override fun onCreate() {
        super.onCreate()

        val myModule = module {
            viewModel { ConnectViewModel(get(), get()) }
            viewModel { MainViewModel(get()) }
            viewModel { SubscribedTopicsViewModel(get()) }
            viewModel { ReceivedMessagesViewModel(get()) }
            viewModel { PublishMessageViewModel(get()) }
            single { createLocalDatabase(get()) }
            single { get<LocalDatabase>().recentBrokersDao() }
            single { get<LocalDatabase>().subscribedTopicsDao() }
            single { MqttApiClient() }
            single { MqttClient(get(), get(), get()) }
        }

        startKoin {
            androidContext(this@MqttClientApp)
            modules(listOf(myModule))
        }
    }
}