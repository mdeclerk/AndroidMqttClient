package com.example.mqttclient.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.mqttclient.data.local.recent_brokers.RecentBrokerDto
import com.example.mqttclient.data.local.recent_brokers.RecentBrokersDao
import com.example.mqttclient.data.local.subscribed_topics.SubscribedTopicDao
import com.example.mqttclient.data.local.subscribed_topics.SubscribedTopicDto

@Database(entities = [RecentBrokerDto::class, SubscribedTopicDto::class], version = 1, exportSchema = false)
abstract class LocalDatabase : RoomDatabase() {
    abstract fun recentBrokersDao(): RecentBrokersDao
    abstract fun subscribedTopicsDao(): SubscribedTopicDao
}

fun createLocalDatabase(context: Context) = Room.databaseBuilder(
    context.applicationContext,
    LocalDatabase::class.java,
    "LocalDb"
).build()
