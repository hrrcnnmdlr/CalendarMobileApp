package com.example.calendar

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "events")
data class Event(
    // Унікальний ідентифікатор події, який буде генеруватися автоматично
    @PrimaryKey(autoGenerate = true) val id: Int = 0,

    // Назва події
    @ColumnInfo(name = "eventName") val eventName: String,

    // Опис події
    @ColumnInfo(name = "description") val description: String,

    // Дата та час початку події (у мілісекундах з 1970 року)
    @ColumnInfo(name = "startDateTime") val startDateTime: Long,

    // Дата та час завершення події (у мілісекундах з 1970 року)
    @ColumnInfo(name = "endDateTime") val endDateTime: Long,

    // Місце проведення події
    @ColumnInfo(name = "location") val location: String,

    // Категорія події
    @ColumnInfo(name = "category") val category: String,

    // Повторення події (наприклад, щоденно або щотижня)
    @ColumnInfo(name = "repeat") val repeat: String,

    // Нагадування про подію (наприклад, за 30 хвилин до початку події)
    @ColumnInfo(name = "reminder") val reminder: String
)