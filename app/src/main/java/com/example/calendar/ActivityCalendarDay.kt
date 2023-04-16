package com.example.calendar

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.calendar.databinding.ActivityCalendarDayBinding
import java.util.*


class ActivityCalendarDay : AppCompatActivity() {
    // Прив'язка до розмітки ActivityCalendarDayBinding
    lateinit var binding: ActivityCalendarDayBinding

    // Список днів тижня
    private val week =  listOf(
        "Su",
        "Mo",
        "Tu",
        "Wed",
        "Th",
        "Fr",
        "Sat"
    )
    // Список місяців
    private val months = listOf(
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
    // Функція onCreate() викликається при створенні Activity
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Приєднання прив'язки до розмітки ActivityCalendarDayBinding
        binding = ActivityCalendarDayBinding.inflate(layoutInflater)

        // Отримання дати з Intent
        val dateInMillis = intent.getLongExtra("date", -1)
        val calendar = Calendar.getInstance()
        if (dateInMillis != -1L) {
            calendar.timeInMillis = dateInMillis
        }

        // Отримання дня та дня тижня
        val day = calendar.get(Calendar.DAY_OF_MONTH)
        val dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK)

        // Встановлення тексту для TextView
        binding.month2.text = months[calendar.get(Calendar.MONTH)]
        binding.year2.text = calendar.get(Calendar.YEAR).toString()
        binding.textView17.text = week[dayOfWeek - 1]
        binding.textView18.text = day.toString()

        val eventView: RecyclerView = binding.recyclerEventView
        val linearLayoutManager = LinearLayoutManager(this)
        eventView.layoutManager = linearLayoutManager
        eventView.setHasFixedSize(true)
        val dataBase = MainDB.getDatabase(this)
        eventView.visibility = View.VISIBLE
        val mAdapter = EventAdapter(this, dataBase.getDao().getAllEvents())
        eventView.adapter = mAdapter
        // Приєднання прив'язки до кореневого елемента розмітки
        setContentView(binding.root)
    }

    // Функція відкриття ActivityCalendarDay
    fun openDays(view: View) {
        val dayIntent = Intent(this, ActivityCalendarDay::class.java)
        val date = intent.getLongExtra("date", -1)
        dayIntent.putExtra("date", date)
        startActivity(dayIntent)
    }

    // Функція відкриття ActivityCalendarWeek
    fun openWeeks(view: View) {
        val weekIntent = Intent(this, ActivityCalendarWeek::class.java)
        val date = intent.getLongExtra("date", -1)
        weekIntent.putExtra("date", date)
        startActivity(weekIntent)
    }

    // Функція відкриття ActivityCalendarMonth
    fun openMonths(view: View) {
        val monthIntent = Intent(this, ActivityCalendarMonth::class.java)
        val date = intent.getLongExtra("date", -1)
        monthIntent.putExtra("date", date)
        startActivity(monthIntent)
    }

    // Метод, який змінює відображуваний тиждень та запускає ActivityCalendarWeek зі зміненим датою.
    fun createEvent(view: View) {
        val eventIntent = Intent(this, ActivityCalendarAddEvent::class.java)
        startActivity(eventIntent)
    }
}