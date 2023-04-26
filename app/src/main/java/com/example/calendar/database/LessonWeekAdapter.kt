package com.example.calendar.database

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.lifecycle.lifecycleScope
import androidx.navigation.Navigation
import androidx.recyclerview.widget.RecyclerView
import com.example.calendar.R
import com.example.calendar.fragments.eventId
import kotlinx.coroutines.launch

class LessonWeekAdapter (
    private val context: Context,
    private val events: List<Event>
) : RecyclerView.Adapter<LessonWeekViewHolder>() {

    // ініціалізуємо список подій
    private var mArrayList: List<Event> = events // список подій тепер є пустим

    // метод, який створює та повертає новий об'єкт EventViewHolder
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LessonWeekViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.schedule_item, parent, false)
        return LessonWeekViewHolder(view)
    }

    // метод, який зв'язує дані з об'єктом EventViewHolder
    @SuppressLint("SetTextI18n", "SimpleDateFormat")
    override fun onBindViewHolder(holder: LessonWeekViewHolder, position: Int) {
        if (mArrayList.isNotEmpty()) {
            val event = mArrayList[position]
            holder.scheduleName.text = event.eventName
            holder.itemView.setOnClickListener {
                val controller = Navigation.findNavController(holder.itemView)
                eventId = event.id
                controller.navigate(R.id.nav_schedule_details)
            }
        } else {
            // handle empty list
        }
    }

    // метод, який повертає кількість подій в списку
    override fun getItemCount(): Int {
        return events.size
    }
}

class LessonAdapter (
    private val context: Context,
    private val events: List<Event>,
    private val lesson: Schedule,
) : RecyclerView.Adapter<LessonViewHolder>() {

    // ініціалізуємо список подій
    private var mArrayList: List<Event> = events // список подій тепер є пустим
    // метод, який створює та повертає новий об'єкт EventViewHolder
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LessonViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.shedule_day_item, parent, false)
        return LessonViewHolder(view)
    }

    // метод, який зв'язує дані з об'єктом EventViewHolder
    @SuppressLint("SetTextI18n", "SimpleDateFormat")
    override fun onBindViewHolder(holder: LessonViewHolder, position: Int) {
        if (mArrayList.isNotEmpty()) {
            val event = mArrayList[position]
            holder.dayItemName.text = event.eventName
            holder.dayItemHomeWork.text = lesson.homework
            holder.itemView.setOnClickListener {
                val controller = Navigation.findNavController(holder.itemView)
                eventId = event.id
                controller.navigate(R.id.nav_schedule_details)
            }
        } else {
            // handle empty list
        }
    }

    // метод, який повертає кількість подій в списку
    override fun getItemCount(): Int {
        return events.size
    }
}
