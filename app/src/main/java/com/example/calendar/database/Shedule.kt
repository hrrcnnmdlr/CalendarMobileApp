package com.example.calendar.database

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "schedule")
data class Schedule(
    // Назва події
    @PrimaryKey @ColumnInfo(name = "eventId") val eventId: Int,
    @ColumnInfo(name = "classNumber") val classNumber: Int,
    @ColumnInfo(name = "homework") val homework: String? = null,
    @ColumnInfo(name = "attendance") val attendance: Boolean? = null,
    @ColumnInfo(name = "completedHomework") val completedHomework: Boolean? = null,
    @ColumnInfo(name = "obtainedGrade") val obtainedGrade: Float? = null
)

