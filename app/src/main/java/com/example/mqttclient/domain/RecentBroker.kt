package com.example.mqttclient.domain

import com.example.mqttclient.data.local.recent_brokers.RecentBrokerDto
import java.sql.Timestamp

data class RecentBroker(
    val host: String,
    val port: Int,
    val timestamp: Timestamp = Timestamp(System.currentTimeMillis()),
) {
    override fun toString() = "${host}:${port}"
}

fun RecentBrokerDto.toRecentBroker() = RecentBroker(
    host = host,
    port = port,
    timestamp = Timestamp(timestamp),
)

fun List<RecentBrokerDto>.toRecentBrokerList() = map {
    it.toRecentBroker()
}

fun RecentBroker.toRecentBrokerDTO() = RecentBrokerDto(
    host = host,
    port = port,
    timestamp = timestamp.time
)