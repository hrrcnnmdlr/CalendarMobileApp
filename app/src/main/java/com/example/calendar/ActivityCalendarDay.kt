package com.example.calendar

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.calendar.databinding.ActivityCalendarDayBinding
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.util.*


class ActivityCalendarDay : AppCompatActivity() {
    lateinit var binding: ActivityCalendarDayBinding

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
    @OptIn(DelicateCoroutinesApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCalendarDayBinding.inflate(layoutInflater)
        val dateInMillis = intent.getLongExtra("date", -1)
        val calendar = Calendar.getInstance()
        if (dateInMillis != -1L) {
            calendar.timeInMillis = dateInMillis
        }
        val day = calendar.get(Calendar.DAY_OF_MONTH)
        val dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK)
        binding.month2.text = months[calendar.get(Calendar.MONTH)]
        binding.year2.text = calendar.get(Calendar.YEAR).toString()
        binding.textView17.text = week[dayOfWeek - 1]
        binding.textView18.text = day.toString()
        // val value = intent.getStringExtra("key")

        /*val recyclerView = findViewById<RecyclerView>(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this)
        val adapter = EventAdapter()
        recyclerView.adapter = adapter*/
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
}