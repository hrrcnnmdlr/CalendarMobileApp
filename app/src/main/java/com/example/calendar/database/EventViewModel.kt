package com.example.calendar.database

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import androidx.preference.PreferenceManager
import com.example.calendar.fragments.eventId
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.*

class EventViewModel(application: Application) : AndroidViewModel(application) {
    private val repository: EventRepository
    private val categoryRepository: CategoryRepository
    private val scheduleRepository: ScheduleRepository
    private val allEvents: LiveData<List<Event>>
    private val application: Application

    init {
        this.application = application
        val database = MainDB.getDatabase(application)
        val eventsDao = database.getDao()
        val categoryDao = database.categoryDao()
        val scheduleDao = database.scheduleDao()
        repository = EventRepository(eventsDao)
        categoryRepository = CategoryRepository(categoryDao)
        scheduleRepository = ScheduleRepository(scheduleDao)
        allEvents = repository.allEvents
    }

    suspend fun insert(event: Event): Int {
        return repository.insert(event)
    }
    suspend fun insert(event: Event, maxDate: Long): Int {
        return repository.insert(copyWithDate(event, maxDate))
    }

    private fun copyWithDate(event: Event, maxDate: Long): Event {
        return event.copy(
            maxDateForRepeat = maxDate
        )
    }

    suspend fun update(event: Event, updateAll: Boolean = false, updateNext: Boolean = false, updateOne: Boolean = false): Int {
        Log.d("UPDATEEVENT! 1", "$event")
        return repository.update(event, updateAll, updateNext, updateOne)
    }

    suspend fun update(event: Event, maxDate: Long, updateAll: Boolean = false, updateNext: Boolean = false, updateOne: Boolean = false): Int {
        Log.d("UPDATEEVENT! 2", "$event")
        return repository.update(copyWithDate(event, maxDate), updateAll, updateNext, updateOne)
    }

    suspend fun delete(event: Event): Int {
        Log.d("ID", event.id.toString())
        return repository.delete(event)
    }

    suspend fun deleteAllRepeated(event: Event): List<Int> {
        return repository.deleteAllRepeated(event)
    }

    suspend fun deleteAllNext(event: Event): List<Int> {
        return repository.deleteAllNextRepeated(event)
    }


    fun getEventById(id: Int): Event {
        return repository.getEventById(id)
    }

    fun getEventsForDay(date: Long): LiveData<List<Event>> {
        return repository.getEventsForDay(date)
    }

    fun getScheduleEvent(startDateTime: Long, endDateTime: Long, categoryId: Int=2): LiveData<List<Event>> {
        return repository.getScheduleEvent(startDateTime, endDateTime, categoryId)
    }


    fun insertCategory(category: Category) = viewModelScope.launch(Dispatchers.IO) {
        categoryRepository.insertCategory(category)
    }

    fun updateCategory(category: Category) = viewModelScope.launch(Dispatchers.IO) {
        categoryRepository.updateCategory(category)
    }

    fun deleteCategory(category: Category) = viewModelScope.launch(Dispatchers.IO) {
        categoryRepository.deleteCategory(category)
    }

    suspend fun getCategoryById(id: Int): Category? {
        return categoryRepository.getCategoryById(id)
    }

    fun insertClass(schedule: Schedule, event: Event) = viewModelScope.launch(Dispatchers.IO) {
        var currentEvent = event // Створення копії об'єкту event
        val parentEventId = repository.insertForClass(currentEvent)
        scheduleRepository.insertClass(schedule.copy(eventId = parentEventId))
        var eventId = parentEventId
        val sharedPrefs = PreferenceManager.getDefaultSharedPreferences(application.applicationContext)
        val numOfWeek = sharedPrefs.getString("weeks_preference", "1")?.toInt()
        while (true) {
            val calendar1 = Calendar.getInstance()
            calendar1.timeInMillis = currentEvent.startDateTime
            calendar1.add(Calendar.DAY_OF_MONTH, 7 * numOfWeek!!)
            val calendar2 = Calendar.getInstance()
            calendar2.timeInMillis = currentEvent.endDateTime
            calendar2.add(Calendar.DAY_OF_MONTH, 7 * numOfWeek)
            if (calendar1.timeInMillis > currentEvent.maxDateForRepeat!!) {
                break
            }
            val newSchedule = copyWithNewIdSchedule(schedule, eventId)
            val newEvent = copyWithNewDatesSchedule(repository.getEventById(eventId),
                currentEvent.startDateTime, currentEvent.endDateTime).copy(repeatParentId = parentEventId)
            eventId = repository.insertForClass(newEvent)
            scheduleRepository.insertClass(newSchedule)
            currentEvent = newEvent // Оновлення копії об'єкту currentEvent
        }
    }

    private fun copyWithNewDatesSchedule(event: Event, startDate: Long, endDate: Long): Event {
        return event.copy(
            id = 0, // Призначаємо 0, щоб зберегти як новий запис в базі даних
            startDateTime = startDate,
            endDateTime = endDate
        )
    }

    fun insertUniqueClass(schedule: Schedule, event: Event) = viewModelScope.launch(Dispatchers.IO) {
        val id = repository.insertForClass(event)
        scheduleRepository.insertClass(schedule.copy(eventId = id))
    }

    fun deleteClass(schedule: Schedule, event: Event) = viewModelScope.launch(Dispatchers.IO) {
        scheduleRepository.deleteClass(schedule)
        repository.deleteForClass(event)
    }

    fun deleteAllRepeatedClasses(event: Event) = viewModelScope.launch(Dispatchers.IO) {
        val ids = repository.deleteAllRepeated(event)
        for (id in ids)
            scheduleRepository.getClassById(id).let { scheduleRepository.deleteClass(it) }
    }

    fun deleteAllNextClasses(event: Event) = viewModelScope.launch(Dispatchers.IO) {
        val ids = repository.deleteAllNextRepeated(event)
        for (id in ids)
            scheduleRepository.getClassById(id).let { scheduleRepository.deleteClass(it) }
    }

    fun updateClass(schedule: Schedule, event: Event) = viewModelScope.launch(Dispatchers.IO) {
        scheduleRepository.updateClass(schedule)
        repository.updateForClass(event)
    }

    fun updateAllNextClasses(event: Event) = viewModelScope.launch(Dispatchers.IO) {
        val events = event.repeatParentId?.let { repository.getEventsByParentId(it) }
        if (events != null) {
            for (element in events){
                if (event.startDateTime <= element.startDateTime){
                    repository.updateForClass(element)
                    scheduleRepository.getClassById(element.id)
                        .let { scheduleRepository.updateClass(it) }
                }
            }
        }
    }

    fun updateAllRepeatedClasses(event: Event) = viewModelScope.launch(Dispatchers.IO) {
        val events = event.repeatParentId?.let { repository.getEventsByParentId(it) }
        if (events != null) {
            for (element in events) {
                repository.updateForClass(element)
                scheduleRepository.getClassById(element.id)
                    .let { scheduleRepository.updateClass(it) }
            }
        }
    }

    suspend fun getClassById(id: Int): Schedule {
        return scheduleRepository.getClassById(id)
    }
}

fun copyWithNewIdSchedule(schedule: Schedule, id: Int): Schedule {
    return schedule.copy(
        eventId = id
    )
}