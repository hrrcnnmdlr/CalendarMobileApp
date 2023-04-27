package com.example.calendar.fragments

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.preference.PreferenceManager
import com.example.calendar.R
import com.example.calendar.database.*
import com.example.calendar.databinding.FragmentScheduleAddLessonBinding
import kotlinx.coroutines.*
import java.text.SimpleDateFormat
import java.util.*

class ScheduleAddLessonFragment : Fragment() {
    private var _binding: FragmentScheduleAddLessonBinding? = null
    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentScheduleAddLessonBinding.inflate(inflater, container, false)

        return binding.root
    }

    @OptIn(DelicateCoroutinesApi::class)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        // Створення binding для Activity
        val eventViewModel = ViewModelProvider(this)[EventViewModel::class.java]
        var selectedNumberOfLesson = 0
        var selectedNumberOfDay = 0
        var startDate = 0L
        var startTime = 0L
        var endTime = 0L

        val numbersOfClass = arrayOf("1", "2", "3", "4", "5", "6")
        val adapter1 = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, numbersOfClass)
        binding.spinnerLessons.adapter = adapter1
        binding.spinnerLessons.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                // Зміна значення Spinner
                selectedNumberOfLesson = position + 1 // Додаємо 1, оскільки індекс починається з 0
                val sharedPrefs = PreferenceManager.getDefaultSharedPreferences(requireContext())
                var timePreference = sharedPrefs.getString("start_time_preference$selectedNumberOfDay", "00:00")
                var timeParts = timePreference?.split(":")
                var cal = Calendar.getInstance().apply {
                    timeZone = TimeZone.getDefault() // встановлення локального часового поясу
                    timeInMillis = 0
                    timeParts?.get(0)?.let { set(Calendar.HOUR_OF_DAY, it.toInt()) }
                    timeParts?.get(1)?.let { set(Calendar.MINUTE, it.toInt()) }
                }
                startTime = cal.timeInMillis
                timePreference = sharedPrefs.getString("end_time_preference$selectedNumberOfDay", "00:00")
                timeParts = timePreference?.split(":")
                cal = Calendar.getInstance().apply {
                    timeZone = TimeZone.getDefault() // встановлення локального часового поясу
                    timeInMillis = 0
                    timeParts?.get(0)?.let { set(Calendar.HOUR_OF_DAY, it.toInt()) }
                    timeParts?.get(1)?.let { set(Calendar.MINUTE, it.toInt()) }
                }
                endTime = cal.timeInMillis - startTime
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                // Нічого не робимо, якщо нічого не вибрано
            }
        }
        var numbersOfDays = resources.getStringArray(R.array.week_days)
        numbersOfDays = numbersOfDays.copyOfRange(1, numbersOfDays.size)
        val adapter2 = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, numbersOfDays)
        binding.spinnerDays.adapter = adapter2
        binding.spinnerDays.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                // Зміна значення Spinner
                selectedNumberOfDay = position + 1 // Додаємо 1, оскільки індекс починається з 0
                val sharedPrefs = PreferenceManager.getDefaultSharedPreferences(requireContext())
                val minDate = sharedPrefs.getString("start_date_preference", "2023-09-01")
                val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                val date = minDate?.let { dateFormat.parse(it) }
                val calendar = Calendar.getInstance().apply {
                    timeZone = TimeZone.getDefault() // встановлення локального часового поясу
                    if (date != null) {
                        time = date
                        set(Calendar.DAY_OF_WEEK, selectedNumberOfDay)
                    }
                }
                startDate = calendar.timeInMillis
                var timePreference = sharedPrefs.getString("start_time_preference$selectedNumberOfDay", "00:00")
                var timeParts = timePreference?.split(":")
                var cal = Calendar.getInstance().apply {
                    timeZone = TimeZone.getDefault() // встановлення локального часового поясу
                    timeInMillis = 0
                    timeParts?.get(0)?.let { set(Calendar.HOUR_OF_DAY, it.toInt()) }
                    timeParts?.get(1)?.let { set(Calendar.MINUTE, it.toInt()) }
                }
                startTime = cal.timeInMillis
                timePreference = sharedPrefs.getString("end_time_preference$selectedNumberOfDay", "00:00")
                timeParts = timePreference?.split(":")
                cal = Calendar.getInstance().apply {
                    timeZone = TimeZone.getDefault() // встановлення локального часового поясу
                    timeInMillis = 0
                    timeParts?.get(0)?.let { set(Calendar.HOUR_OF_DAY, it.toInt()) }
                    timeParts?.get(1)?.let { set(Calendar.MINUTE, it.toInt()) }
                }
                endTime = cal.timeInMillis
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                // Нічого не робимо, якщо нічого не вибрано
            }
        }
        val sharedPrefs = PreferenceManager.getDefaultSharedPreferences(requireContext())
        val endDate = sharedPrefs.getString("end_date_preference", "2023-05-30")
        val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val date = endDate?.let { dateFormat.parse(it) }
        val calendar = Calendar.getInstance().apply {
            timeZone = TimeZone.getDefault() // встановлення локального часового поясу
            if (date != null) {
                time = date
                set(Calendar.DAY_OF_WEEK, selectedNumberOfDay)
            }
        }
        val maxDate = calendar.timeInMillis
        // Обробник натискання на кнопку додавання події
        binding.addEventButton2.setOnClickListener {
            if (binding.lessonNameEditText.text.toString().isEmpty()) {
                Toast.makeText(
                    requireContext(),
                    "Class name cannot be empty",
                    Toast.LENGTH_SHORT
                ).show()
                return@setOnClickListener
            }

            // Створити об'єкт події і додати його до бази даних
            val event = Event(
                eventName = binding.lessonNameEditText.text.toString(),
                description = binding.lessonDescriptionEditText.text.toString(),
                startDateTime = startDate + startTime, // Встановити дату та час початку події
                endDateTime = startDate + endTime, // Встановити дату та час закінчення події
                location = "Class",
                categoryId = 2,
                remind5MinutesBefore = true,
                remind15MinutesBefore = true,
                maxDateForRepeat = maxDate
            )
            val lesson = Schedule(
                eventId = 0,
                homework = binding.editTextHomeWork.text.toString(),
                classNumber = selectedNumberOfLesson
            )
            // Отримати доступ до бази даних та додати об'єкт події до неї
            GlobalScope.launch(Dispatchers.IO) {
                val alertDialogBuilder =
                    androidx.appcompat.app.AlertDialog.Builder(requireContext())
                alertDialogBuilder.apply {
                    setTitle("Insert Сlasses")
                    setMessage("What do you want to insert?")
                    setPositiveButton("All Сlasses") { _, _ ->
                        lifecycleScope.launch {
                            withContext(Dispatchers.IO) {
                                eventViewModel.insertClass(lesson, event)
                            }
                        }
                        Toast.makeText(
                            context,
                            "Classes insert",
                            Toast.LENGTH_SHORT
                        ).show()
                        Log.d("EVENT", "$lesson $event")
                    }
                    setNegativeButton("Just One") { _, _ ->
                        lifecycleScope.launch {
                            withContext(Dispatchers.IO) {
                                eventViewModel.insertUniqueClass(lesson, event)
                            }
                        }
                        Toast.makeText(
                            context,
                            "Сlass insert",
                            Toast.LENGTH_SHORT
                        ).show()
                        Log.d("EVENT", "$lesson $event")
                    }
                }
                requireActivity().runOnUiThread {
                    alertDialogBuilder.create().show()
                }
                eventViewModel.insertClass(lesson, event)
            }
            // Показати повідомлення користувачеві про успішне додавання події
            findNavController().navigateUp()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}