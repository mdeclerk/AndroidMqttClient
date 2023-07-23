package com.example.mqttclient.data.local.subscribed_topics

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "topic_table")
data class SubscribedTopicDto(
    @PrimaryKey
    val name: String,
    val isSubscribed: Boolean,
)