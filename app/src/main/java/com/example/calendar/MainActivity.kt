package com.example.calendar

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.GridView
import android.widget.TextView
import java.text.SimpleDateFormat
import java.util.*

class MainActivity : AppCompatActivity() {
    private lateinit var gridView: GridView
    private lateinit var monthTextView: TextView
    private lateinit var prevMonthButton: Button
    private lateinit var nextMonthButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }

    private fun displayMonth(month: Int, year: Int) {
        // Створюємо календар і задаємо місяць та рік
        val calendar = Calendar.getInstance()
        calendar.set(Calendar.MONTH, month)
        calendar.set(Calendar.YEAR, year)

        // Виводимо назву місяця
        val monthFormat = SimpleDateFormat("MMMM yyyy", Locale.getDefault())
        monthTextView.text = monthFormat.format(calendar.time)


        prevMonthButton.setOnClickListener {
            // Зменшуємо місяць на одиницю
            calendar.set(Calendar.MONTH, month - 1)
            if (month == 0) {
                calendar.set(Calendar.MONTH, 11)
                calendar.set(Calendar.YEAR, year - 1)
            }
            displayMonth(calendar.get(Calendar.MONTH), calendar.get(Calendar.YEAR))
        }

        nextMonthButton.setOnClickListener {
            // Збільшуємо місяць на одиницю
            calendar.set(Calendar.MONTH, month + 1)
            if (month == 11) {
                calendar.set(Calendar.MONTH, 0)
                calendar.set(Calendar.YEAR, year + 1)
            }
            displayMonth(calendar.get(Calendar.MONTH), calendar.get(Calendar.YEAR))
        }

    }
}


