package com.example.calendar

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import com.example.calendar.databinding.ActivityCalendarDayBinding

class ActivityCalendarDay : AppCompatActivity() {
    lateinit var binding: ActivityCalendarDayBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCalendarDayBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }

    fun openWeeks(view: View) {
        val weekIntent = Intent(this, ActivityCalendarWeek::class.java)
        startActivity(weekIntent)
    }

    fun openDays(view: View) {
        val dayIntent = Intent(this, ActivityCalendarDay::class.java)
        startActivity(dayIntent)
    }

    fun openMonths(view: View) {
        val monthIntent = Intent(this, ActivityCalendarMonth::class.java)
        startActivity(monthIntent)
    }

    fun createEvent(view: View) {
        val eventIntent = Intent(this, ActivityCalendarAddEvent::class.java)
        startActivity(eventIntent)
    }
}