package com.example.calendar

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "events")
data class Event(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    @ColumnInfo(name = "eventName")
    val eventName: String,
    @ColumnInfo(name = "description")
    val description: String,
    @ColumnInfo(name = "startDateTime")
    val startDateTime: Long,
    @ColumnInfo(name = "endDateTime")
    val endDateTime: Long,
    @ColumnInfo(name = "location")
    val location: String,
    @ColumnInfo(name = "category")
    val category: String,
    @ColumnInfo(name = "repeat")
    val repeat: String,
    @ColumnInfo(name = "reminder")
    val reminder: String
)