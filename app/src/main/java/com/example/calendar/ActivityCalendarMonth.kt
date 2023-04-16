package com.example.calendar

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.CalendarView
import com.example.calendar.databinding.ActivityCalendarMonthBinding
import java.util.*

class ActivityCalendarMonth : AppCompatActivity() {
    // Поле зі зв'язкою до елементів UI через ViewBinding
    lateinit var binding: ActivityCalendarMonthBinding

    // Початкова дата - 0, щоб при запуску вибрати поточну дату
    var selectedDate: Long = 0L

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Ініціалізація зв'язки з елементами UI через ViewBinding
        binding = ActivityCalendarMonthBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Знаходження календарного віджету в макеті та встановлення слухача подій на зміну дати
        val calendarView = findViewById<CalendarView>(R.id.calendarView)
        calendarView.setOnDateChangeListener { _, year: Int, month: Int, dayOfMonth: Int ->
            // Створення об'єкту календаря із вибраною датою
            val calendar = Calendar.getInstance()
            calendar.set(year, month, dayOfMonth)
            // Збереження вибраної дати в поле класу
            selectedDate = calendar.timeInMillis
        }

        // Встановлення вибраної дати, якщо вона передана із попередньої активності
        selectedDate = intent.getLongExtra("date", calendarView.date)
        calendarView.date = selectedDate
        setContentView(binding.root)
    }

    // Функція для переходу на активність з відображенням тижня
    fun openWeeks(view: View) {
        val weekIntent = Intent(this, ActivityCalendarWeek::class.java)
        var date = selectedDate
        if (selectedDate == 0L) {date = binding.calendarView.date}
        weekIntent.putExtra("date", date)
        startActivity(weekIntent)
    }

    // Функція для переходу на активність з відображенням дня
    fun openDays(view: View) {
        val dayIntent = Intent(this, ActivityCalendarDay::class.java)
        var date = selectedDate
        if (selectedDate == 0L) {date = binding.calendarView.date}
        dayIntent.putExtra("date", date)
        startActivity(dayIntent)
    }

    // Функція для переходу на активність з відображенням місяця
    fun openMonths(view: View) {
        val monthIntent = Intent(this, ActivityCalendarMonth::class.java)
        var date = selectedDate
        if (selectedDate == 0L) {date = binding.calendarView.date}
        monthIntent.putExtra("date", date)
        startActivity(monthIntent)
    }

    // Метод, який змінює відображуваний тиждень та запускає ActivityCalendarWeek зі зміненим датою.
    fun createEvent(view: View) {
        val eventIntent = Intent(this, ActivityCalendarAddEvent::class.java)
        startActivity(eventIntent)
    }
}


