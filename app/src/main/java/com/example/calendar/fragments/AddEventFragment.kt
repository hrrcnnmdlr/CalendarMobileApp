package com.example.calendar.fragments

import android.app.AlertDialog
import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Context
import android.os.Bundle
import android.text.InputType
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.EditText
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.preference.PreferenceManager
import com.example.calendar.*
import com.example.calendar.database.*
import com.example.calendar.databinding.FragmentAddEventBinding
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

class AddEventFragment : Fragment() {

    private var _binding: FragmentAddEventBinding? = null
    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAddEventBinding.inflate(inflater, container, false)
        return binding.root
    }

    @OptIn(DelicateCoroutinesApi::class)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        // Створення binding для Activity
        val eventViewModel = ViewModelProvider(this)[EventViewModel::class.java]
        val categories = mutableListOf<String>()
        val db = MainDB.getDatabase(requireContext())
        val categoryDao = db.categoryDao()
        var selectedCategoryId = 0
        fun getAllCategories(context: Context) {
            categoryDao.getAllCategories().observeForever { categoriesList ->
                categories.clear()
                categories.addAll(categoriesList.filter { it.id != 2 }
                    .map { it.name })// map categories to their names
                Log.d("DATABASE", "$categories")
                if (categories.size > 0 && categories[categories.size - 1] != "New category") {
                    categories.add("New category")
                }
                // Set the adapter for categorySpinner only when categories are loaded
                val adapter1 = ArrayAdapter(
                    context,
                    android.R.layout.simple_spinner_item,
                    categories
                )
                binding.categorySpinner.adapter = adapter1
                lifecycleScope.launch {
                    if (categories.size > 0) {
                        selectedCategoryId = 1
                        binding.categorySpinner.setSelection(0)
                    }
                    binding.categorySpinner.onItemSelectedListener =
                        object : AdapterView.OnItemSelectedListener {
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
                }
            }
        }
        lifecycleScope.launch {
            getAllCategories(requireContext())
            val adapter1 = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, categories)
            binding.categorySpinner.adapter = adapter1
        }
        var selectedRepetition = EventRepetition.NONE
        val eventRepetition = listOf(
            EventRepetition.DAILY, EventRepetition.WEEKLY,
            EventRepetition.MONTHLY, EventRepetition.ANNUALLY, EventRepetition.NONE)
        val adapter2 = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, eventRepetition)
        adapter2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.repeatSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
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
        binding.repeatSpinner.adapter = adapter2
        binding.repeatSpinner.setSelection(eventRepetition.size - 1)

        // Обробник натискання на TextView для вибору дати та часу початку та закінчення події
        binding.startDateTimeTextView.setOnClickListener {
            showDateTimePicker(true)
        }

        binding.endDateTimeTextView.setOnClickListener {
            showDateTimePicker(false)
        }
        // Обробник натискання на кнопку додавання події
        binding.addEventButton.setOnClickListener {
            if (binding.eventNameEditText.text.toString().isEmpty()) {
                Toast.makeText(
                    requireContext(),
                    "Event name cannot be empty",
                    Toast.LENGTH_SHORT
                ).show()
                return@setOnClickListener
            }

            if (startDateTime == 0L || endDateTime == 0L || startDateTime > endDateTime) {
                Toast.makeText(
                    requireContext(),
                    "Invalid start/end time",
                    Toast.LENGTH_SHORT
                )
                    .show()
                return@setOnClickListener
            }

            // Створити об'єкт події і додати його до бази даних
            val event = Event(
                eventName = binding.eventNameEditText.text.toString(),
                description = binding.descriptionEditText.text.toString(),
                startDateTime = startDateTime, // Встановити дату та час початку події
                endDateTime = endDateTime, // Встановити дату та час закінчення події
                location = binding.locationEditText.text.toString(),
                categoryId = selectedCategoryId,
                remind5MinutesBefore = binding.reminder5Min.isChecked,
                remind15MinutesBefore = binding.reminder15Min.isChecked,
                remind30MinutesBefore = binding.reminder30Min.isChecked,
                remind1HourBefore = binding.reminder1Hour.isChecked,
                remind1DayBefore = binding.reminder1Hour.isChecked,
                repeat = selectedRepetition.toString(),
                repeatParentId = 0,
                maxDateForRepeat = 0
            )

            // Отримати доступ до бази даних та додати об'єкт події до неї
            GlobalScope.launch(Dispatchers.IO) {
                if (selectedRepetition != EventRepetition.NONE) {
                    eventViewModel.insert(event, maxDate)
                }
                else {
                    eventViewModel.insert(event)
                }
            }
            // Показати повідомлення користувачеві про успішне додавання події
            findNavController().navigateUp()
        }
    }

    // Метод для відображення діалогового вікна вибору дати та часу
    private var startDateTime: Long = 0
    private var endDateTime: Long = 0
    private fun showDateTimePicker(isStartDate: Boolean) {
        val calendar = Calendar.getInstance()
        val dateInMillis = selectedDate
        calendar.timeInMillis = dateInMillis

        val datePickerDialog = DatePickerDialog(requireContext(),
            { _, year, month, dayOfMonth ->
                calendar.set(year, month, dayOfMonth)
                val timePickerDialog = TimePickerDialog(requireContext(),
                    { _, hourOfDay, minute ->
                        calendar.set(Calendar.HOUR_OF_DAY, hourOfDay)
                        calendar.set(Calendar.MINUTE, minute)
                        val dateFormat = PreferenceManager.getDefaultSharedPreferences(requireContext()).
                        getString("date_format_preference", "dd/MM/yyyy")
                        val timeFormat = PreferenceManager.getDefaultSharedPreferences(requireContext()).
                        getString("time_format_preference", "HH:mm")
                        if (isStartDate) {
                            binding.startDateTimeTextView.text = SimpleDateFormat(
                                "$dateFormat $timeFormat",
                                Locale.getDefault()
                            ).format(calendar.time)
                            startDateTime = calendar.timeInMillis
                        } else {
                            binding.endDateTimeTextView.text = SimpleDateFormat(
                                "$dateFormat $timeFormat",
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
        val input = EditText(requireContext())
        input.inputType = InputType.TYPE_CLASS_TEXT
        val dialog = AlertDialog.Builder(requireContext())
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
                    Toast.makeText(requireContext(), "Name must not be empty", Toast.LENGTH_SHORT).show()
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
        var changed = false
        val dateInMillis = selectedDate
        calendar.timeInMillis = dateInMillis
        val datePickerDialog = DatePickerDialog(
            requireContext(),
            { _, year, month, dayOfMonth ->
                // Встановлюємо максимальну дату в TextView
                calendar.set(year, month, dayOfMonth)
                maxDate = calendar.timeInMillis
                val dateFormat = PreferenceManager.getDefaultSharedPreferences(requireContext()).
                getString("date_format_preference", "dd/MM/yyyy")
                val formattedDate = SimpleDateFormat(dateFormat, Locale.getDefault()).format(calendar.time)
                binding.textMaxEndDate.text = formattedDate
                changed = true
            },
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        )
        datePickerDialog.show()
        Log.d("Changed", changed.toString())
        if (!changed){
            calendar.timeInMillis = selectedDate + 900000
            val dateFormat = PreferenceManager.getDefaultSharedPreferences(requireContext()).
            getString("date_format_preference", "dd/MM/yyyy")
            val formattedDate = SimpleDateFormat(dateFormat, Locale.getDefault()).format(calendar.time)
            binding.textMaxEndDate.text = formattedDate
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}