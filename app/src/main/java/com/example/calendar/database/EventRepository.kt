package com.example.calendar.database

import android.util.Log
import androidx.lifecycle.LiveData
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.util.*
import kotlin.math.max
import kotlin.math.min

class EventRepository(private val eventDao: EventDao) {
    val allEvents: LiveData<List<Event>> = eventDao.getAllEvents()

    suspend fun insert(event: Event) : Int {
        var id: Int
        withContext(Dispatchers.IO) {
            if (event.repeat == EventRepetition.NONE.toString()) {
                id = eventDao.addEvent(event).toInt()
            } else if (event.maxDateForRepeat != null) {
                if (event.startDateTime <= event.maxDateForRepeat) {
                    id = eventDao.addEvent(event).toInt()
                    val calendar1 = Calendar.getInstance()
                    calendar1.timeInMillis = event.startDateTime
                    val calendar2 = Calendar.getInstance()
                    calendar2.timeInMillis = event.endDateTime
                    if (event.repeat == EventRepetition.DAILY.toString()
                        && event.startDateTime <= event.maxDateForRepeat) {
                        do {
                            calendar1.add(Calendar.DAY_OF_MONTH, 1)
                            calendar2.add(Calendar.DAY_OF_MONTH, 1)
                            val num = eventDao.addRepeat(
                                copyWithNewDates(
                                    event,
                                    calendar1.timeInMillis,
                                    calendar2.timeInMillis,
                                    id
                                )
                            )
                        } while (num != 0L)
                    } else if (event.repeat == EventRepetition.WEEKLY.toString()
                        && event.startDateTime <= event.maxDateForRepeat) {
                        do {
                            calendar1.add(Calendar.DAY_OF_MONTH, 7)
                            calendar2.add(Calendar.DAY_OF_MONTH, 7)
                            val num = eventDao.addRepeat(
                                copyWithNewDates(
                                    event,
                                    calendar1.timeInMillis,
                                    calendar2.timeInMillis,
                                    id
                                )
                            )
                        } while (num != 0L)
                    } else if (event.repeat == EventRepetition.MONTHLY.toString()
                        && event.startDateTime <= event.maxDateForRepeat) {
                        do {
                            calendar1.add(Calendar.MONTH, 1)
                            calendar2.add(Calendar.MONTH, 1)
                            val num = eventDao.addRepeat(
                                copyWithNewDates(
                                    event,
                                    calendar1.timeInMillis,
                                    calendar2.timeInMillis,
                                    id
                                )
                            )
                        } while (num != 0L)
                    } else if (event.repeat == EventRepetition.ANNUALLY.toString()
                        && event.startDateTime <= event.maxDateForRepeat) {
                        do {
                            calendar1.add(Calendar.YEAR, 1)
                            calendar2.add(Calendar.YEAR, 1)
                            val num = eventDao.addRepeat(
                                copyWithNewDates(
                                    event,
                                    calendar1.timeInMillis,
                                    calendar2.timeInMillis,
                                    id
                                )
                            )
                        } while (num != 0L)
                    } else {
                        id = 0
                    }
                } else {
                    id = 0
                }
            } else {
                id = 0
            }
        }
        return id
    }

    suspend fun insertForClass(event: Event) : Int {
        var id: Int
        withContext(Dispatchers.IO) {
            id = eventDao.addEvent(event).toInt()
        }
        return id
    }

    suspend fun update(event: Event, updateAll: Boolean = false, updateNext: Boolean = false, updateOne: Boolean = false): Int {
        var id = 0
        val prevEvent: Event = eventDao.getEventById(event.id)
        Log.d("UPDATEPREVEVENT   1", "$id   $prevEvent ${event.repeat != EventRepetition.NONE.toString()} ${prevEvent.maxDateForRepeat == event.maxDateForRepeat}")
        if (event.repeat != EventRepetition.NONE.toString()) {
            id = eventDao.updateEvent(event)
            Log.d("IDUPDATEEVENT    1", "$id   $event")
            if (updateAll) {
                withContext(Dispatchers.IO) {
                    val dates: List<Long> = deleteAllRepeatedMin(event)
                    withContext(Dispatchers.IO) {
                        id = eventDao.addEvent(event).toInt()
                        val calendar1 = Calendar.getInstance()
                        calendar1.timeInMillis = dates[0]
                        val calendar2 = Calendar.getInstance()
                        calendar2.timeInMillis = dates[1]
                        if (event.repeat == EventRepetition.DAILY.toString()) {
                            do {
                                calendar1.add(Calendar.DAY_OF_MONTH, 1)
                                calendar2.add(Calendar.DAY_OF_MONTH, 1)
                                val num = eventDao.addRepeat(
                                    copyWithNewDates(
                                        event,
                                        calendar1.timeInMillis,
                                        calendar2.timeInMillis,
                                        id
                                    )
                                )
                            } while (num != 0L)
                        } else if (event.repeat == EventRepetition.WEEKLY.toString()) {
                            do {
                                calendar1.add(Calendar.DAY_OF_MONTH, 7)
                                calendar2.add(Calendar.DAY_OF_MONTH, 7)
                                val num = eventDao.addRepeat(
                                    copyWithNewDates(
                                        event,
                                        calendar1.timeInMillis,
                                        calendar2.timeInMillis,
                                        id
                                    )
                                )
                            } while (num != 0L)
                        } else if (event.repeat == EventRepetition.MONTHLY.toString()) {
                            do {
                                calendar1.add(Calendar.MONTH, 1)
                                calendar2.add(Calendar.MONTH, 1)
                                val num = eventDao.addRepeat(
                                    copyWithNewDates(
                                        event,
                                        calendar1.timeInMillis,
                                        calendar2.timeInMillis,
                                        id
                                    )
                                )
                            } while (num != 0L)
                        } else if ((event.repeat == EventRepetition.ANNUALLY.toString())) {
                            do {
                                calendar1.add(Calendar.YEAR, 1)
                                calendar2.add(Calendar.YEAR, 1)
                                val num = eventDao.addRepeat(
                                    copyWithNewDates(
                                        event,
                                        calendar1.timeInMillis,
                                        calendar2.timeInMillis,
                                        id
                                    )
                                )
                            } while (num != 0L)
                        }
                    }
                }
                return id
            } else if (updateNext) {
                withContext(Dispatchers.IO) {
                    if (event.maxDateForRepeat != null) {
                        deleteAllNextRepeated(event)
                        withContext(Dispatchers.IO) {
                            id = eventDao.addEvent(event).toInt()
                            val calendar1 = Calendar.getInstance()
                            calendar1.timeInMillis = event.startDateTime
                            val calendar2 = Calendar.getInstance()
                            calendar2.timeInMillis = event.endDateTime
                            when (event.repeat) {
                                EventRepetition.DAILY.toString() -> {
                                    do {
                                        calendar1.add(Calendar.DAY_OF_MONTH, 1)
                                        calendar2.add(Calendar.DAY_OF_MONTH, 1)
                                        val num = eventDao.addRepeat(
                                            copyWithNewDates(
                                                event,
                                                calendar1.timeInMillis,
                                                calendar2.timeInMillis,
                                                id
                                            )
                                        )
                                    } while (num != 0L)
                                }
                                EventRepetition.WEEKLY.toString() -> {
                                    do {
                                        calendar1.add(Calendar.DAY_OF_MONTH, 7)
                                        calendar2.add(Calendar.DAY_OF_MONTH, 7)
                                        val num = eventDao.addRepeat(
                                            copyWithNewDates(
                                                event,
                                                calendar1.timeInMillis,
                                                calendar2.timeInMillis,
                                                id
                                            )
                                        )
                                    } while (num != 0L)
                                }
                                EventRepetition.MONTHLY.toString() -> {
                                    do {
                                        calendar1.add(Calendar.MONTH, 1)
                                        calendar2.add(Calendar.MONTH, 1)
                                        val num = eventDao.addRepeat(
                                            copyWithNewDates(
                                                event,
                                                calendar1.timeInMillis,
                                                calendar2.timeInMillis,
                                                id
                                            )
                                        )
                                    } while (num != 0L)
                                }
                                EventRepetition.ANNUALLY.toString() -> {
                                    do {
                                        calendar1.add(Calendar.YEAR, 1)
                                        calendar2.add(Calendar.YEAR, 1)
                                        val num = eventDao.addRepeat(
                                            copyWithNewDates(
                                                event,
                                                calendar1.timeInMillis,
                                                calendar2.timeInMillis,
                                                id
                                            )
                                        )
                                    } while (num != 0L)
                                }
                            }
                        }
                    }
                }
                return id
            }
            else if (updateOne) {
                return id
            }
        } else if (event.repeat == EventRepetition.NONE.toString()) {
            Log.d("IDUPDATEEVENT one  1", "$id   $event")
            id = eventDao.updateEvent(event)
            return id
        }
        return id
    }

    suspend fun updateForClass(event: Event): Int {
        return eventDao.updateEvent(event)
    }

    suspend fun delete(event: Event): Int {
        Log.d("ID", event.id.toString())
        return eventDao.deleteEvent(event)
    }

    suspend fun deleteAllRepeated(event: Event): List<Int> {
        val ids = mutableListOf<Int>()
        ids.add(eventDao.deleteEvent(event))
        if (event.repeat != EventRepetition.NONE.toString()) {
            val events = event.repeatParentId?.let { eventDao.getEventsByParentId(it) }
            if (events != null) {
                for (element in events) {
                    ids.add(eventDao.deleteEvent(element))
                }
            }
        }
        return ids
    }

    suspend fun deleteAllRepeatedMin(event: Event): List<Long> {
        val end = mutableListOf<Long>()
        val start = mutableListOf<Long>()
        end.add(event.endDateTime)
        start.add(event.startDateTime)
        eventDao.deleteEvent(event)
        if (event.repeat != EventRepetition.NONE.toString()) {
            val events = event.repeatParentId?.let { eventDao.getEventsByParentId(it) }
            if (events != null) {
                for (element in events) {
                    start.add(element.startDateTime)
                    end.add(element.endDateTime)
                    eventDao.deleteEvent(element)
                }
            }
        }
        var i = 0
        var id = 0
        var t = start[0]
        for (time in start){
            if (t > time) {
                t = time
                id = i
            }
            i++
        }
        return listOf( start[id], end[id])
    }

    private suspend fun deleteAllRepeatedExceptThis(event: Event): List<Int> {
        val ids = mutableListOf<Int>()
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
        return ids
    }

    suspend fun deleteAllNextRepeated(event: Event): List<Int> {
        val ids = mutableListOf<Int>()
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
        return ids
    }


    suspend fun deleteForClass(event: Event): Int {
        return eventDao.deleteEvent(event)
    }


   fun getEventById(eventId: Int): Event {
       return eventDao.getEventById(eventId)
   }

    fun getEventsByParentId(parentId: Int): List<Event> {
        return eventDao.getEventsByParentId(parentId)
    }

    fun getEventsForDay(date: Long): LiveData<List<Event>> {
        return eventDao.getEventsForDay(date)
    }

}

fun copyWithNewDates(event: Event, startDate: Long, endDate: Long, parentId: Int): Event {
    return event.copy(
        id = 0, // Призначаємо 0, щоб зберегти як новий запис в базі даних
        startDateTime = startDate,
        endDateTime = endDate,
        repeatParentId = parentId
    )
}