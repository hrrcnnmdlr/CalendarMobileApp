package com.example.calendar

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.calendar.databinding.ActivityCalendarMonthBinding

class ActivityCalendarMonth : AppCompatActivity() {
    lateinit var binding: ActivityCalendarMonthBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCalendarMonthBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }

    fun save() {

    }
}


