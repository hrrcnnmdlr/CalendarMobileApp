package com.example.calendar

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase


// Анотація @Database означає, що клас MainDB є базою даних, яка містить таблицю events.
@Database(entities = [Event::class, Category::class], version = 3)
abstract class MainDB : RoomDatabase() {

    // Статичний метод companion object використовується для забезпечення доступу до бази даних.
    // Цей метод створює та повертає інстанс бази даних MainDB.
    companion object {
        private var instance: MainDB? = null
        fun getDatabase(context: Context): MainDB {
            return instance ?: synchronized(this) {
                instance ?: Room.databaseBuilder(
                    context.applicationContext,
                    MainDB::class.java,
                    name = "calendar.db"
                ).addMigrations(migration1to2, migration2to3)
                    .build().also { instance = it }
            }
        }
        private val migration1to2 = object : Migration(1, 2) {
            override fun migrate(database: SupportSQLiteDatabase) {

            }
        }
        private val migration2to3 = object : Migration(2, 3) {
            override fun migrate(database: SupportSQLiteDatabase) {
                database.execSQL("CREATE TABLE IF NOT EXISTS `categories` (`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `name` TEXT NOT NULL)")
                database.execSQL("INSERT INTO categories (name) VALUES ('Business'), ('Education'), ('Entertainment'), ('Food and Drink'), ('Health and Wellness'), ('Hobbies'), ('Music'), ('Networking'), ('Sports'), ('Technology'), ('Travel'), ('Other')")
                database.execSQL("CREATE TABLE events_new (id INTEGER PRIMARY KEY NOT NULL, eventName TEXT NOT NULL, description TEXT NOT NULL, startDateTime INTEGER NOT NULL, endDateTime INTEGER NOT NULL, location TEXT NOT NULL, category_id INTEGER NOT NULL DEFAULT 1, repeat TEXT NOT NULL, reminder TEXT NOT NULL)")
                database.execSQL("INSERT INTO events_new (id, eventName, description, startDateTime, endDateTime, location, repeat, reminder) SELECT id, eventName, description, startDateTime, endDateTime, location, repeat, reminder FROM events")
                database.execSQL("DROP TABLE events")
                database.execSQL("ALTER TABLE events_new RENAME TO events")
                /*
                database.execSQL("CREATE TABLE IF NOT EXISTS `categories` (`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `name` TEXT NOT NULL)")
                database.execSQL("INSERT INTO categories (name) VALUES ('Business'), ('Education'), ('Entertainment'), ('Food and Drink'), ('Health and Wellness'), ('Hobbies'), ('Music'), ('Networking'), ('Sports'), ('Technology'), ('Travel'), ('Other')")
                database.execSQL("ALTER TABLE `events` RENAME TO `events_temp`")
                database.execSQL("CREATE TABLE IF NOT EXISTS `events` (`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `eventName` TEXT NOT NULL, `description` TEXT NOT NULL, `startDateTime` INTEGER NOT NULL, `endDateTime` INTEGER NOT NULL, `location` TEXT NOT NULL, `repeat` TEXT NOT NULL, `reminder` TEXT NOT NULL, `category_id` INTEGER NOT NULL DEFAULT 1, FOREIGN KEY(`category_id`) REFERENCES `categories`(`id`) ON UPDATE NO ACTION ON DELETE CASCADE)")
                database.execSQL("INSERT INTO `events` (`id`, `eventName`, `description`, `startDateTime`, `endDateTime`, `location`, `repeat`, `reminder`) SELECT `id`, `eventName`, `description`, `startDateTime`, `endDateTime`, `location`, `repeat`, `reminder` FROM `events_temp`")
                database.execSQL("DROP TABLE `events_temp`")
                */
            }
        }
    }

    // Абстрактний метод, який повертає об'єкт EventDao для виконання запитів до таблиці events.
    abstract fun getDao(): EventDao

    abstract fun categoryDao(): CategoryDao

}