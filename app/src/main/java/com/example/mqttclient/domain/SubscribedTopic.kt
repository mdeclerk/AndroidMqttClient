package com.example.mqttclient.domain

import com.example.mqttclient.data.local.subscribed_topics.SubscribedTopicDto

data class SubscribedTopic(
    val name: String,
    val isSubscribed: Boolean,
)

fun SubscribedTopicDto.toSubscribedTopic() = SubscribedTopic(
    name = name,
    isSubscribed = isSubscribed,
)

fun List<SubscribedTopicDto>.toSubscribedTopicList() = map {
    it.toSubscribedTopic()
}

fun SubscribedTopic.toSubscribedTopicDTO() = SubscribedTopicDto(
    name = name,
    isSubscribed = isSubscribed,
)
