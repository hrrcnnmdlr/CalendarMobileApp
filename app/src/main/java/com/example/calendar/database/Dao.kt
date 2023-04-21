package com.example.calendar.database

import androidx.lifecycle.LiveData
import androidx.room.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.util.*

// Оголошуємо інтерфейс EventDao, який відповідає за доступ до даних таблиці events у базі даних
@Dao
interface EventDao {
    // Оголошуємо метод для отримання всіх подій з бази даних за допомогою SQL запиту
    // Застосовуємо Flow, щоб отримувати зміни в режимі реального часу
    @Query("SELECT * FROM events")
    fun getAllEvents(): LiveData<List<Event>>

    // Оголошуємо метод для отримання події за її ідентифікатором
    @Query("SELECT * FROM events WHERE id = :id")
    fun getEventById(id: Int): Event

    // Оголошуємо метод для отримання всіх подій, які відбуваються в задану дату
    // date - дата в мілісекундах, починаючи з 1 січня 1970 року
    // 86400000 - кількість мілісекунд у 1 добі
    @Query("SELECT * FROM events WHERE startDateTime >= :date AND startDateTime < :date + 86400000 OR endDateTime >= :date AND endDateTime < :date + 86400000")
    fun getEventsForDay(date: Long): LiveData<List<Event>>

    // Оголошуємо метод для додавання нової події в базу даних
    // Якщо вже існує подія з таким же ідентифікатором, то замінюємо її на нову
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun add(event: Event)

    suspend fun addEvent(event: Event): Int {
        val id = event.id
        withContext(Dispatchers.Default) {
            add(event)
        }
        return id
    }

    // Оголошуємо метод для видалення події з бази даних
    @Delete
    fun delete(event: Event)
    suspend fun deleteEvent(event: Event): Int {
        val id = event.id
        withContext(Dispatchers.Default) {
            delete(event)
        }
        return id
    }

    suspend fun addRepeat(event: Event): Int {
        if (event.maxDateForRepeat != null) {
            if (event.startDateTime < event.maxDateForRepeat) {
                return addEvent(event)
            }
        }
        return 0
    }

    suspend fun updateRepeat(event: Event): Int {
        if (event.maxDateForRepeat != null) {
            if (event.startDateTime < event.maxDateForRepeat){
                    return updateEvent(event)
            }
        }
        return 0
    }

    suspend fun deleteRepeat(event: Event): Int {
        if (event.maxDateForRepeat != null) {
            if (event.startDateTime < event.maxDateForRepeat){
                return deleteEvent(event)
            }
        }
        return 0
    }


    // Оголошуємо метод для заміни події з бази даних з новими даними
    @Update
    fun update(event: Event)

    @Update
    suspend fun updateEvent(event: Event) : Int {
        val id = event.id
        withContext(Dispatchers.Default) {
            update(event)
        }
        return id
    }

    @Query("SELECT * FROM events WHERE repeatParentId = :id")
    fun getEventsByParentId(id: Int): List<Event>
}



@Dao
interface CategoryDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCategory(category: Category)

    @Query("SELECT * FROM categories")
    fun getAllCategories(): LiveData<List<Category>>

    @Query("SELECT * FROM categories WHERE id = :id")
    fun getCategoryById(id: Int): Category

    @Update
    suspend fun updateCategory(category: Category)

    @Delete
    suspend fun deleteCategory(category: Category)

    @Query("DELETE FROM categories")
    suspend fun deleteAllCategories()
}

@Dao
interface ScheduleDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertClass(schedule : Schedule) : Int = schedule.eventId

    @Query("SELECT * FROM schedule")
    fun getAllClasses(): LiveData<List<Schedule>>

    @Query("SELECT * FROM schedule WHERE eventId = :id")
    fun getClassById(id: Int): Schedule

    @Update
    suspend fun updateClass(schedule : Schedule) : Int = schedule.eventId

    @Delete
    suspend fun deleteClass(schedule : Schedule) : Int = schedule.eventId

    @Query("DELETE FROM schedule")
    suspend fun deleteClasses()
}