package com.example.calendar

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch


class EventAdapter     {// Конструктор, в якому ініціалізуємо список подій
    /*(  // список подій
    private var events: List<Event>
) :
    RecyclerView.Adapter<EventAdapter.ViewHolder>() {
    // Метод, який створює новий ViewHolder і повертає його
    @OptIn(DelicateCoroutinesApi::class)
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view: View = LayoutInflater.from(parent.context)
            .inflate(R.layout.event_list_item, parent, false)
        val db = MainDB.getDatabase(this)
        var events: List<Event>? = null
        GlobalScope.launch(Dispatchers.IO) {
            events = db.getDao().getEventsForDay(intent.getLongExtra("date", -1))
        }
        return ViewHolder(view)
    }

    // Метод, який заповнює ViewHolder даними про подію
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val event = events[position]
        holder.name.setText(event.eventName)
        holder.date.setText(event.startDateTime.toString())
    }

    // Метод, який повертає кількість елементів в списку подій
    override fun getItemCount(): Int {
        return events.size
    }

    // Клас ViewHolder, який містить посилання на елементи інтерфейсу
    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var name: TextView
        var date: TextView

        init {
            name = itemView.findViewById(R.id.event_name)
            date = itemView.findViewById(R.id.event_date_time)
        }
    }*/
}