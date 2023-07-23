package com.example.mqttclient.data.local.recent_brokers

import androidx.room.Entity

@Entity(tableName = "broker_table", primaryKeys = ["host", "port"])
data class RecentBrokerDto(
    val host: String,
    val port: Int,
    val timestamp: Long = System.currentTimeMillis(),
)
