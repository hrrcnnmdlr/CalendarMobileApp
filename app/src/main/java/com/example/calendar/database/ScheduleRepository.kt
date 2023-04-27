package com.example.calendar.database

import androidx.lifecycle.LiveData
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class ScheduleRepository(private val scheduleDao: ScheduleDao) {
    val allClasses: LiveData<List<Schedule>> = scheduleDao.getAllClasses()

    suspend fun insertClass(schedule : Schedule): Long {
        return scheduleDao.insertClass(schedule)
    }


    suspend fun updateClass(schedule : Schedule): Int {
        return scheduleDao.updateClass(schedule)
    }

    suspend fun deleteClass(schedule : Schedule): Int {
        return scheduleDao.deleteClass(schedule)
    }

    suspend fun deleteAllClasses() {
        scheduleDao.deleteClasses()
    }

    suspend fun getClassById(id: Int): Schedule {
        return withContext(Dispatchers.IO) {
            scheduleDao.getClassById(id)
        }
    }
}