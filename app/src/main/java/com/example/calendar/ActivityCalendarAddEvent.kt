package com.example.calendar

import android.app.AlertDialog
import android.app.DatePickerDialog
import android.app.TimePickerDialog
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.InputType
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.EditText
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import com.example.calendar.databinding.ActivityCalendarAddEventBinding
import kotlinx.coroutines.*
import java.text.SimpleDateFormat
import java.util.*

class ActivityCalendarAddEvent : AppCompatActivity() {

    private lateinit var binding: ActivityCalendarAddEventBinding

    @OptIn(DelicateCoroutinesApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Створення binding для Activity
        binding = ActivityCalendarAddEventBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Створення списку категорій подій та встановлення адаптера для Spinner
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
        val eventViewModel = ViewModelProvider(this)[EventViewModel::class.java]
        val categories = mutableListOf<String>()
        val db = MainDB.getDatabase(this)
        val categoryDao = db.categoryDao()
        fun getAllCategories() {
            categoryDao.getAllCategories().observeForever { categoriesList ->
                categories.clear()
                categories.addAll(categoriesList.map { it.name }) // map categories to their names
            }
        }
        getAllCategories()
        var selectedCategoryId: Int = 0
        categories.add("New category") // додайте останній елемент у спінері
        val adapter1 = ArrayAdapter(this, android.R.layout.simple_spinner_item, categories)
        binding.categorySpinner.adapter = adapter1
        binding.categorySpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
                if (position == categories.size - 1) {
                    showAddCategoryDialog(adapter1, eventViewModel, categories)
                } else {
                    //
                }
                selectedCategoryId = position
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                // не робіть нічого
            }
        }


        // Створення списку варіантів повторення подій та встановлення адаптера для Spinner
        val repeatCategories = listOf(
            "Does not repeat",
            "Daily",
            "Weekly",
            "Monthly",
            "Yearly"
        )
        val adapter2 = ArrayAdapter(this, android.R.layout.simple_spinner_item, repeatCategories)
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
        val adapter3 = ArrayAdapter(this, android.R.layout.simple_spinner_item, reminderCategories)
        adapter3.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.reminderSpinner.adapter = adapter3

        // Обробник натискання на TextView для вибору дати та часу початку та закінчення події
        binding.startDateTimeTextView.setOnClickListener {
            showDateTimePicker(true)
        }

        binding.endDateTimeTextView.setOnClickListener {
            showDateTimePicker(false)
        }
        // Обробник натискання на кнопку додавання події
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

            // Створити об'єкт події і додати його до бази даних

            val event = Event(
                eventName = eventName,
                description = description,
                startDateTime = startDateTime, // Встановити дату та час початку події
                endDateTime = endDateTime, // Встановити дату та час закінчення події
                location = location,
                categoryId = selectedCategoryId,
                repeat = repeat,
                reminder = reminder
            )

            // Отримати доступ до бази даних та додати об'єкт події до неї
            val db = MainDB.getDatabase(this)
            GlobalScope.launch(Dispatchers.IO) {
                db.getDao().addEvent(event)
            }

            // Показати повідомлення користувачеві про успішне додавання події
            Toast.makeText(this, event.toString(), Toast.LENGTH_SHORT).show()
        }
    }

    // Метод для відображення діалогового вікна вибору дати та часу
    var startDateTime: Long = 0
    var endDateTime: Long = 0
    fun showDateTimePicker(isStartDate: Boolean) {
        val calendar = Calendar.getInstance()
        val dateInMillis = intent.getLongExtra("date", -1)
        calendar.timeInMillis = dateInMillis
        val datePickerDialog = DatePickerDialog(this,
            { _, year, month, dayOfMonth ->
                calendar.set(year, month, dayOfMonth)
                val timePickerDialog = TimePickerDialog(this,
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