package com.example.calendar.database

import android.util.Log
import androidx.lifecycle.LiveData
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.util.*

class EventRepository(private val eventDao: EventDao) {
    val allEvents: LiveData<List<Event>> = eventDao.getAllEvents()

    suspend fun insert(event: Event) : Int {
        var id = 0
        withContext(Dispatchers.IO) {
            if (event.repeat == EventRepetition.NONE.toString()) {
                id = eventDao.addEvent(event)
            } else if (event.maxDateForRepeat != null) {
                if (event.startDateTime < event.maxDateForRepeat) {
                    id = eventDao.addEvent(event)
                    val calendar1 = Calendar.getInstance()
                    calendar1.timeInMillis = event.startDateTime
                    val calendar2 = Calendar.getInstance()
                    calendar2.timeInMillis = event.endDateTime
                    if (event.repeat == EventRepetition.DAILY.toString()) {
                        do {
                            calendar1.add(Calendar.DAY_OF_MONTH, 1)
                            calendar2.add(Calendar.DAY_OF_MONTH, 1)
                            eventDao.addRepeat(
                                copyWithNewDates(
                                    event,
                                    calendar1.timeInMillis,
                                    calendar2.timeInMillis,
                                )
                            )
                        } while (event.startDateTime < event.maxDateForRepeat)
                    } else if (event.repeat == EventRepetition.WEEKLY.toString() && event.startDateTime < event.maxDateForRepeat) {
                        do {
                            calendar1.add(Calendar.DAY_OF_MONTH, 7)
                            calendar2.add(Calendar.DAY_OF_MONTH, 7)
                            eventDao.addRepeat(
                                copyWithNewDates(
                                    event,
                                    calendar1.timeInMillis,
                                    calendar2.timeInMillis,
                                )
                            )
                        } while (event.startDateTime < event.maxDateForRepeat)
                    } else if (event.repeat == EventRepetition.MONTHLY.toString() && event.startDateTime < event.maxDateForRepeat) {
                        do {
                            calendar1.add(Calendar.MONTH, 1)
                            calendar2.add(Calendar.MONTH, 1)
                            eventDao.addRepeat(
                                copyWithNewDates(
                                    event,
                                    calendar1.timeInMillis,
                                    calendar2.timeInMillis,
                                )
                            )
                        } while (event.startDateTime < event.maxDateForRepeat)
                    } else if (event.repeat == EventRepetition.ANNUALLY.toString() && event.startDateTime < event.maxDateForRepeat) {
                        do {
                            calendar1.add(Calendar.YEAR, 1)
                            calendar2.add(Calendar.YEAR, 1)
                            eventDao.addRepeat(
                                copyWithNewDates(
                                    event,
                                    calendar1.timeInMillis,
                                    calendar2.timeInMillis,
                                )
                            )
                        } while (event.startDateTime < event.maxDateForRepeat)
                    }
                }
            }
        }
        return id
    }

    suspend fun insertForClass(event: Event) : Int {
        var id: Int
        withContext(Dispatchers.IO) {
            id = eventDao.addEvent(event)
        }
        return id
    }

    suspend fun update(event: Event, updateAll: Boolean = false, updateNext: Boolean = false, updateOne: Boolean = false): Int {
        var id: Int = 0
        val prevEvent: Event = eventDao.getEventById(event.id)
        if (prevEvent.repeat != EventRepetition.NONE.toString() && prevEvent.maxDateForRepeat == event.maxDateForRepeat) {
            id = eventDao.updateEvent(event)
            if (updateAll) {
                withContext(Dispatchers.IO) {
                    if (event.maxDateForRepeat != null) {
                        if (prevEvent.repeat == event.repeat && event.repeat !=
                            EventRepetition.NONE.toString() && event.repeatParentId != null
                        ) {
                            for (elem in eventDao.getEventsByParentId(event.repeatParentId)) {
                                update(elem)
                            }
                        } else {
                            deleteAllRepeatedExceptThis(event)
                            withContext(Dispatchers.IO) {
                                if (event.startDateTime < event.maxDateForRepeat) {
                                    id = eventDao.addEvent(event)
                                    val calendar1 = Calendar.getInstance()
                                    calendar1.timeInMillis = event.startDateTime
                                    val calendar2 = Calendar.getInstance()
                                    calendar2.timeInMillis = event.endDateTime
                                    if (event.repeat == EventRepetition.DAILY.toString()) {
                                        do {
                                            calendar1.add(Calendar.DAY_OF_MONTH, 1)
                                            calendar2.add(Calendar.DAY_OF_MONTH, 1)
                                            val num = eventDao.addRepeat(
                                                copyWithNewDates(
                                                    event,
                                                    calendar1.timeInMillis,
                                                    calendar2.timeInMillis,
                                                )
                                            )
                                        } while (num != 0)
                                    } else if (event.repeat == EventRepetition.WEEKLY.toString() && event.startDateTime < event.maxDateForRepeat) {
                                        do {
                                            calendar1.add(Calendar.DAY_OF_MONTH, 7)
                                            calendar2.add(Calendar.DAY_OF_MONTH, 7)
                                            val num = eventDao.addRepeat(
                                                copyWithNewDates(
                                                    event,
                                                    calendar1.timeInMillis,
                                                    calendar2.timeInMillis,
                                                )
                                            )
                                        } while (num != 0)
                                    } else if (event.repeat == EventRepetition.MONTHLY.toString() && event.startDateTime < event.maxDateForRepeat) {
                                        do {
                                            calendar1.add(Calendar.MONTH, 1)
                                            calendar2.add(Calendar.MONTH, 1)
                                            val num = eventDao.addRepeat(
                                                copyWithNewDates(
                                                    event,
                                                    calendar1.timeInMillis,
                                                    calendar2.timeInMillis,
                                                )
                                            )
                                        } while (num != 0)
                                    } else if (event.repeat == EventRepetition.ANNUALLY.toString() && event.startDateTime < event.maxDateForRepeat) {
                                        do {
                                            calendar1.add(Calendar.YEAR, 1)
                                            calendar2.add(Calendar.YEAR, 1)
                                            val num = eventDao.addRepeat(
                                                copyWithNewDates(
                                                    event,
                                                    calendar1.timeInMillis,
                                                    calendar2.timeInMillis,
                                                )
                                            )
                                        } while (num != 0)
                                    }
                                }
                            }
                        }
                    }
                }
                return id
            } else if (updateNext) {
                withContext(Dispatchers.IO) {
                    if (event.maxDateForRepeat != null) {
                        if (prevEvent.repeat == event.repeat && event.repeat !=
                            EventRepetition.NONE.toString() && event.repeatParentId != null
                        ) {
                            for (elem in eventDao.getEventsByParentId(event.repeatParentId)) {
                                if (event.startDateTime <= elem.startDateTime) update(elem)
                            }
                        } else {
                            deleteAllRepeatedExceptThis(event)
                            withContext(Dispatchers.IO) {
                                if (event.startDateTime < event.maxDateForRepeat) {
                                    id = eventDao.addEvent(event)
                                    val calendar1 = Calendar.getInstance()
                                    calendar1.timeInMillis = event.startDateTime
                                    val calendar2 = Calendar.getInstance()
                                    calendar2.timeInMillis = event.endDateTime
                                    if (event.repeat == EventRepetition.DAILY.toString()) {
                                        do {
                                            calendar1.add(Calendar.DAY_OF_MONTH, 1)
                                            calendar2.add(Calendar.DAY_OF_MONTH, 1)
                                            val num = eventDao.addRepeat(
                                                copyWithNewDates(
                                                    event,
                                                    calendar1.timeInMillis,
                                                    calendar2.timeInMillis,
                                                )
                                            )
                                        } while (num != 0)
                                    } else if (event.repeat == EventRepetition.WEEKLY.toString() && event.startDateTime < event.maxDateForRepeat) {
                                        do {
                                            calendar1.add(Calendar.DAY_OF_MONTH, 7)
                                            calendar2.add(Calendar.DAY_OF_MONTH, 7)
                                            val num = eventDao.addRepeat(
                                                copyWithNewDates(
                                                    event,
                                                    calendar1.timeInMillis,
                                                    calendar2.timeInMillis,
                                                )
                                            )
                                        } while (num != 0)
                                    } else if (event.repeat == EventRepetition.MONTHLY.toString() && event.startDateTime < event.maxDateForRepeat) {
                                        do {
                                            calendar1.add(Calendar.MONTH, 1)
                                            calendar2.add(Calendar.MONTH, 1)
                                            val num = eventDao.addRepeat(
                                                copyWithNewDates(
                                                    event,
                                                    calendar1.timeInMillis,
                                                    calendar2.timeInMillis,
                                                )
                                            )
                                        } while (num != 0)
                                    } else if (event.repeat == EventRepetition.ANNUALLY.toString() && event.startDateTime < event.maxDateForRepeat) {
                                        do {
                                            calendar1.add(Calendar.YEAR, 1)
                                            calendar2.add(Calendar.YEAR, 1)
                                            val num = eventDao.addRepeat(
                                                copyWithNewDates(
                                                    event,
                                                    calendar1.timeInMillis,
                                                    calendar2.timeInMillis,
                                                )
                                            )
                                        } while (num != 0)
                                    }
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
            id = eventDao.updateEvent(event)
            return id
        } else if (event.maxDateForRepeat != null && prevEvent.repeat == EventRepetition.NONE.toString()){
            withContext(Dispatchers.IO) {
                if (event.startDateTime < event.maxDateForRepeat) {
                    id = eventDao.addEvent(event)
                    val calendar1 = Calendar.getInstance()
                    calendar1.timeInMillis = event.startDateTime
                    val calendar2 = Calendar.getInstance()
                    calendar2.timeInMillis = event.endDateTime
                    if (event.repeat == EventRepetition.DAILY.toString()) {
                        do {
                            calendar1.add(Calendar.DAY_OF_MONTH, 1)
                            calendar2.add(Calendar.DAY_OF_MONTH, 1)
                            val num = eventDao.addRepeat(
                                copyWithNewDates(
                                    event,
                                    calendar1.timeInMillis,
                                    calendar2.timeInMillis,
                                )
                            )
                        } while (num != 0)
                    } else if (event.repeat == EventRepetition.WEEKLY.toString() && event.startDateTime < event.maxDateForRepeat) {
                        do {
                            calendar1.add(Calendar.DAY_OF_MONTH, 7)
                            calendar2.add(Calendar.DAY_OF_MONTH, 7)
                            val num = eventDao.addRepeat(
                                copyWithNewDates(
                                    event,
                                    calendar1.timeInMillis,
                                    calendar2.timeInMillis,
                                )
                            )
                        } while (num != 0)
                    } else if (event.repeat == EventRepetition.MONTHLY.toString() && event.startDateTime < event.maxDateForRepeat) {
                        do {
                            calendar1.add(Calendar.MONTH, 1)
                            calendar2.add(Calendar.MONTH, 1)
                            val num = eventDao.addRepeat(
                                copyWithNewDates(
                                    event,
                                    calendar1.timeInMillis,
                                    calendar2.timeInMillis,
                                )
                            )
                        } while (num != 0)
                    } else if (event.repeat == EventRepetition.ANNUALLY.toString() && event.startDateTime < event.maxDateForRepeat) {
                        do {
                            calendar1.add(Calendar.YEAR, 1)
                            calendar2.add(Calendar.YEAR, 1)
                            val num = eventDao.addRepeat(
                                copyWithNewDates(
                                    event,
                                    calendar1.timeInMillis,
                                    calendar2.timeInMillis,
                                )
                            )
                        } while (num != 0)
                    }
                }
            }
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

fun copyWithNewDates(event: Event, startDate: Long, endDate: Long): Event {
    return event.copy(
        id = 0, // Призначаємо 0, щоб зберегти як новий запис в базі даних
        startDateTime = startDate,
        endDateTime = endDate
    )
}