package com.example.calendar

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.CalendarView
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.calendar.databinding.ActivityCalendarMonthBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
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
        val eventView: RecyclerView = binding.recyclerEventView

        // Встановлення вибраної дати, якщо вона передана із попередньої активності
        selectedDate = intent.getLongExtra("date", calendarView.date)
        calendarView.date = selectedDate
        calendarView.setOnDateChangeListener { _, year, month, dayOfMonth ->
            // Виконати дії, коли користувач вибирає дату
            // Наприклад, оновити список подій для вибраної дати
            val selectedDateInMillis = Calendar.getInstance().apply {
                set(year, month, dayOfMonth, 0, 0, 0)
            }.timeInMillis
            lifecycleScope.launch(Dispatchers.IO) {
                val dataBase = MainDB.getDatabase(this@ActivityCalendarMonth)
                val events = dataBase.getDao().getEventsForDay(selectedDateInMillis)
                // switch back to the main thread to update the UI
                withContext(Dispatchers.Main) {
                    val mAdapter = EventAdapter(this@ActivityCalendarMonth, events)
                    eventView.adapter = mAdapter
                }
            }
            val calendar = Calendar.getInstance()
            calendar.set(year, month, dayOfMonth)
            // Збереження вибраної дати в поле класу
            selectedDate = calendar.timeInMillis
        }

        val linearLayoutManager = LinearLayoutManager(this)
        eventView.layoutManager = linearLayoutManager
        eventView.visibility = View.VISIBLE

        var dateInMillis = selectedDate
        if (selectedDate == 0L) {dateInMillis = binding.calendarView.date}
        val calendar = Calendar.getInstance()
        calendar.timeInMillis = dateInMillis

        val cal = Calendar.getInstance()
        cal.set(Calendar.YEAR, calendar.get(Calendar.YEAR))
        cal.set(Calendar.MONTH, calendar.get(Calendar.MONTH))
        cal.set(Calendar.DAY_OF_MONTH, calendar.get(Calendar.DAY_OF_MONTH))
        cal.set(Calendar.HOUR_OF_DAY, 0)
        cal.set(Calendar.MINUTE, 0)
        cal.set(Calendar.SECOND, 0)
        cal.set(Calendar.MILLISECOND, 0)
        val date = cal.timeInMillis

        // launch a coroutine on the IO dispatcher to get the events from the database
        lifecycleScope.launch(Dispatchers.IO) {
            val dataBase = MainDB.getDatabase(this@ActivityCalendarMonth)
            val events = dataBase.getDao().getEventsForDay(date)
            // switch back to the main thread to update the UI
            withContext(Dispatchers.Main) {
                val mAdapter = EventAdapter(this@ActivityCalendarMonth, events)
                eventView.adapter = mAdapter
            }
        }

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
        var date = selectedDate
        if (selectedDate == 0L) {date = binding.calendarView.date}
        eventIntent.putExtra("date", date)
        startActivity(eventIntent)
    }
}


