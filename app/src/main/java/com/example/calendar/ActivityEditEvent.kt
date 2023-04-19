package com.example.calendar

import android.R
import android.app.Activity
import android.app.AlertDialog
import android.app.DatePickerDialog
import android.app.TimePickerDialog
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.InputType
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.EditText
import android.widget.Toast
import androidx.activity.viewModels
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.example.calendar.databinding.ActivityEditEventBinding
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*
import kotlin.math.log

class ActivityEditEvent : AppCompatActivity() {

    private lateinit var binding: ActivityEditEventBinding
    private val viewModel: EventViewModel by viewModels()

    private lateinit var event: Event
    private var startDateTime: Long = 0
    private var endDateTime: Long = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityEditEventBinding.inflate(layoutInflater)
        setContentView(binding.root)

        var selectedCategoryId = -1
        val eventId = intent.getIntExtra("event_id", -1)
        if (eventId == -1) {
            Toast.makeText(this, "Invalid event id", Toast.LENGTH_SHORT).show()
            finish()
        }
        lifecycleScope.launch {
            val event = viewModel.getEventById(eventId)
            if (event != null) {
                startDateTime = event.startDateTime
            }
            if (event != null) {
                endDateTime = event.endDateTime
            }
            if (event != null) {
                binding.eventNameEditText.setText(event.eventName)
            }
            if (event != null) {
                binding.eventDescriptionEditText.setText(event.description)
            }
            binding.startDateTimeTextView.text = SimpleDateFormat(
                "yyyy/MM/dd HH:mm",
                Locale.getDefault()
            ).format(Date(startDateTime))
            binding.endDateTimeTextView.text = SimpleDateFormat(
                "yyyy/MM/dd HH:mm",
                Locale.getDefault()
            ).format(Date(endDateTime))
            if (event != null) {
                binding.locationEditText.setText(event.location)
            }

            val eventViewModel = ViewModelProvider(this@ActivityEditEvent)[EventViewModel::class.java]
            val categories = mutableListOf<String>()
            val db = MainDB.getDatabase(this@ActivityEditEvent)
            val categoryDao = db.categoryDao()

            fun getAllCategories() {
                categoryDao.getAllCategories().observeForever { categoriesList ->
                    categories.clear()
                    categories.addAll(categoriesList.map { it.name }) // map categories to their names
                    Log.d("DATABASE", "${categories}")
                    if (categories.size > 0 && categories[categories.size - 1] != "New category") {
                        categories.add("New category")
                    }

                    // Set the adapter for categorySpinner only when categories are loaded
                    val adapter1 = ArrayAdapter(
                        this@ActivityEditEvent,
                        R.layout.simple_spinner_item,
                        categories
                    )
                    binding.categorySpinner.adapter = adapter1

                    if (categories.size > 0) {
                        if (event != null) {
                            selectedCategoryId = event.categoryId
                            binding.categorySpinner.setSelection(event.categoryId - 1)
                        } else {
                            selectedCategoryId = 0
                        }
                    }
                }
            }
            getAllCategories()
            if (categories.size > 0) {
                if (event != null) {
                    selectedCategoryId = event.categoryId
                }
            }

            val adapter1 = ArrayAdapter(this@ActivityEditEvent, R.layout.simple_spinner_item, categories)
            binding.categorySpinner.adapter = adapter1
            binding.categorySpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(
                    parent: AdapterView<*>,
                    view: View?,
                    position: Int,
                    id: Long
                ) {
                    if (categories.size > 0 && position == categories.size - 1) {
                        showAddCategoryDialog(adapter1, eventViewModel, categories)
                    } else {
                        selectedCategoryId = id.toInt() + 1
                    }
                }
                override fun onNothingSelected(parent: AdapterView<*>?) {
                    // do nothing
                }
            }
            if (event != null) {
                selectedCategoryId = event.categoryId
                binding.categorySpinner.setSelection(selectedCategoryId - 1)
            }

            // Створення списку варіантів повторення подій та встановлення адаптера для Spinner
            val repeatCategories = listOf(
                "Does not repeat",
                "Daily",
                "Weekly",
                "Monthly",
                "Yearly"
            )
            val adapter2 = ArrayAdapter(this@ActivityEditEvent, R.layout.simple_spinner_item, repeatCategories)
            adapter2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            binding.repeatSpinner.adapter = adapter2

            // Створення списку напоминань та встановлення адаптера для Spinner
            val reminderCategories = listOf(
                "5 minutes before",
                "15 minutes before",
                "30 minutes before",
                "1 hour before",
                "1 day before"
            )
            val adapter3 = ArrayAdapter(this@ActivityEditEvent, R.layout.simple_spinner_item, reminderCategories)
            adapter3.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            binding.reminderSpinner.adapter = adapter3

            binding.categorySpinner.setSelection(selectedCategoryId-1) //event.category)
            binding.repeatSpinner.setSelection(1) //event.repeat)
            binding.reminderSpinner.setSelection(1) //event.reminder)

            binding.saveButton.setOnClickListener {
                val eventName = binding.eventNameEditText.text.toString()
                val description = binding.eventDescriptionEditText.text.toString()
                val location = binding.locationEditText.text.toString()
                val repeat = binding.repeatSpinner.selectedItem as String
                val reminder = binding.reminderSpinner.selectedItem as String

                if (eventName.isEmpty()) {
                    Toast.makeText(this@ActivityEditEvent, "Event name cannot be empty", Toast.LENGTH_SHORT).show()
                    return@setOnClickListener
                }

                if (startDateTime >= endDateTime) {
                    Toast.makeText(this@ActivityEditEvent, "Invalid start/end time", Toast.LENGTH_SHORT).show()
                    return@setOnClickListener
                }

                val updatedEvent = event?.copy(
                    eventName = eventName,
                    description = description,
                    startDateTime = startDateTime,
                    endDateTime = endDateTime,
                    location = location,
                    categoryId = selectedCategoryId,
                    repeat = repeat,
                    reminder = reminder
                )

                if (updatedEvent != null) {
                    viewModel.update(updatedEvent)
                }
                setResult(Activity.RESULT_OK)
                finish()
            }

            binding.cancelButton.setOnClickListener {
                setResult(Activity.RESULT_CANCELED)
                finish()
            }

            binding.startDateTimeTextView.setOnClickListener {
                if (event != null) {
                    showDateTimePicker(true, event.startDateTime)
                }
            }

            binding.endDateTimeTextView.setOnClickListener {
                if (event != null) {
                    showDateTimePicker(false, event.endDateTime)
                }
            }
        }

    }

    // Метод для відображення діалогового вікна вибору дати та часу
    fun showDateTimePicker(isStartDate: Boolean, time: Long) {
        val calendar = Calendar.getInstance()
        calendar.timeInMillis = time
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
                            binding.startDateTimeTextView.text = SimpleDateFormat(
                                "yyyy/MM/dd HH:mm",
                                Locale.getDefault()
                            ).format(calendar.time)
                            startDateTime =
                                calendar.timeInMillis // Встановити дату та час початку події
                        } else {
                            binding.endDateTimeTextView.text = SimpleDateFormat(
                                "yyyy/MM/dd HH:mm",
                                Locale.getDefault()
                            ).format(calendar.time)
                            endDateTime =
                                calendar.timeInMillis // Встановити дату та час початку події
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
    private fun showAddCategoryDialog(adapter: ArrayAdapter<String>, eventViewModel: EventViewModel, categories: MutableList<String>) {
        val input = EditText(this)
        input.inputType = InputType.TYPE_CLASS_TEXT
        val dialog = AlertDialog.Builder(this)
            .setTitle("New category")
            .setMessage("Input name of category:")
            .setView(input)
            .setPositiveButton("Add") { _, _ ->
                val categoryName = input.text.toString()
                if (categoryName.isNotBlank()) {
                    eventViewModel.insertCategory(Category(0, categoryName))
                    // перезавантажити список категорій
                    categories.removeAt(categories.size - 1)
                    categories.add(categoryName)
                    categories.add("New category")
                    adapter.notifyDataSetChanged()
                    // відобразіть події з новою категорією
                    // ...
                } else {
                    Toast.makeText(this, "Name must not be empty", Toast.LENGTH_SHORT).show()
                }
            }
            .setNegativeButton("Cansel") { dialog, _ -> dialog.cancel() }
            .create()
        dialog.show()
    }
}