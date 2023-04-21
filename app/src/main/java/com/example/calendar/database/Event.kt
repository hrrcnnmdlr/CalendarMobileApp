package com.example.calendar.database

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
    @ColumnInfo(name = "category_id") val categoryId: Int,
    // Повторення події (наприклад, щоденно або щотижня)
    // Нагадування про подію (наприклад, за 30 хвилин до початку події)
    @ColumnInfo(name = "remind_5_minutes_before") var remind5MinutesBefore: Boolean = false,
    @ColumnInfo(name = "remind_15_minutes_before")  var remind15MinutesBefore: Boolean = false,
    @ColumnInfo(name = "remind_30_minutes_before") var remind30MinutesBefore: Boolean = false,
    @ColumnInfo(name = "remind_1_hour_before") var remind1HourBefore: Boolean = false,
    @ColumnInfo(name = "remind_1_day_before") var remind1DayBefore: Boolean = false,
    @ColumnInfo(name = "repeat") var repeat: String = EventRepetition.NONE.toString(),
    @ColumnInfo(name = "repeatParentId") val repeatParentId: Int? = null,
    @ColumnInfo(name = "maxDateForRepeat") val maxDateForRepeat: Long? = null
)

enum class EventRepetition {
    NONE,
    DAILY,
    WEEKLY,
    MONTHLY,
    ANNUALLY
}