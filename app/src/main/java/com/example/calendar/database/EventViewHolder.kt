package com.example.calendar.database

import android.view.View
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.calendar.R

// Клас відповідальний за управління елементами інтерфейсу, які відображають події в RecyclerView
class EventViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

    // Оголошення текстових полів, що відображають ім'я події, опис та дату і час
    var eventName: TextView = itemView.findViewById(R.id.event_name)
    var eventDateTime: TextView = itemView.findViewById(R.id.event_date_time)
    //var editEvent: ImageView = itemView.findViewById(R.id.edit_event)
}
