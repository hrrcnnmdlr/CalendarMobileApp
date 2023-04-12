package com.example.calendar

import android.R
import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Toast
import com.example.calendar.databinding.ActivityCalendarAddEventBinding
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

class ActivityCalendarAddEvent : AppCompatActivity() {

    private lateinit var binding: ActivityCalendarAddEventBinding

    @OptIn(DelicateCoroutinesApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityCalendarAddEventBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // ...

        val eventCategories = listOf(
            "Business",
            "Education",
            "Entertainment",
            "Food and Drink",
            "Health and Wellness",
            "Hobbies",
            "Music",
            "Networking",
            "Sports",
            "Technology",
            "Travel",
            "Other"
        )
        val adapter1 = ArrayAdapter(this, R.layout.simple_spinner_item, eventCategories)
        adapter1.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.categorySpinner.adapter = adapter1

        val repeatCategories = listOf(
            "Does not repeat",
            "Daily",
            "Weekly",
            "Monthly",
            "Yearly"
        )
        val adapter2 = ArrayAdapter(this, R.layout.simple_spinner_item, repeatCategories)
        adapter2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.repeatSpinner.adapter = adapter2

        val reminderCategories = listOf(
            "5 minutes before",
            "15 minutes before",
            "30 minutes before",
            "1 hour before",
            "1 day before")
        val adapter3 = ArrayAdapter(this, R.layout.simple_spinner_item, reminderCategories)
        adapter3.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.reminderSpinner.adapter = adapter3

        binding.startDateTimeTextView.setOnClickListener {
            showDateTimePicker(true)
        }

        binding.endDateTimeTextView.setOnClickListener {
            showDateTimePicker(false)
        }

        binding.addEventButton.setOnClickListener {
            val eventName = binding.eventNameEditText.text.toString()
            val description =
                binding.descriptionEditText.text.toString()
            val location = binding.locationEditText.text.toString()
            val category = binding.categorySpinner.selectedItem.toString()
            val repeat = binding.repeatSpinner.selectedItem.toString()
            val reminder = binding.reminderSpinner.selectedItem.toString()
            val dateFormatter = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())
            val startDate = dateFormatter.parse(binding.startDateTimeTextView.text.toString())
            val endDate = dateFormatter.parse(binding.endDateTimeTextView.text.toString())

            val event = Event(
                eventName = eventName,
                description = description,
                startDateTime = startDate?.time ?: 0,
                endDateTime = endDate?.time ?: 0,
                location = location,
                category = category,
                repeat = repeat,
                reminder = reminder
            )

            val db = MainDB.getDatabase(this)
            GlobalScope.launch(Dispatchers.IO) {
                db.getDao().addEvent(event)
            }


            //val createIntent = Intent(this, ActivityCalendarWeek::class.java)
            //startActivity(createIntent)
            //Toast.makeText(this, "Event added successfully", Toast.LENGTH_SHORT).show()
            Toast.makeText(this, event.toString(), Toast.LENGTH_SHORT).show()
        }

        // ...
    }

    var startDateTime: Long = 0
    var endDateTime: Long = 0
    private fun showDateTimePicker(isStartDate: Boolean) {
        val calendar = Calendar.getInstance()

        val datePickerDialog = DatePickerDialog(
            this,
            { _, year, month, dayOfMonth ->
                calendar.set(year, month, dayOfMonth)

                val timePickerDialog = TimePickerDialog(
                    this,
                    { _, hourOfDay, minute ->
                        calendar.set(Calendar.HOUR_OF_DAY, hourOfDay)
                        calendar.set(Calendar.MINUTE, minute)

                        if (isStartDate) {
                            startDateTime = calendar.timeInMillis
                            binding.startDateTimeTextView.text = SimpleDateFormat("yyyy/MM/dd HH:mm", Locale.getDefault()).format(calendar.time)
                        } else {
                            endDateTime = calendar.timeInMillis
                            binding.endDateTimeTextView.text = SimpleDateFormat("yyyy/MM/dd HH:mm", Locale.getDefault()).format(calendar.time)
                        }
                    },
                    calendar.get(Calendar.HOUR_OF_DAY),
                    calendar.get(Calendar.MINUTE),
                    true
                )

                timePickerDialog.show()
            },
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        )

        datePickerDialog.show()
    }
// ...
}