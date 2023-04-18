package com.example.calendar

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.calendar.databinding.ActivityCalendarWeekBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.*

class ActivityCalendarWeek : AppCompatActivity() {
    lateinit var binding: ActivityCalendarWeekBinding

    // список для днів тижня
    val week =  listOf(
        "Su", // Неділя
        "Mo", // Понеділок
        "Tu", // Вівторок
        "Wed", // Середа
        "Th", // Четвер
        "Fr", // П'ятниця
        "Sat" // Субота
    )

    // список для місяців
    val months = listOf(
        "January", // Січень
        "February", // Лютий
        "March", // Березень
        "April", // Квітень
        "May", // Травень
        "June", // Червень
        "July", // Липень
        "August", // Серпень
        "September", // Вересень
        "October", // Жовтень
        "November", // Листопад
        "December" // Грудень
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // встановлюємо зв'язок з файлом розмітки activity_calendar_week.xml
        binding = ActivityCalendarWeekBinding.inflate(layoutInflater)

        // отримуємо передану дату
        val dateInMillis = intent.getLongExtra("date", -1)
        val calendar = Calendar.getInstance()
        if (dateInMillis != -1L) {
            calendar.timeInMillis = dateInMillis
        }

        // отримуємо поточний день, день тижня і місяць
        val day = calendar.get(Calendar.DAY_OF_MONTH)
        val dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK)
        val month = calendar.get(Calendar.MONTH)

        // встановлюємо назву місяця і рік в текстові поля
        binding.month = months[month]
        binding.year= calendar.get(Calendar.YEAR).toString()

        // встановлюємо назви днів тижня в текстові поля
        binding.dayOfWeek01.text = week[if (dayOfWeek < 4){dayOfWeek+7-4}else {dayOfWeek - 4}]
        binding.dayOfWeek02.text = week[if (dayOfWeek < 3){dayOfWeek+7-3}else {dayOfWeek - 3}]
        binding.dayOfWeek03.text = week[if (dayOfWeek < 2){dayOfWeek+7-2}else {dayOfWeek - 2}]
        binding.dayOfWeek04.text = week[dayOfWeek - 1]
        binding.dayOfWeek05.text = week[if (dayOfWeek > 6){dayOfWeek-7}else {dayOfWeek}]
        binding.dayOfWeek06.text = week[if (dayOfWeek > 5){dayOfWeek-7+1}else {dayOfWeek + 1}]
        binding.dayOfWeek07.text = week[if (dayOfWeek > 4){dayOfWeek-7+2}else {dayOfWeek + 2}]

        calendar.add(Calendar.DAY_OF_MONTH, -3)

        // Отримуємо дату на кожен день тижня та встановлюємо її в відповідне поле
        for (i in 1..7) {
            val nextDay = calendar.get(Calendar.DAY_OF_MONTH)
            val dateTextView = when (i) {
                1 -> binding.date01
                2 -> binding.date02
                3 -> binding.date03
                4 -> binding.date04
                5 -> binding.date05
                6 -> binding.date06
                7 -> binding.date07
                else -> null
            }
            dateTextView?.text = nextDay.toString()
            calendar.add(Calendar.DAY_OF_MONTH, 1)
        }
        calendar.add(Calendar.DAY_OF_MONTH, -4)

        val eventView: RecyclerView = binding.recyclerEventView
        val linearLayoutManager = LinearLayoutManager(this)
        eventView.layoutManager = linearLayoutManager
        eventView.visibility = View.VISIBLE

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
            val dataBase = MainDB.getDatabase(this@ActivityCalendarWeek)
            val events = dataBase.getDao().getEventsForDay(date)
            // switch back to the main thread to update the UI
            withContext(Dispatchers.Main) {
                val mAdapter = EventAdapter(this@ActivityCalendarWeek, events)
                eventView.adapter = mAdapter
            }
        }

        setContentView(binding.root)
    }

    // Відкриваємо екран з подіями вибраного дня
    fun openDays(view: View) {
        val dayIntent = Intent(this, ActivityCalendarDay::class.java)
        val date = intent.getLongExtra("date", -1)
        dayIntent.putExtra("date", date)
        startActivity(dayIntent)
    }

    // Відкриваємо екран з подіями вибраного тижня
    fun openWeeks(view: View) {
        val weekIntent = Intent(this, ActivityCalendarWeek::class.java)
        val date = intent.getLongExtra("date", -1)
        weekIntent.putExtra("date", date)
        startActivity(weekIntent)
    }

    // Відкриваємо екран з подіями вибраного місяця
    fun openMonths(view: View) {
        val monthIntent = Intent(this, ActivityCalendarMonth::class.java)
        val date = intent.getLongExtra("date", -1)
        monthIntent.putExtra("date", date)
        startActivity(monthIntent)
    }

    // Метод, який запускає ActivityCalendarAddEvent для створення події.
    fun createEvent(view: View) {
        val eventIntent = Intent(this, ActivityCalendarAddEvent::class.java)
        val date = intent.getLongExtra("date", -1)
        eventIntent.putExtra("date", date)
        startActivity(eventIntent)
    }

    // Методи, які змінюють відображуваний тиждень в залежності від дня тижня, на який користувач натиснув.
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

    // Метод, який змінює відображуваний тиждень та запускає ActivityCalendarWeek зі зміненим датою.
    private fun changeWeek(numberOfDay: Int) {
        val weekIntent = Intent(this, ActivityCalendarWeek::class.java)
        val date = intent.getLongExtra("date", -1)
        weekIntent.putExtra("date", date + (86400000 * numberOfDay))
        startActivity(weekIntent)
    }
}