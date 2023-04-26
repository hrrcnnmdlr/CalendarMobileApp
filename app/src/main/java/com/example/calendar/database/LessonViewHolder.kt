package com.example.calendar.database

import android.view.View
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.calendar.R

class LessonWeekViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)  {
    // Оголошення текстових полів, що відображають ім'я події, опис та дату і час
    var scheduleName: TextView = itemView.findViewById(R.id.schedule_name)
}

class LessonViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)  {
    // Оголошення текстових полів, що відображають ім'я події, опис та дату і час
    var dayItemName: TextView = itemView.findViewById(R.id.day_item_name)
    var dayItemHomeWork: TextView = itemView.findViewById(R.id.day_item_home_work)
    //var editEvent: ImageView = itemView.findViewById(R.id.edit_event)

}