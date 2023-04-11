package com.example.calendar
import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database (entities = [Event::class], version = 1)
abstract class MainDB : RoomDatabase() {
    companion object {
        fun getDatabase(context: Context) : MainDB{
            return Room.databaseBuilder(context.applicationContext,
                MainDB::class.java, name = "calendar.db").build()
        }
    }
    abstract fun getDao(): EventDao
}