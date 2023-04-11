package com.example.calendar

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface EventDao {
    @Query("SELECT * FROM events")
    fun getAllEvents(): Flow<List<Event>>

    @Query("SELECT * FROM events WHERE id = :id")
    fun getEventById(id: Int): Event

    @Query("SELECT * FROM events WHERE startDateTime = :dateTime")
    fun getEventByStart(dateTime: Long): Event

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun addEvent(event: Event)

    @Delete
    fun deleteEvent(event: Event)
}