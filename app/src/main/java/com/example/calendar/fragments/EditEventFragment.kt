package com.example.calendar.fragments

import android.app.AlertDialog
import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Context
import android.os.Bundle
import android.os.Handler
import android.os.Looper
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
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.calendar.database.*
import com.example.calendar.databinding.FragmentEditEventBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.*

class EditEventFragment : Fragment() {
    private var _binding: FragmentEditEventBinding? = null
    private val binding get() = _binding
    private val viewModel: EventViewModel by viewModels()
    private var startDateTime: Long = 0
    private var endDateTime: Long = 0


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentEditEventBinding.inflate(inflater, container, false)
        return binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        var selectedCategoryId = -1
        val args = arguments
        val eventId = args?.getInt("event_id")
        if (eventId == null) {
            Toast.makeText(requireContext(), "Invalid event id", Toast.LENGTH_SHORT).show()
            findNavController().navigateUp()
        }
        var event: Event? = null
        lifecycleScope.launch {
            withContext(Dispatchers.IO) {
                if (eventId != null) {
                    event = viewModel.getEventById(eventId)
                    Log.d("EDITEVENT", "$event")
                }
                if (event != null) {
                    startDateTime = event!!.startDateTime
                    endDateTime = event!!.endDateTime
                    Handler(Looper.getMainLooper()).post {
                        binding?.eventNameEditText?.setText(event!!.eventName)
                        binding?.eventDescriptionEditText?.setText(event!!.description)
                        binding?.locationEditText?.setText(event!!.location)
                    }
                    selectedCategoryId = event!!.categoryId
                    binding?.categorySpinner2?.setSelection(selectedCategoryId - 1)
                } else {
                    Toast.makeText(requireContext(), "Event not found", Toast.LENGTH_SHORT).show()
                    findNavController().navigateUp()
                }
                Handler(Looper.getMainLooper()).post {
                    binding?.startDateTimeTextView?.text = SimpleDateFormat(
                        "yyyy/MM/dd HH:mm",
                        Locale.getDefault()
                    ).format(Date(startDateTime))
                    Log.d(
                        "EDITTIMES", "${
                            SimpleDateFormat(
                                "yyyy/MM/dd HH:mm",
                                Locale.getDefault()
                            ).format(Date(startDateTime))
                        } $endDateTime"
                    )
                    binding?.endDateTimeTextView?.text = SimpleDateFormat(
                        "yyyy/MM/dd HH:mm",
                        Locale.getDefault()
                    ).format(Date(endDateTime))
                }
            }
        }

        val eventViewModel =
            ViewModelProvider(requireActivity())[EventViewModel::class.java]
        val categories = mutableListOf<String>()
        val db = MainDB.getDatabase(requireContext())
        val categoryDao = db.categoryDao()

        fun getAllCategories(context: Context) {
            categoryDao.getAllCategories().observeForever { categoriesList ->
                categories.clear()
                categories.addAll(categoriesList.map { it.name }) // map categories to their names
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
                binding?.categorySpinner2?.adapter = adapter1

                if (categories.size > 0) {
                    selectedCategoryId = if (event != null) {
                        (event?.categoryId ?: binding?.categorySpinner2?.setSelection(event!!.categoryId - 1)) as Int
                    } else {
                        0
                    }
                }
            }
        }
        getAllCategories(requireContext())
        if (categories.size > 0) {
            if (eventId?.let { viewModel.getEventById(it) } == event) {
                selectedCategoryId = event!!.categoryId
            }
        }

        val adapter1 =
            ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, categories)
        binding?.categorySpinner2?.adapter = adapter1
        binding?.categorySpinner2?.onItemSelectedListener =
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
        if (event != null) {
            selectedCategoryId = event!!.categoryId
            binding?.categorySpinner2?.setSelection(selectedCategoryId - 1)
        }
        binding?.categorySpinner2?.setSelection(selectedCategoryId - 1) //event.category)

        var selectedRepetition = EventRepetition.NONE
        val eventRepetition = listOf(
            EventRepetition.NONE,
            EventRepetition.DAILY, EventRepetition.WEEKLY,
            EventRepetition.MONTHLY, EventRepetition.ANNUALLY
        )
        val adapter2 = ArrayAdapter(
            requireContext(),
            android.R.layout.simple_spinner_item,
            eventRepetition
        )
        adapter2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding?.repeatSpinner2?.onItemSelectedListener =
            object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(
                    parent: AdapterView<*>?,
                    view: View?,
                    position: Int,
                    id: Long
                ) {
                    if (eventRepetition[position] != EventRepetition.NONE) {
                        // Відкриваємо діалогове вікно для вибору максимальної дати
                        binding?.let { event?.let { it1 -> showMaxEndDateDialog(it, it1) } }
                    }
                    selectedRepetition = eventRepetition[position]
                }

                override fun onNothingSelected(parent: AdapterView<*>?) {
                    // не робіть нічого
                }
            }
        binding?.repeatSpinner2?.adapter = adapter2
        if (event != null) {
            var i = 0
            do {
                binding?.repeatSpinner2?.setSelection(i + 1); ++i
            } while
                    (event!!.repeat != eventRepetition[i-1].toString())
            binding?.reminder5Min2?.isChecked = event!!.remind5MinutesBefore
            binding?.reminder15Min2?.isChecked = event!!.remind15MinutesBefore
            binding?.reminder30Min2?.isChecked = event!!.remind30MinutesBefore
            binding?.reminder1Hour2?.isChecked = event!!.remind1HourBefore
            binding?.reminder1Day2?.isChecked = event!!.remind1DayBefore
        }

        binding?.saveButton?.setOnClickListener {
            if (binding?.eventNameEditText?.text.toString().isEmpty()) {
                Toast.makeText(
                    requireContext(),
                    "Event name cannot be empty",
                    Toast.LENGTH_SHORT
                ).show()
                return@setOnClickListener
            }

            if (startDateTime >= endDateTime) {
                Toast.makeText(
                    requireContext(),
                    "Invalid start/end time",
                    Toast.LENGTH_SHORT
                )
                    .show()
                return@setOnClickListener
            }


            val updatedEvent = event?.copy(
                eventName = binding?.eventNameEditText?.text.toString(),
                description = binding?.eventDescriptionEditText?.text.toString(),
                startDateTime = startDateTime, // Встановити дату та час початку події
                endDateTime = endDateTime, // Встановити дату та час закінчення події
                location = binding?.locationEditText?.text.toString(),
                categoryId = selectedCategoryId,
                remind5MinutesBefore = binding!!.reminder5Min2.isChecked,
                remind15MinutesBefore = binding!!.reminder15Min2.isChecked,
                remind30MinutesBefore = binding!!.reminder30Min2.isChecked,
                remind1HourBefore = binding!!.reminder1Hour2.isChecked,
                remind1DayBefore = binding!!.reminder1Hour2.isChecked,
                repeat = selectedRepetition.toString()
            )
            lifecycleScope.launch(Dispatchers.IO) {
                withContext(Dispatchers.IO) {
                    if (event != null && updatedEvent != null) {
                        if (event!!.repeat != EventRepetition.NONE.toString()) {
                            val alertDialogBuilder =
                                androidx.appcompat.app.AlertDialog.Builder(requireContext())
                            alertDialogBuilder.apply {
                                setTitle("Update Event")
                                setMessage("What do you want to update?")
                                setPositiveButton("All Repeated Events") { _, _ ->
                                    lifecycleScope.launch {
                                        withContext(Dispatchers.IO) {
                                            eventViewModel.update(updatedEvent, maxDate, updateAll = true)
                                        }
                                    }
                                    Toast.makeText(
                                        requireContext(),
                                        "All repeated events updated",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                    findNavController().navigateUp()
                                }
                                setNegativeButton("Just This One") { _, _ ->
                                    lifecycleScope.launch {
                                        withContext(Dispatchers.IO) {
                                            eventViewModel.update(updatedEvent, maxDate, updateOne = true)
                                        }
                                    }
                                    Toast.makeText(
                                        requireContext(),
                                        "Event updated",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                    findNavController().navigateUp()
                                }
                                setNeutralButton("This And All Next Events") { _, _ ->
                                    lifecycleScope.launch {
                                        withContext(Dispatchers.IO) {
                                            eventViewModel.update(updatedEvent, maxDate, updateNext = true)
                                        }
                                    }
                                    Toast.makeText(
                                        requireContext(),
                                        "This and next events updated",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                    findNavController().navigateUp()
                                }
                            }
                            alertDialogBuilder.create().show()
                        } else {
                            lifecycleScope.launch {
                                withContext(Dispatchers.IO) {
                                    eventViewModel.update(updatedEvent)
                                }
                            }
                            Toast.makeText(
                                requireContext(),
                                "Event updated",
                                Toast.LENGTH_SHORT
                            ).show()
                            findNavController().navigateUp()
                        }
                    }
                }
            }
            findNavController().navigateUp()

            binding?.cancelButton?.setOnClickListener {
                findNavController().navigateUp()
            }

            binding?.startDateTimeTextView?.setOnClickListener {
                if (event != null) {
                    showDateTimePicker(true, event!!.startDateTime, binding!!)
                }
            }

            binding?.endDateTimeTextView?.setOnClickListener {
                if (event != null) {
                    this.showDateTimePicker(false, event!!.endDateTime, binding!!)
                }
            }
        }
    }
    // Метод для відображення діалогового вікна вибору дати та часу
    private fun showDateTimePicker(isStartDate: Boolean, time: Long, binding: FragmentEditEventBinding) {
        val calendar = Calendar.getInstance()
        calendar.timeInMillis = time
        val datePickerDialog = DatePickerDialog(
            requireContext(),
            { _, year, month, dayOfMonth ->
                calendar.set(year, month, dayOfMonth)
                val timePickerDialog = TimePickerDialog(
                    requireContext(),
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

    fun showAddCategoryDialog(adapter: ArrayAdapter<String>, eventViewModel: EventViewModel, categories: MutableList<String>) {
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
                    Toast.makeText(
                        requireContext(),
                        "Name must not be empty",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
            .setNegativeButton("Cansel") { dialog, _ -> dialog.cancel() }
            .create()
        dialog.show()
    }
    private var maxDate: Long = -1L
    // Функція для відображення діалогового вікна для вибору максимальної дати
    fun showMaxEndDateDialog(binding: FragmentEditEventBinding, event: Event) {
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
                val formattedDate =
                    SimpleDateFormat("dd.MM.yyyy", Locale.getDefault()).format(calendar.time)
                binding.textMaxEndDate2.text = formattedDate
                changed = true
            },
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        )
        datePickerDialog.show()
        Log.d("Changed", changed.toString())
        if (!changed) {
            if (event.maxDateForRepeat != 0L) {
                if (event.maxDateForRepeat != null) {
                    calendar.timeInMillis = event.maxDateForRepeat
                    Log.d("Changed22", event.maxDateForRepeat.toString())
                } else {
                    calendar.timeInMillis = selectedDate + 90000
                    Log.d(
                        "Changed33",
                        (selectedDate + 90000).toString()
                    )
                }
                val formattedDate =
                    SimpleDateFormat("dd.MM.yyyy", Locale.getDefault()).format(calendar.time)
                binding.textMaxEndDate2.text = formattedDate
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}