package com.example.calendar

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.calendar.databinding.ActivityCalendarWeekBinding

class ActivityCalendarWeek : AppCompatActivity() {
    lateinit var binding: ActivityCalendarWeekBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCalendarWeekBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }

}