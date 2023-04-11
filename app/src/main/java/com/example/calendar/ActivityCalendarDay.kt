package com.example.calendar

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.calendar.databinding.ActivityCalendarDayBinding

class ActivityCalendarDay : AppCompatActivity() {
    lateinit var binding: ActivityCalendarDayBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCalendarDayBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }
}