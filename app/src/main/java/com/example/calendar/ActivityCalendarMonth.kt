package com.example.calendar

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.CalendarView
import com.example.calendar.databinding.ActivityCalendarMonthBinding
import java.text.SimpleDateFormat
import java.util.*

class ActivityCalendarMonth : AppCompatActivity() {
    lateinit var binding: ActivityCalendarMonthBinding

    var selectedDate: Long = 0L

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCalendarMonthBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val calendarView = findViewById<CalendarView>(R.id.calendarView)
        calendarView.setOnDateChangeListener { _, year: Int, month: Int, dayOfMonth: Int ->
            val calendar = Calendar.getInstance()
            calendar.set(year, month, dayOfMonth)
            selectedDate = calendar.timeInMillis
        }
        selectedDate = intent.getLongExtra("date", calendarView.date)
        calendarView.date = selectedDate
        setContentView(binding.root)
    }
    fun openWeeks(view: View) {
        val weekIntent = Intent(this, ActivityCalendarWeek::class.java)
        var date = selectedDate
        if (selectedDate == 0L) {date = binding.calendarView.date}
        weekIntent.putExtra("date", date)
        startActivity(weekIntent)
    }

    fun openDays(view: View) {
        val dayIntent = Intent(this, ActivityCalendarDay::class.java)
        var date = selectedDate
        if (selectedDate == 0L) {date = binding.calendarView.date}
        dayIntent.putExtra("date", date)
        // dayIntent.putExtra("key", value)
        // val value = intent.getStringExtra("key")
        startActivity(dayIntent)
    }

    fun createEvent(view: View) {
        val eventIntent = Intent(this, ActivityCalendarAddEvent::class.java)
        startActivity(eventIntent)
    }
}


