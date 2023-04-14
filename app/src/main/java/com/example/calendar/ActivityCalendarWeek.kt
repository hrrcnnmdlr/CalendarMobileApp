package com.example.calendar

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import com.example.calendar.databinding.ActivityCalendarWeekBinding
import java.util.*

class ActivityCalendarWeek : AppCompatActivity() {
    lateinit var binding: ActivityCalendarWeekBinding
    val week =  listOf(
        "Su",
        "Mo",
        "Tu",
        "Wed",
        "Th",
        "Fr",
        "Sat"
    )
    val months = listOf(
        "January",
        "February",
        "March",
        "April",
        "May",
        "June",
        "July",
        "August",
        "September",
        "October",
        "November",
        "December"
    )
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCalendarWeekBinding.inflate(layoutInflater)
        val dateInMillis = intent.getLongExtra("date", -1)
        val calendar = Calendar.getInstance()
        if (dateInMillis != -1L) {
            calendar.timeInMillis = dateInMillis
        }
        val day = calendar.get(Calendar.DAY_OF_MONTH)
        val dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK)
        binding.month.text = months[calendar.get(Calendar.MONTH)]
        binding.year.text = calendar.get(Calendar.YEAR).toString()
        binding.dayOfWeek01.text = week[if (dayOfWeek < 4){dayOfWeek+7-4}else {dayOfWeek - 4}]
        binding.dayOfWeek02.text = week[if (dayOfWeek < 3){dayOfWeek+7-3}else {dayOfWeek - 3}]
        binding.dayOfWeek03.text = week[if (dayOfWeek < 2){dayOfWeek+7-2}else {dayOfWeek - 2}]
        binding.dayOfWeek04.text = week[dayOfWeek - 1]
        binding.dayOfWeek05.text = week[if (dayOfWeek > 6){dayOfWeek-7}else {dayOfWeek}]
        binding.dayOfWeek06.text = week[if (dayOfWeek > 5){dayOfWeek-7+1}else {dayOfWeek + 1}]
        binding.dayOfWeek07.text = week[if (dayOfWeek > 4){dayOfWeek-7+2}else {dayOfWeek + 2}]
        calendar.add(Calendar.DAY_OF_MONTH, -3)
        var nextDay = calendar.get(Calendar.DAY_OF_MONTH)
        binding.date01.text = nextDay.toString()
        calendar.add(Calendar.DAY_OF_MONTH, 1)
        nextDay = calendar.get(Calendar.DAY_OF_MONTH)
        binding.date02.text = nextDay.toString()
        calendar.add(Calendar.DAY_OF_MONTH, 1)
        nextDay = calendar.get(Calendar.DAY_OF_MONTH)
        binding.date03.text = nextDay.toString()
        binding.date04.text = day.toString()
        calendar.add(Calendar.DAY_OF_MONTH, 2)
        nextDay = calendar.get(Calendar.DAY_OF_MONTH)
        binding.date05.text = nextDay.toString()
        calendar.add(Calendar.DAY_OF_MONTH, 1)
        nextDay = calendar.get(Calendar.DAY_OF_MONTH)
        binding.date06.text = nextDay.toString()
        calendar.add(Calendar.DAY_OF_MONTH, 1)
        nextDay = calendar.get(Calendar.DAY_OF_MONTH)
        binding.date07.text = nextDay.toString()
        setContentView(binding.root)
    }

    fun openDays(view: View) {
        val dayIntent = Intent(this, ActivityCalendarDay::class.java)
        val date = intent.getLongExtra("date", -1)
        dayIntent.putExtra("date", date)
        startActivity(dayIntent)
    }

    fun openWeeks(view: View) {
        val weekIntent = Intent(this, ActivityCalendarWeek::class.java)
        val date = intent.getLongExtra("date", -1)
        weekIntent.putExtra("date", date)
        startActivity(weekIntent)
    }

    fun openMonths(view: View) {
        val monthIntent = Intent(this, ActivityCalendarMonth::class.java)
        val date = intent.getLongExtra("date", -1)
        monthIntent.putExtra("date", date)
        startActivity(monthIntent)
    }

    fun createEvent(view: View) {
        val eventIntent = Intent(this, ActivityCalendarAddEvent::class.java)
        startActivity(eventIntent)
    }

    fun day1(view: View) {
        changeWeek(-3)
    }

    fun day2(view: View) {
        changeWeek(-2)
    }

    fun day3(view: View) {
        changeWeek(-1)
    }

    fun day5(view: View) {
        changeWeek(1)
    }

    fun day6(view: View) {
        changeWeek(2)
    }

    fun day7(view: View) {
        changeWeek(3)
    }

    private fun changeWeek(numberOfDay: Int) {
        val weekIntent = Intent(this, ActivityCalendarWeek::class.java)
        val date = intent.getLongExtra("date", -1)
        weekIntent.putExtra("date", date + (86400000 * numberOfDay))
        startActivity(weekIntent)
    }
}