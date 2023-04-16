package com.example.calendar

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.runBlocking

internal class EventAdapter(
    private val context: Context,
    private val listEventsFlow: Flow<List<Event>>
) : RecyclerView.Adapter<EventViewHolder>() {

    // ініціалізуємо список подій
    private var mArrayList: List<Event> = emptyList() // список подій тепер є пустим
    // ініціалізуємо базу даних
    private val mDatabase: MainDB = MainDB.getDatabase(context)

    // метод, який створює та повертає новий об'єкт EventViewHolder
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EventViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.event_item, parent, false)
        return EventViewHolder(view)
    }

    // метод, який зв'язує дані з об'єктом EventViewHolder
    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: EventViewHolder, position: Int) {
        val event = mArrayList[position]
        holder.eventName.text = event.eventName
        holder.eventDescription.text = event.description
        holder.eventDateTime.text = "${event.startDateTime} - ${event.endDateTime}"
        //holder.editEvent.setOnClickListener { listener.onEditClick(event) }
    }

    // метод, який повертає кількість подій в списку
    override fun getItemCount(): Int {
        var count = 0
        runBlocking {
            listEventsFlow.collect { events ->
                count = events.size
            }
        }
        return count
    }


    // відкоментуйте цей метод, якщо ви хочете додати можливість редагування подій в списку
    /*private fun editEventDialog(event: Event) {
        val inflater = LayoutInflater.from(context)
        val subView = inflater.inflate(R.layout.add_event, null)
        val nameField: EditText = subView.findViewById(R.id.event_name)
        val descriptionField: EditText = subView.findViewById(R.id.event_description)
        val startField: EditText = subView.findViewById(R.id.event_start)
        val endField: EditText = subView.findViewById(R.id.event_end)
        nameField.setText(event.eventName)
        descriptionField.setText(event.description)
        startField.setText(event.startDateTime.toString())
        endField.setText(event.endDateTime.toString())
        val builder = AlertDialog.Builder(context)
        builder.setTitle("Edit Event")
        builder.setView(subView)
        builder.create()
        builder.setPositiveButton(
            "SAVE"
        ) { _, _ ->
            val name = nameField.text.toString()
            val description = descriptionField.text.toString()
            val startDateTime = startField.text.toString().toLong()
            val endDateTime = endField.text.toString().toLong()
            if (TextUtils.isEmpty(name)) {
                Toast.makeText(
                    context,
                    "Something went wrong. Check your input values",
                    Toast.LENGTH_LONG
                ).show()
            } else {
                val updatedEvent = Event(event.id, name, description, startDateTime, endDateTime)
                mDatabase.getDao().updateEvent(updatedEvent)
                notifyDataSetChanged()
            }
        }
        builder.setNegativeButton(
            "CANCEL"
        ) { _, _ -> Toast.makeText(context, "Edit cancelled", Toast.LENGTH_LONG).show() }
        builder.show()
    }*/
}
