package com.example.calendar.database

import android.util.Log
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

    suspend fun insert(event: Event, maxDate: Long) {
        withContext(Dispatchers.IO) {
            if (event.startDateTime < maxDate) {
                eventDao.addEvent(event)
            }
            if (event.remind5MinutesBefore) {
                Log.d("REMIND", "5 minutes")
            }
            if (event.remind15MinutesBefore) {
                Log.d("REMIND", "15 minutes")
            }
            if (event.remind30MinutesBefore) {
                Log.d("REMIND", "30 minutes")
            }
            if (event.remind1HourBefore) {
                Log.d("REMIND", "1 hour")
            }
            if (event.remind1DayBefore) {
                Log.d("REMIND", "1 day")
            }
            if (event.repeat == EventRepetition.DAILY.toString() && event.startDateTime < maxDate) {
                val calendar1 = Calendar.getInstance()
                calendar1.timeInMillis = event.startDateTime
                calendar1.add(Calendar.DAY_OF_MONTH, 1)
                val calendar2 = Calendar.getInstance()
                calendar2.timeInMillis = event.endDateTime
                calendar2.add(Calendar.DAY_OF_MONTH, 1)
                insert(copyWithNewDates(event, calendar1.timeInMillis, calendar2.timeInMillis), maxDate)
            }
            else if (event.repeat == EventRepetition.WEEKLY.toString() && event.startDateTime < maxDate) {
                val calendar1 = Calendar.getInstance()
                calendar1.timeInMillis = event.startDateTime
                calendar1.add(Calendar.DAY_OF_MONTH, 7)
                val calendar2 = Calendar.getInstance()
                calendar2.timeInMillis = event.endDateTime
                calendar2.add(Calendar.DAY_OF_MONTH, 7)
                insert(copyWithNewDates(event, calendar1.timeInMillis, calendar2.timeInMillis), maxDate)
            }
            else if (event.repeat == EventRepetition.MONTHLY.toString() && event.startDateTime < maxDate) {
                val calendar1 = Calendar.getInstance()
                calendar1.timeInMillis = event.startDateTime
                calendar1.add(Calendar.MONTH, 1)
                val calendar2 = Calendar.getInstance()
                calendar2.timeInMillis = event.endDateTime
                calendar2.add(Calendar.MONTH, 1)
                insert(copyWithNewDates(event, calendar1.timeInMillis, calendar2.timeInMillis), maxDate)
            }
            else if (event.repeat == EventRepetition.ANNUALLY.toString() && event.startDateTime < maxDate) {
                val calendar1 = Calendar.getInstance()
                calendar1.timeInMillis = event.startDateTime
                calendar1.add(Calendar.YEAR, 1)
                val calendar2 = Calendar.getInstance()
                calendar2.timeInMillis = event.endDateTime
                calendar2.add(Calendar.YEAR, 1)
                insert(copyWithNewDates(event, calendar1.timeInMillis, calendar2.timeInMillis), maxDate)
            }
        }
    }

    suspend fun insert(event: Event) {
        withContext(Dispatchers.IO) {
            eventDao.addEvent(event)
            if (event.remind5MinutesBefore) {
                Log.d("REMIND", "5 minutes")
            }
            if (event.remind15MinutesBefore) {
                Log.d("REMIND", "15 minutes")
            }
            if (event.remind30MinutesBefore) {
                Log.d("REMIND", "30 minutes")
            }
            if (event.remind1HourBefore) {
                Log.d("REMIND", "1 hour")
            }
            if (event.remind1DayBefore) {
                Log.d("REMIND", "1 day")
            }
        }
    }

    suspend fun update(event: Event, maxDate: Long) {
        withContext(Dispatchers.IO) {
            eventDao.updateEvent(event)
            if (event.remind5MinutesBefore) {
                Log.d("REMIND", "5 minutes")
            }
            if (event.remind15MinutesBefore) {
                Log.d("REMIND", "15 minutes")
            }
            if (event.remind30MinutesBefore) {
                Log.d("REMIND", "30 minutes")
            }
            if (event.remind1HourBefore) {
                Log.d("REMIND", "1 hour")
            }
            if (event.remind1DayBefore) {
                Log.d("REMIND", "1 day")
            }
            if (event.repeat == EventRepetition.DAILY.toString() && event.startDateTime < maxDate) {
                val calendar1 = Calendar.getInstance()
                calendar1.timeInMillis = event.startDateTime
                calendar1.add(Calendar.DAY_OF_MONTH, 1)
                val calendar2 = Calendar.getInstance()
                calendar2.timeInMillis = event.endDateTime
                calendar2.add(Calendar.DAY_OF_MONTH, 1)
                insert(copyWithNewDates(event, calendar1.timeInMillis, calendar2.timeInMillis), maxDate)
            }
            else if (event.repeat == EventRepetition.WEEKLY.toString() && event.startDateTime < maxDate) {
                val calendar1 = Calendar.getInstance()
                calendar1.timeInMillis = event.startDateTime
                calendar1.add(Calendar.DAY_OF_MONTH, 7)
                val calendar2 = Calendar.getInstance()
                calendar2.timeInMillis = event.endDateTime
                calendar2.add(Calendar.DAY_OF_MONTH, 7)
                insert(copyWithNewDates(event, calendar1.timeInMillis, calendar2.timeInMillis), maxDate)
            }
            else if (event.repeat == EventRepetition.MONTHLY.toString() && event.startDateTime < maxDate) {
                val calendar1 = Calendar.getInstance()
                calendar1.timeInMillis = event.startDateTime
                calendar1.add(Calendar.MONTH, 1)
                val calendar2 = Calendar.getInstance()
                calendar2.timeInMillis = event.endDateTime
                calendar2.add(Calendar.MONTH, 1)
                insert(copyWithNewDates(event, calendar1.timeInMillis, calendar2.timeInMillis), maxDate)
            }
            else if (event.repeat == EventRepetition.ANNUALLY.toString() && event.startDateTime < maxDate) {
                val calendar1 = Calendar.getInstance()
                calendar1.timeInMillis = event.startDateTime
                calendar1.add(Calendar.YEAR, 1)
                val calendar2 = Calendar.getInstance()
                calendar2.timeInMillis = event.endDateTime
                calendar2.add(Calendar.YEAR, 1)
                insert(copyWithNewDates(event, calendar1.timeInMillis, calendar2.timeInMillis), maxDate)
            }
        }
    }

    suspend fun update(event: Event) {
        withContext(Dispatchers.IO) {
            eventDao.updateEvent(event)
            if (event.remind5MinutesBefore) {
                Log.d("REMIND", "5 minutes")
            }
            if (event.remind15MinutesBefore) {
                Log.d("REMIND", "15 minutes")
            }
            if (event.remind30MinutesBefore) {
                Log.d("REMIND", "30 minutes")
            }
            if (event.remind1HourBefore) {
                Log.d("REMIND", "1 hour")
            }
            if (event.remind1DayBefore) {
                Log.d("REMIND", "1 day")
            }
        }
    }

    suspend fun delete(event: Event) {
        withContext(Dispatchers.IO) {
            if (event.remind5MinutesBefore) {
                Log.d("DELETE REMIND", "5 minutes")
            }
            if (event.remind15MinutesBefore) {
                Log.d("DELETE REMIND", "15 minutes")
            }
            if (event.remind30MinutesBefore) {
                Log.d("DELETE REMIND", "30 minutes")
            }
            if (event.remind1HourBefore) {
                Log.d("DELETE REMIND", "1 hour")
            }
            if (event.remind1DayBefore) {
                Log.d("DELETE REMIND", "1 day")
            }
            eventDao.deleteEvent(event)
        }
    }
    suspend fun delete(event: Event, maxDate: Long) {
        withContext(Dispatchers.IO) {
            eventDao.deleteEvent(event)
            if (event.repeat == EventRepetition.DAILY.toString() && event.startDateTime < maxDate) {
                val calendar1 = Calendar.getInstance()
                calendar1.timeInMillis = event.startDateTime
                calendar1.add(Calendar.DAY_OF_MONTH, 1)
                val calendar2 = Calendar.getInstance()
                calendar2.timeInMillis = event.endDateTime
                calendar2.add(Calendar.DAY_OF_MONTH, 1)
                val repeatedEvent = copyWithNewDates(event, calendar1.timeInMillis, calendar2.timeInMillis)
                eventDao.deleteEvent(repeatedEvent)
                delete(repeatedEvent, maxDate)
            }
            else if (event.repeat == EventRepetition.WEEKLY.toString() && event.startDateTime < maxDate) {
                val calendar1 = Calendar.getInstance()
                calendar1.timeInMillis = event.startDateTime
                calendar1.add(Calendar.DAY_OF_MONTH, 7)
                val calendar2 = Calendar.getInstance()
                calendar2.timeInMillis = event.endDateTime
                calendar2.add(Calendar.DAY_OF_MONTH, 7)
                val repeatedEvent = copyWithNewDates(event, calendar1.timeInMillis, calendar2.timeInMillis)
                eventDao.deleteEvent(repeatedEvent)
                delete(repeatedEvent, maxDate)
            }
            else if (event.repeat == EventRepetition.MONTHLY.toString() && event.startDateTime < maxDate) {
                val calendar1 = Calendar.getInstance()
                calendar1.timeInMillis = event.startDateTime
                calendar1.add(Calendar.MONTH, 1)
                val calendar2 = Calendar.getInstance()
                calendar2.timeInMillis = event.endDateTime
                calendar2.add(Calendar.MONTH, 1)
                val repeatedEvent = copyWithNewDates(event, calendar1.timeInMillis, calendar2.timeInMillis)
                eventDao.deleteEvent(repeatedEvent)
                delete(repeatedEvent, maxDate)
            }
            else if (event.repeat == EventRepetition.ANNUALLY.toString() && event.startDateTime < maxDate) {
                val calendar1 = Calendar.getInstance()
                calendar1.timeInMillis = event.startDateTime
                calendar1.add(Calendar.YEAR, 1)
                val calendar2 = Calendar.getInstance()
                calendar2.timeInMillis = event.endDateTime
                calendar2.add(Calendar.YEAR, 1)
                val repeatedEvent = copyWithNewDates(event, calendar1.timeInMillis, calendar2.timeInMillis)
                eventDao.deleteEvent(repeatedEvent)
                delete(repeatedEvent, maxDate)
            }
        }
    }

    suspend fun getEventById(eventId: Int): Event? {
        return withContext(Dispatchers.IO) {
            eventDao.getEventById(eventId)
        }
    }
}

fun copyWithNewDates(event: Event, startDate: Long, endDate: Long): Event {
    return event.copy(
        id = 0, // Призначаємо 0, щоб зберегти як новий запис в базі даних
        startDateTime = startDate,
        endDateTime = endDate
    )
}