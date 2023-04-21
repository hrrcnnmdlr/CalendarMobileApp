package com.example.calendar.database

import androidx.lifecycle.LiveData
import androidx.lifecycle.liveData
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.util.*

class EventRepository(private val eventDao: EventDao) {
    val allEvents: LiveData<List<Event>> = liveData {
        val events = withContext(Dispatchers.IO) {
            eventDao.getAllEvents()
        }
        emit(events)
    }

    suspend fun insert(event: Event) : Int {
        var id = 0
        withContext(Dispatchers.IO) {
            if (event.startDateTime < event.maxDateForRepeat!! || event.repeat == EventRepetition.NONE.toString()) {
                id = eventDao.addEvent(event)
            }
            if ((event.repeat == EventRepetition.DAILY.toString()) && (event.startDateTime < event.maxDateForRepeat)) {
                val calendar1 = Calendar.getInstance()
                calendar1.timeInMillis = event.startDateTime
                calendar1.add(Calendar.DAY_OF_MONTH, 1)
                val calendar2 = Calendar.getInstance()
                calendar2.timeInMillis = event.endDateTime
                calendar2.add(Calendar.DAY_OF_MONTH, 1)
                insert(copyWithNewDates(event, calendar1.timeInMillis, calendar2.timeInMillis))
            }
            if (event.repeat == EventRepetition.WEEKLY.toString() && event.startDateTime < event.maxDateForRepeat) {
                val calendar1 = Calendar.getInstance()
                calendar1.timeInMillis = event.startDateTime
                calendar1.add(Calendar.DAY_OF_MONTH, 7)
                val calendar2 = Calendar.getInstance()
                calendar2.timeInMillis = event.endDateTime
                calendar2.add(Calendar.DAY_OF_MONTH, 7)
                insert(copyWithNewDates(event, calendar1.timeInMillis, calendar2.timeInMillis))
            }
            if (event.repeat == EventRepetition.MONTHLY.toString() && event.startDateTime < event.maxDateForRepeat) {
                val calendar1 = Calendar.getInstance()
                calendar1.timeInMillis = event.startDateTime
                calendar1.add(Calendar.MONTH, 1)
                val calendar2 = Calendar.getInstance()
                calendar2.timeInMillis = event.endDateTime
                calendar2.add(Calendar.MONTH, 1)
                insert(copyWithNewDates(event, calendar1.timeInMillis, calendar2.timeInMillis))
            }
            if (event.repeat == EventRepetition.ANNUALLY.toString() && event.startDateTime < event.maxDateForRepeat) {
                val calendar1 = Calendar.getInstance()
                calendar1.timeInMillis = event.startDateTime
                calendar1.add(Calendar.YEAR, 1)
                val calendar2 = Calendar.getInstance()
                calendar2.timeInMillis = event.endDateTime
                calendar2.add(Calendar.YEAR, 1)
                insert(copyWithNewDates(event, calendar1.timeInMillis, calendar2.timeInMillis))
            }
        }
        return id
    }

    suspend fun insertForClass(event: Event) : Int {
        var id = 0
        withContext(Dispatchers.IO) {
            id = eventDao.addEvent(event)
        }
        return id
    }

    suspend fun update(event: Event): Int {
        var id: Int
        val prevEvent: Event
        withContext(Dispatchers.IO) {
            prevEvent = eventDao.getEventById(event.id)
            if (prevEvent.repeat != EventRepetition.NONE.toString()) {
                deleteAllRepeatedExceptThis(prevEvent)
            }
            id = eventDao.updateEvent(event)
            if (event.repeat == EventRepetition.DAILY.toString() && event.startDateTime < event.maxDateForRepeat!!
                && event.repeat != prevEvent.repeat
            ) {
                val calendar1 = Calendar.getInstance()
                calendar1.timeInMillis = event.startDateTime
                calendar1.add(Calendar.DAY_OF_MONTH, 1)
                val calendar2 = Calendar.getInstance()
                calendar2.timeInMillis = event.endDateTime
                calendar2.add(Calendar.DAY_OF_MONTH, 1)
                insert(copyWithNewDates(event, calendar1.timeInMillis, calendar2.timeInMillis))
            }
            if (event.repeat == EventRepetition.WEEKLY.toString() && event.startDateTime < event.maxDateForRepeat!!
                && event.repeat != prevEvent.repeat
            ) {
                val calendar1 = Calendar.getInstance()
                calendar1.timeInMillis = event.startDateTime
                calendar1.add(Calendar.DAY_OF_MONTH, 7)
                val calendar2 = Calendar.getInstance()
                calendar2.timeInMillis = event.endDateTime
                calendar2.add(Calendar.DAY_OF_MONTH, 7)
                insert(copyWithNewDates(event, calendar1.timeInMillis, calendar2.timeInMillis))
            }
            if (event.repeat == EventRepetition.MONTHLY.toString() && event.startDateTime < event.maxDateForRepeat!!
                && event.repeat != prevEvent.repeat
            ) {
                val calendar1 = Calendar.getInstance()
                calendar1.timeInMillis = event.startDateTime
                calendar1.add(Calendar.MONTH, 1)
                val calendar2 = Calendar.getInstance()
                calendar2.timeInMillis = event.endDateTime
                calendar2.add(Calendar.MONTH, 1)
                insert(copyWithNewDates(event, calendar1.timeInMillis, calendar2.timeInMillis))
            }
            if (event.repeat == EventRepetition.ANNUALLY.toString() && event.startDateTime < event.maxDateForRepeat!!
                && event.repeat != prevEvent.repeat
            ) {
                val calendar1 = Calendar.getInstance()
                calendar1.timeInMillis = event.startDateTime
                calendar1.add(Calendar.YEAR, 1)
                val calendar2 = Calendar.getInstance()
                calendar2.timeInMillis = event.endDateTime
                calendar2.add(Calendar.YEAR, 1)
                insert(copyWithNewDates(event, calendar1.timeInMillis, calendar2.timeInMillis))
            }
        }
        return id
    }

    suspend fun updateForClass(event: Event): Int {
        var id: Int
        withContext(Dispatchers.IO) {
            id = eventDao.updateEvent(event)
        }
        return id
    }

    suspend fun delete(event: Event): Int {
        var id: Int
        withContext(Dispatchers.IO) {
            id = eventDao.deleteEvent(event)
        }
        return id
    }

    suspend fun deleteAllRepeated(event: Event): List<Int> {
        val ids = mutableListOf<Int>()
        withContext(Dispatchers.IO) {
            ids.add(eventDao.deleteEvent(event))
            if (event.repeat != EventRepetition.NONE.toString()) {
                val events = event.repeatParentId?.let { eventDao.getEventsByParentId(it) }
                if (events != null) {
                    for (element in events) {
                        ids.add(eventDao.deleteEvent(element))
                    }
                }
            }
        }
        return ids
    }

    suspend fun deleteAllRepeatedExceptThis(event: Event): List<Int> {
        val ids = mutableListOf<Int>()
        withContext(Dispatchers.IO) {
            ids.add(eventDao.deleteEvent(event))
            if (event.repeat != EventRepetition.NONE.toString()) {
                val events = event.repeatParentId?.let { eventDao.getEventsByParentId(it) }
                if (events != null) {
                    for (element in events) {
                        if (element != event) {
                            ids.add(eventDao.deleteEvent(element))
                        }
                    }
                }
            }
        }
        return ids
    }

    suspend fun deleteAllNextRepeated(event: Event): List<Int> {
        val ids = mutableListOf<Int>()
        withContext(Dispatchers.IO) {
            ids.add(eventDao.deleteEvent(event))
            if (event.repeat != EventRepetition.NONE.toString()) {
                val events = event.repeatParentId?.let { eventDao.getEventsByParentId(it) }
                if (events != null) {
                    for (element in events) {
                        if (element.startDateTime >= event.startDateTime) {
                            ids.add(eventDao.deleteEvent(element))
                        }
                    }
                }
            }
        }
        return ids
    }

    suspend fun deleteForClass(event: Event): Int {
        var id: Int
        withContext(Dispatchers.IO) {
            id = eventDao.deleteEvent(event)
        }
        return id
    }


    suspend fun getEventById(eventId: Int): Event {
        return withContext(Dispatchers.IO) {
            eventDao.getEventById(eventId)
        }
    }

    suspend fun getEventsByParentId(parentId: Int): List<Event> {
        var events: List<Event>
        withContext(Dispatchers.IO) {
            events = eventDao.getEventsByParentId(parentId)
        }
        return events
    }
}

fun copyWithNewDates(event: Event, startDate: Long, endDate: Long): Event {
    return event.copy(
        id = 0, // Призначаємо 0, щоб зберегти як новий запис в базі даних
        startDateTime = startDate,
        endDateTime = endDate
    )
}