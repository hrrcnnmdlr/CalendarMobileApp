package com.example.calendar

import androidx.room.*
import kotlinx.coroutines.flow.Flow

// Оголошуємо інтерфейс EventDao, який відповідає за доступ до даних таблиці events у базі даних
@Dao
interface EventDao {
    // Оголошуємо метод для отримання всіх подій з бази даних за допомогою SQL запиту
    // Застосовуємо Flow, щоб отримувати зміни в режимі реального часу
    @Query("SELECT * FROM events")
    fun getAllEvents(): List<Event>

    // Оголошуємо метод для отримання події за її ідентифікатором
    @Query("SELECT * FROM events WHERE id = :id")
    fun getEventById(id: Int): Event

    // Оголошуємо метод для отримання всіх подій, які відбуваються в задану дату
    // date - дата в мілісекундах, починаючи з 1 січня 1970 року
    // 86400000 - кількість мілісекунд у 1 добі
    @Query("SELECT * FROM events WHERE startDateTime >= :date AND startDateTime < :date + 86400000 OR endDateTime >= :date AND endDateTime < :date + 86400000")
    fun getEventsForDay(date: Long): List<Event>

    // Оголошуємо метод для додавання нової події в базу даних
    // Якщо вже існує подія з таким же ідентифікатором, то замінюємо її на нову
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun addEvent(event: Event)

    // Оголошуємо метод для видалення події з бази даних
    @Delete
    fun deleteEvent(event: Event)

    // Оголошуємо метод для заміни події з бази даних з новими даними
    @Update
    fun updateEvent(event: Event)
}