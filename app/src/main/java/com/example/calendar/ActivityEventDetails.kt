package com.example.calendar

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.map
import com.example.calendar.databinding.ActivityEventDetailsBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

class ActivityEventDetails: AppCompatActivity() {

    private lateinit var binding: ActivityEventDetailsBinding
    private lateinit var eventViewModel: EventViewModel
    private var eventId: Int = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityEventDetailsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Отримати ідентифікатор зустрічі, переданий з попередньої активності
        eventId = intent.getIntExtra("event_id", -1)

        // Створити ViewModel для доступу до даних
        eventViewModel = ViewModelProvider(this)[EventViewModel::class.java]

        // Отримати дані про зустріч за заданим ідентифікатором
        lifecycleScope.launch {
            val event = eventViewModel.getEventById(eventId)
            // Відобразити дані про зустріч
            if (event != null) {
                binding.eventNameTextView.text = event.eventName
                binding.descriptionTextView.text = event.description
                binding.startDateTimeTextView.text =
                    SimpleDateFormat("dd.MM.yyyy HH:mm", Locale.getDefault())
                        .format(event.startDateTime)
                binding.endDateTimeTextView.text =
                    SimpleDateFormat("dd.MM.yyyy HH:mm", Locale.getDefault())
                        .format(event.endDateTime)
                binding.locationTextView.text = event.location
                val category = eventViewModel.getCategoryById(event.categoryId)
                if (category != null) {
                    binding.categoryTextView.text = category.name
                }
                binding.repeatTextView.text = event.repeat
                binding.reminderTextView.text = event.reminder
            }
            // Додати обробник для кнопки редагування
            binding.editEventButton.setOnClickListener {
                val intent = Intent(this@ActivityEventDetails, ActivityEditEvent::class.java)
                Log.d("ActivityEventDetails", "eventId: $eventId")
                intent.putExtra("event_id", eventId)
                startActivity(intent)
                finish()
            }
            // Додати обробник для кнопки видалення
            binding.deleteEventButton.setOnClickListener {
                if (event != null) {
                    eventViewModel.delete(event)
                }
                Toast.makeText(this@ActivityEventDetails, "Event deleted", Toast.LENGTH_SHORT).show()
                finish()
            }
        }

    }
}