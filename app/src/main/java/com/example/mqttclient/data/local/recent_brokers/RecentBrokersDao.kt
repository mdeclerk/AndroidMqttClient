package com.example.mqttclient.data.local.recent_brokers

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface RecentBrokersDao {
    @Query("SELECT * FROM broker_table ORDER BY timestamp DESC")
    suspend fun getAll(): List<RecentBrokerDto>

    @Query("SELECT * FROM broker_table ORDER BY timestamp DESC")
    fun getAllAsLiveData(): LiveData<List<RecentBrokerDto>>

    @Query("SELECT * FROM broker_table ORDER BY timestamp DESC LIMIT 1")
    suspend fun getLast(): RecentBrokerDto?

    @Query("SELECT * FROM broker_table ORDER BY timestamp DESC LIMIT 1")
    fun getLastAsLiveData(): LiveData<RecentBrokerDto?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(vararg broker: RecentBrokerDto)

    @Delete
    suspend fun delete(vararg broker: RecentBrokerDto)

    @Query("DELETE FROM broker_table")
    suspend fun deleteAll()
}