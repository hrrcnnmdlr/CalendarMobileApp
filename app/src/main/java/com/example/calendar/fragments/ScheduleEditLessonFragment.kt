package com.example.calendar.fragments

import android.R
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
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.preference.PreferenceManager
import com.example.calendar.database.Event
import com.example.calendar.database.EventRepetition
import com.example.calendar.database.EventViewModel
import com.example.calendar.database.Schedule
import com.example.calendar.databinding.FragmentScheduleEditLessonBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.*

class ScheduleEditLessonFragment : Fragment() {
    private var _binding: FragmentScheduleEditLessonBinding? = null
    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!
    private val viewModel: EventViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentScheduleEditLessonBinding.inflate(inflater, container, false)

        return binding.root
    }

    override fun onResume() {
        super.onResume()
        lifecycleScope.launch {
            updateData(binding)
        }
    }

    private val eventIdEdit = eventId
    private var event: Event? = null
    private var lesson: Schedule? = null
    private var selectedNumberOfLesson = 0
    private var startTime = 0L
    private var endTime = 0L
    private var startDate = 0L
    private suspend fun updateData(binding: FragmentScheduleEditLessonBinding) {
        withContext(Dispatchers.IO) {
            event = viewModel.getEventById(eventIdEdit)
            lesson = viewModel.getClassById(eventIdEdit)
            Log.d("EDIT EVENT", "$eventIdEdit")
            if (event != null) {
                val calendar = Calendar.getInstance().apply {
                    timeZone = TimeZone.getDefault() // встановлення локального часового поясу
                    timeInMillis = event!!.startDateTime // date - це час у мілісекундах
                    set(Calendar.HOUR_OF_DAY, 0)
                    set(Calendar.MINUTE, 0)
                    set(Calendar.SECOND, 0)
                    set(Calendar.MILLISECOND, 0)
                }
                startDate = calendar.timeInMillis
                Log.d("EDIT CLASS", "$startDate ${event!!.startDateTime}")
                val dateFormat = PreferenceManager.getDefaultSharedPreferences(requireContext())
                    .getString("date_format_preference", "dd/MM/yyyy")
                withContext(Dispatchers.Main) {
                    binding.startDateTimeTextView3.text = SimpleDateFormat("$dateFormat", Locale.getDefault()
                    ).format(Date(startDate))
                    binding.lessonNameEditText2.setText(event!!.eventName)
                    binding.lessonDescriptionEditText2.setText(event!!.description)
                    binding.spinnerLessons2.setSelection(lesson!!.classNumber - 1)
                    binding.switchAttendance.isChecked = lesson!!.attendance == true
                    binding.homeworkSwitch.isChecked = lesson!!.completedHomework == true
                    binding.obtainedGrade.setText(lesson!!.obtainedGrade.toString())
                }
            } else {
                Toast.makeText(requireContext(), "Class not found", Toast.LENGTH_SHORT).show()
                findNavController().navigateUp()
            }
            // Set selected category ID after loading categories
            selectedNumberOfLesson = lesson!!.classNumber
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val eventViewModel = ViewModelProvider(requireActivity())[EventViewModel::class.java]
        val sharedPrefs = PreferenceManager.getDefaultSharedPreferences(requireContext())
        // Налаштування адаптерів та спінерів
        val numbersOfClass = arrayOf("1", "2", "3", "4", "5", "6")
        val adapter1 = ArrayAdapter(requireContext(), R.layout.simple_spinner_item, numbersOfClass)
        binding.spinnerLessons2.adapter = adapter1
        binding.spinnerLessons2.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
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
                Log.d("DATE TIME", "startTime = $startTimeCal $startTime\n" +
                        "                endTime =$endTimeCal $endTime")
            }
            override fun onNothingSelected(parent: AdapterView<*>?) {
                // Нічого не робимо, якщо нічого не вибрано
            }
        }
        lifecycleScope.launch {
            updateData(binding)
        }
        binding.addEventButton4.setOnClickListener {
            if (binding.lessonNameEditText2.text.toString().isEmpty()) {
                Toast.makeText(
                    requireContext(),
                    "Class name cannot be empty",
                    Toast.LENGTH_SHORT
                ).show()
                return@setOnClickListener
            }
            val updatedEvent = event!!.copy(
                eventName = binding.lessonNameEditText2.text.toString(),
                description = binding.lessonDescriptionEditText2.text.toString(),
                startDateTime = startDate + startTime, // Встановити дату та час початку події
                endDateTime = startDate + endTime // Встановити дату та час закінчення події
            )
            var obtainedGrade = 0F
            obtainedGrade = if (binding.obtainedGrade.text.toString() == "null") 0F
            else binding.obtainedGrade.text.toString().toFloat()
            val updatedLesson = lesson!!.copy(
                classNumber = selectedNumberOfLesson,
                obtainedGrade = obtainedGrade,
                homework = binding.homeWork.text.toString(),
                attendance = binding.switchAttendance.isChecked,
                completedHomework = binding.homeworkSwitch.isChecked,
            )
            lifecycleScope.launch {
                withContext(Dispatchers.IO) {
                    if (event != null) {
                        val alertDialogBuilder =
                            androidx.appcompat.app.AlertDialog.Builder(requireContext())
                        alertDialogBuilder.apply {
                            setTitle("Update Classes")
                            setMessage("What do you want to update?")
                            setPositiveButton("All Repeated Classes") { _, _ ->
                                lifecycleScope.launch {
                                    withContext(Dispatchers.IO) {
                                        Log.d("UPDATE all", "$updatedEvent $updatedLesson")
                                        eventViewModel.updateClass(
                                            updatedEvent, updatedLesson, updateAll = true
                                        )
                                    }
                                }
                                Toast.makeText(
                                    context,
                                    "All repeated classes updated",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                            setNegativeButton("Just This One") { _, _ ->
                                lifecycleScope.launch {
                                    withContext(Dispatchers.IO) {
                                        Log.d("UPDATE one", "$updatedEvent $updatedLesson")
                                        eventViewModel.updateClass(
                                            updatedEvent, updatedLesson, updateOne = true
                                        )
                                    }
                                }
                                Toast.makeText(
                                    context,
                                    "Class updated",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                            setNeutralButton("This And All Next Classes") { _, _ ->
                                lifecycleScope.launch {
                                    withContext(Dispatchers.IO) {
                                        Log.d("UPDATE next", "$updatedEvent $updatedLesson")
                                        eventViewModel.updateClass(
                                            updatedEvent, updatedLesson, updateNext = true
                                        )
                                    }
                                }
                                Toast.makeText(
                                    context,
                                    "This and next classes updated",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        }
                        requireActivity().runOnUiThread {
                            alertDialogBuilder.create().show()
                        }
                    }
                }
            }
            findNavController().navigateUp()
        }
        binding.startDateTimeTextView3.setOnClickListener {
            showDateTimePicker(event!!.startDateTime, binding)
        }
    }

    private fun showDateTimePicker(time: Long, binding: FragmentScheduleEditLessonBinding) {
        val calendar = Calendar.getInstance(TimeZone.getDefault())
        calendar.timeInMillis = time
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
                binding.startDateTimeTextView3.text = SimpleDateFormat(
                    "$dateFormat",
                    Locale.getDefault()
                ).format(calendar.time)
                startDate = calendar.timeInMillis
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