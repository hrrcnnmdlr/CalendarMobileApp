package com.example.calendar

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

// Анотація @Database означає, що клас MainDB є базою даних, яка містить таблицю events.
@Database(entities = [Event::class], version = 1)
abstract class MainDB : RoomDatabase() {

    // Статичний метод companion object використовується для забезпечення доступу до бази даних.
    // Цей метод створює та повертає інстанс бази даних MainDB.
    companion object {
        fun getDatabase(context: Context): MainDB {
            return Room.databaseBuilder(
                context.applicationContext,
                MainDB::class.java,
                name = "calendar.db"
            ).build()
        }
    }

    // Абстрактний метод, який повертає об'єкт EventDao для виконання запитів до таблиці events.
    abstract fun getDao(): EventDao
}