package com.example.calendar

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

class ActivityEditEvent : AppCompatActivity() {

    private lateinit var binding: ActivityEditEventBinding
    private val viewModel: EventViewModel by viewModels()

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
                endDateTime = event.endDateTime
                binding.eventNameEditText.setText(event.eventName)
                binding.eventDescriptionEditText.setText(event.description)
                binding.locationEditText.setText(event.location)
                selectedCategoryId = event.categoryId
                binding.categorySpinner2.setSelection(selectedCategoryId - 1)
            } else {
                Toast.makeText(this@ActivityEditEvent, "Event not found", Toast.LENGTH_SHORT).show()
                finish()
            }
            binding.startDateTimeTextView.text = SimpleDateFormat(
                "yyyy/MM/dd HH:mm",
                Locale.getDefault()
            ).format(Date(startDateTime))
            binding.endDateTimeTextView.text = SimpleDateFormat(
                "yyyy/MM/dd HH:mm",
                Locale.getDefault()
            ).format(Date(endDateTime))

            val eventViewModel = ViewModelProvider(this@ActivityEditEvent)[EventViewModel::class.java]
            val categories = mutableListOf<String>()
            val db = MainDB.getDatabase(this@ActivityEditEvent)
            val categoryDao = db.categoryDao()

            fun getAllCategories() {
                categoryDao.getAllCategories().observeForever { categoriesList ->
                    categories.clear()
                    categories.addAll(categoriesList.map { it.name }) // map categories to their names
                    Log.d("DATABASE", "$categories")
                    if (categories.size > 0 && categories[categories.size - 1] != "New category") {
                        categories.add("New category")
                    }

                    // Set the adapter for categorySpinner only when categories are loaded
                    val adapter1 = ArrayAdapter(
                        this@ActivityEditEvent,
                        android.R.layout.simple_spinner_item,
                        categories
                    )
                    binding.categorySpinner2.adapter = adapter1

                    if (categories.size > 0) {
                        if (event != null) {
                            selectedCategoryId = event.categoryId
                            binding.categorySpinner2.setSelection(event.categoryId - 1)
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

            val adapter1 = ArrayAdapter(this@ActivityEditEvent, android.R.layout.simple_spinner_item, categories)
            binding.categorySpinner2.adapter = adapter1
            binding.categorySpinner2.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
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
                binding.categorySpinner2.setSelection(selectedCategoryId -1)
            }
            binding.categorySpinner2.setSelection(selectedCategoryId-1) //event.category)

            var selectedRepetition = EventRepetition.NONE
            val eventRepetition = listOf(EventRepetition.DAILY, EventRepetition.WEEKLY,
                EventRepetition.MONTHLY, EventRepetition.ANNUALLY, EventRepetition.NONE)
            val adapter2 = ArrayAdapter(this@ActivityEditEvent, android.R.layout.simple_spinner_item, eventRepetition)
            adapter2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            binding.repeatSpinner2.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(
                    parent: AdapterView<*>?,
                    view: View?,
                    position: Int,
                    id: Long
                ) {
                    if (eventRepetition[position] != EventRepetition.NONE) {
                        // Відкриваємо діалогове вікно для вибору максимальної дати
                        showMaxEndDateDialog()
                    }
                    selectedRepetition = eventRepetition[position]
                }

                override fun onNothingSelected(parent: AdapterView<*>?) {
                    // не робіть нічого
                }
            }
            binding.repeatSpinner2.adapter = adapter2
            if (event != null) {
                var i = 0
                while (event.repeat == eventRepetition[i].toString()) {
                    binding.repeatSpinner2.setSelection(i-1)
                    ++i
                }
                binding.reminder5Min2.isChecked = event.remind5MinutesBefore
                binding.reminder15Min2.isChecked = event.remind15MinutesBefore
                binding.reminder30Min2.isChecked = event.remind30MinutesBefore
                binding.reminder1Hour2.isChecked = event.remind1HourBefore
                binding.reminder1Day2.isChecked = event.remind1DayBefore
            }

            binding.saveButton.setOnClickListener {
                if (binding.eventNameEditText.text.toString().isEmpty()) {
                    Toast.makeText(this@ActivityEditEvent, "Event name cannot be empty", Toast.LENGTH_SHORT).show()
                    return@setOnClickListener
                }

                if (startDateTime >= endDateTime) {
                    Toast.makeText(this@ActivityEditEvent, "Invalid start/end time", Toast.LENGTH_SHORT).show()
                    return@setOnClickListener
                }

                val updatedEvent = event?.copy(
                    eventName = binding.eventNameEditText.text.toString(),
                    description = binding.eventDescriptionEditText.text.toString(),
                    startDateTime = startDateTime, // Встановити дату та час початку події
                    endDateTime = endDateTime, // Встановити дату та час закінчення події
                    location = binding.locationEditText.text.toString(),
                    categoryId = selectedCategoryId,
                    remind5MinutesBefore = binding.reminder5Min2.isChecked,
                    remind15MinutesBefore = binding.reminder15Min2.isChecked,
                    remind30MinutesBefore = binding.reminder30Min2.isChecked,
                    remind1HourBefore = binding.reminder1Hour2.isChecked,
                    remind1DayBefore = binding.reminder1Hour2.isChecked,
                    repeat = selectedRepetition.toString()
                )

                if (updatedEvent != null) {
                    if (selectedRepetition != EventRepetition.NONE) {
                        eventViewModel.update(event, maxDate)
                    }
                    else {
                        eventViewModel.update(event)
                    }
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
    private fun showDateTimePicker(isStartDate: Boolean, time: Long) {
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
    private var maxDate: Long = -1L
    // Функція для відображення діалогового вікна для вибору максимальної дати
    private fun showMaxEndDateDialog() {
        val calendar = Calendar.getInstance()
        val datePickerDialog = DatePickerDialog(
            this,
            { _, year, month, dayOfMonth ->
                // Встановлюємо максимальну дату в TextView
                calendar.set(year, month, dayOfMonth)
                maxDate = calendar.timeInMillis
                val formattedDate = SimpleDateFormat("dd.MM.yyyy", Locale.getDefault()).format(calendar.time)
                binding.textMaxEndDate2.text = formattedDate
            },
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        )
        datePickerDialog.show()
    }
}