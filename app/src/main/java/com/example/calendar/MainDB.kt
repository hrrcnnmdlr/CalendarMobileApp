package com.example.calendar

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

// Анотація @Database означає, що клас MainDB є базою даних, яка містить таблицю events.
@Database(entities = [Event::class], version = 2)
abstract class MainDB : RoomDatabase() {

    // Статичний метод companion object використовується для забезпечення доступу до бази даних.
    // Цей метод створює та повертає інстанс бази даних MainDB.
    companion object {
        fun getDatabase(context: Context): MainDB {
            return Room.databaseBuilder(
                context.applicationContext,
                MainDB::class.java,
                name = "calendar.db"
            ).addMigrations(migration1to2)
                .build()
        }
    }

    // Абстрактний метод, який повертає об'єкт EventDao для виконання запитів до таблиці events.
    abstract fun getDao(): EventDao
}

val migration1to2 = object : Migration(1, 2) {
    override fun migrate(database: SupportSQLiteDatabase) {
    }
}