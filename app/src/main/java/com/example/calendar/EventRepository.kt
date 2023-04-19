package com.example.calendar

import androidx.lifecycle.LiveData
import androidx.lifecycle.liveData
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class EventRepository(private val eventDao: EventDao) {
    val allEvents: LiveData<List<Event>> = liveData {
        val events = withContext(Dispatchers.IO) {
            eventDao.getAllEvents()
        }
        emit(events)
    }

    suspend fun insert(event: Event) {
        withContext(Dispatchers.IO) {
            eventDao.addEvent(event)
        }
    }

    suspend fun update(event: Event) {
        withContext(Dispatchers.IO) {
            eventDao.updateEvent(event)
        }
    }

    suspend fun delete(event: Event) {
        withContext(Dispatchers.IO) {
            eventDao.deleteEvent(event)
        }
    }

    suspend fun getEventById(eventId: Int): Event? {
        return withContext(Dispatchers.IO) {
            eventDao.getEventById(eventId)
        }
    }
}
