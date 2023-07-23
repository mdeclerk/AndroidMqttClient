package com.example.mqttclient.data.local.subscribed_topics

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update

@Dao
interface SubscribedTopicDao {
    @Query("SELECT * FROM topic_table")
    suspend fun getAll(): List<SubscribedTopicDto>

    @Query("SELECT * FROM topic_table")
    fun getAllAsLiveData(): LiveData<List<SubscribedTopicDto>>

    @Query("SELECT * FROM topic_table WHERE name = :topicName")
    suspend fun findByName(topicName: String): SubscribedTopicDto?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(vararg topic: SubscribedTopicDto)

    @Update
    suspend fun update(vararg topic: SubscribedTopicDto)

    @Delete
    suspend fun delete(vararg topic: SubscribedTopicDto)

    @Query("DELETE FROM topic_table")
    suspend fun deleteAll()
}