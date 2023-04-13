package com.example.calendar

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface EventDao {
    @Query("SELECT * FROM events")
    fun getAllEvents(): Flow<List<Event>>

    @Query("SELECT * FROM events WHERE id = :id")
    fun getEventById(id: Int): Event

    @Query("SELECT * FROM events WHERE startDateTime >= :date AND startDateTime < :date + 86400000 OR endDateTime >= :date AND endDateTime < :date + 86400000")
    fun getEventsForDay(date: Long): List<Event>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun addEvent(event: Event)

    @Delete
    fun deleteEvent(event: Event)
}