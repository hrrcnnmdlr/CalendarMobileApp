package com.example.calendar.fragments

import android.app.DatePickerDialog
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.annotation.RequiresApi
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
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.*

class ScheduleAddLessonFragment : Fragment() {
    private var _binding: FragmentScheduleAddLessonBinding? = null
    // Ця властивість є дійсною лише між onCreateView та onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentScheduleAddLessonBinding.inflate(inflater, container, false)

        return binding.root
    }
    private var startDate = 0L
    private var endDate = 0L
    @RequiresApi(Build.VERSION_CODES.O)
    @OptIn(DelicateCoroutinesApi::class)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        // Створення binding для Activity
        val eventViewModel = ViewModelProvider(this)[EventViewModel::class.java]
        // Початкове значення для Spinner-ів
        var selectedNumberOfLesson = 0
        var startTime = 0L
        var endTime = 0L


        val sharedPrefs = PreferenceManager.getDefaultSharedPreferences(requireContext())
        val startDatePref = sharedPrefs.getString("start_date_preference", "00-00-0000")
        val endDatePref = sharedPrefs.getString("end_date_preference", "00-00-0000")
        Log.d("PREF", "$startDatePref $endDatePref")
        // Формат дати, відповідний формату рядка дати
        val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")

        // Створення екземпляру LocalDate з рядка дати та формату
        val startDateLocal = LocalDate.parse(startDatePref, formatter)
        val endDateLocal = LocalDate.parse(endDatePref, formatter)

        // Отримання року, місяця та дня з екземплярів LocalDate
        val startYear = startDateLocal.year
        val startMonth = startDateLocal.monthValue - 1
        val startDay = startDateLocal.dayOfMonth

        val endYear = endDateLocal.year
        val endMonth = endDateLocal.monthValue - 1
        val endDay = endDateLocal.dayOfMonth

        val calendar = Calendar.getInstance(TimeZone.getDefault()).apply {
            timeInMillis = 0
        }
        calendar.set(startYear, startMonth, startDay)
        val dateFormat = PreferenceManager.getDefaultSharedPreferences(requireContext())
            .getString("date_format_preference", "dd/MM/yyyy")
        binding.startDateTimeTextView2.text = SimpleDateFormat(
                "$dateFormat",
                Locale.getDefault()
            ).format(calendar.time)
        startDate = calendar.timeInMillis
        calendar.timeInMillis = 0
        calendar.set(endYear, endMonth, endDay)
        binding.endDateTimeTextView2.text = SimpleDateFormat(
                "$dateFormat",
                Locale.getDefault()
            ).format(calendar.time)
        endDate = calendar.timeInMillis // Встановити дату та час початку події


        // Налаштування адаптерів та спінерів
        val numbersOfClass = arrayOf("1", "2", "3", "4", "5", "6")
        val adapter1 = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, numbersOfClass)
        binding.spinnerLessons.adapter = adapter1
        binding.spinnerLessons.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                // Зміна значення Spinner
                selectedNumberOfLesson = position + 1 // Додаємо 1, оскільки індекс починається з 0
                // Отримання налаштувань користувача

                // Отримати значення часу початку першої пари з SharedPreferences
                val startTimeString =
                    sharedPrefs.getString("start_time_preference$selectedNumberOfLesson", "00:00")
                val startTimeDateFormat = SimpleDateFormat("HH:mm", Locale.getDefault())
                val startTimeCal = startTimeString?.let { startTimeDateFormat.parse(it) }

                // Отримати значення часу закінчення першої пари з SharedPreferences
                val endTimeString =
                    sharedPrefs.getString("end_time_preference$selectedNumberOfLesson", "00:00")
                val endTimeDateFormat = SimpleDateFormat("HH:mm", Locale.getDefault())
                val endTimeCal = endTimeString?.let { endTimeDateFormat.parse(it) }

                // Використовувати значення у коді
                val calendar3 = Calendar.getInstance().apply {
                    timeZone = TimeZone.getDefault() // встановлення локального часового поясу
                    timeInMillis = 0 // date - це час у мілісекундах
                    set(Calendar.HOUR_OF_DAY, 0)
                    set(Calendar.MINUTE, 0)
                    set(Calendar.SECOND, 0)
                    set(Calendar.MILLISECOND, 0)
                }

                // Встановити час початку пари
                if (startTimeCal != null) {
                    calendar3.set(Calendar.HOUR_OF_DAY, startTimeCal.hours+3)
                    calendar3.set(Calendar.MINUTE, startTimeCal.minutes)
                }
                calendar3.set(Calendar.SECOND, 0)

                startTime = calendar3.timeInMillis // Початок пари

                // Встановити час закінчення пари
                if (endTimeCal != null) {
                    calendar3.set(Calendar.HOUR_OF_DAY, endTimeCal.hours+3)
                    calendar3.set(Calendar.MINUTE, endTimeCal.minutes)
                }

                endTime = calendar3.timeInMillis // Кінець пари
                Log.d("DATETIME", "startDateTime = $startDate + $startTimeCal $startTime\n" +
                "                endDateTime = $startDate + $endTimeCal $endTime")
            }
            override fun onNothingSelected(parent: AdapterView<*>?) {
                // Нічого не робимо, якщо нічого не вибрано
            }
        }
        // Обробник натискання на TextView для вибору дати та часу початку та закінчення події
        binding.startDateTimeTextView2.setOnClickListener {
            showDateTimePicker(true)
            Log.d("DATETIME", "startDateTime = $startDate + $startTime\n" +
                    "                endDateTime = $startDate + $endTime")
        }

        binding.endDateTimeTextView2.setOnClickListener {
            showDateTimePicker(false)
            Log.d("DATETIME", "startDateTime = $startDate + $startTime\n" +
                    "                endDateTime = $startDate + $endTime")
        }

        // Обробник натискання на кнопку додавання події
        binding.addEventButton2.setOnClickListener {
            if (binding.lessonNameEditText.text.toString().isEmpty()) {
                Toast.makeText(
                    requireContext(),
                    getString(com.example.calendar.R.string.class_name_empty_error),
                    Toast.LENGTH_SHORT
                ).show()
                return@setOnClickListener
            }
            if (startDate == 0L || endDate == 0L || startDate > endDate) {
                Toast.makeText(
                    requireContext(),
                    getString(R.string.invalid_time_error),
                    Toast.LENGTH_SHORT
                )
                    .show()
                return@setOnClickListener
            }
            Log.d("DATETIME", "startDateTime = $startDate + $startTime\n" +
                    "                endDateTime = $startDate + $endTime")
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
                maxDateForRepeat = endDate + endTime
            )
            val lesson = Schedule(
                eventId = 0,
                homework = binding.editTextHomeWork.text.toString(),
                classNumber = selectedNumberOfLesson
            )
            Log.d("CREATE", "$event $lesson")
            // Отримати доступ до бази даних та додати об'єкт події до неї
            GlobalScope.launch(Dispatchers.IO) {
                val alertDialogBuilder =
                    androidx.appcompat.app.AlertDialog.Builder(requireContext())
                alertDialogBuilder.apply {
                    setTitle(getString(R.string.insert_classes_title))
                    setMessage(getString(R.string.insert_classes_message))
                    setPositiveButton(getString(R.string.insert_classes_all_button)) { _, _ ->
                        lifecycleScope.launch {
                            withContext(Dispatchers.IO) {
                                eventViewModel.insertClass(lesson, event)
                            }
                        }
                        Toast.makeText(
                            requireContext(),
                            getString(R.string.classes_inserted),
                            Toast.LENGTH_SHORT
                        ).show()
                        Log.d("EVENT", "$lesson $event")
                    }
                    setNegativeButton(getString(R.string.insert_classes_one_button)) { _, _ ->
                        lifecycleScope.launch {
                            withContext(Dispatchers.IO) {
                                eventViewModel.insertUniqueClass(lesson, event)
                            }
                        }
                        Toast.makeText(
                            requireContext(),
                            getString(R.string.class_inserted),
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
    private fun showDateTimePicker(isStartDate: Boolean) {
        val calendar = Calendar.getInstance(TimeZone.getDefault())
        val dateInMillis = selectedDate
        calendar.timeInMillis = dateInMillis
        val datePickerDialog = DatePickerDialog(requireContext(),
            { _, year, month, dayOfMonth ->
                calendar.set(year, month, dayOfMonth)
                calendar.set(Calendar.HOUR_OF_DAY, 0)
                calendar.set(Calendar.MINUTE, 0)
                calendar.set(Calendar.SECOND, 0)
                calendar.set(Calendar.MILLISECOND, 0)
                val dateFormat = PreferenceManager.getDefaultSharedPreferences(requireContext())
                    .getString("date_format_preference", "dd/MM/yyyy")
                if (isStartDate) {
                    binding.startDateTimeTextView2.text = SimpleDateFormat(
                        "$dateFormat",
                        Locale.getDefault()
                    ).format(calendar.time)
                    startDate = calendar.timeInMillis
                } else {
                    binding.endDateTimeTextView2.text = SimpleDateFormat(
                        "$dateFormat",
                        Locale.getDefault()
                    ).format(calendar.time)
                    endDate =
                        calendar.timeInMillis // Встановити дату та час початку події
                }
            },
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        )
        datePickerDialog.show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}