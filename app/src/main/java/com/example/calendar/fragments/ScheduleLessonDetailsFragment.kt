package com.example.calendar.fragments

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.preference.PreferenceManager
import com.example.calendar.R
import com.example.calendar.database.Event
import com.example.calendar.database.EventRepetition
import com.example.calendar.database.EventViewModel
import com.example.calendar.database.Schedule
import com.example.calendar.databinding.FragmentScheduleLessonDetailsBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.*

class ScheduleLessonDetailsFragment : Fragment() {
    private var _binding: FragmentScheduleLessonDetailsBinding? = null
    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!
    private lateinit var eventViewModel: EventViewModel
    private var eventIdDetails: Int = -1

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentScheduleLessonDetailsBinding.inflate(inflater, container, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        eventIdDetails = eventId
        // Створити ViewModel для доступу до даних
        eventViewModel = ViewModelProvider(this)[EventViewModel::class.java]
        lifecycleScope.launch {
            val event: Event
            val lesson: Schedule
            withContext(Dispatchers.IO) {
                event = eventIdDetails.let { eventViewModel.getEventById(it) }
                lesson = eventIdDetails.let { eventViewModel.getClassById(it) }
            }
            val dateFormat = PreferenceManager.getDefaultSharedPreferences(requireContext())
                .getString("date_format_preference", "dd/MM/yyyy")
            val timeFormat = PreferenceManager.getDefaultSharedPreferences(requireContext())
                .getString("time_format_preference", "HH:mm")
            // Відобразити дані про зустріч
            binding.lessonName.text = event.eventName
            binding.lessonDescription.text = event.description
            binding.homeWork.text = lesson.homework
            binding.attendance.text = lesson.attendance.toString()
            binding.grade.text = lesson.obtainedGrade.toString()
            binding.completedHomework.text = lesson.completedHomework.toString()
            binding.numberOfLesson.text = lesson.classNumber.toString()
            // Додати обробник для кнопки редагування
            binding.editEventButton2.setOnClickListener {
                lifecycleScope.launch {
                    findNavController().navigate(R.id.nav_schedule_edit)
                }
            }
            // Додати обробник для кнопки видалення
            binding.deleteEventButton2.setOnClickListener {
                val alertDialogBuilder = AlertDialog.Builder(requireContext())
                alertDialogBuilder.apply {
                    setTitle(getString(R.string.delete_lesson_title))
                    setMessage(getString(R.string.delete_lesson_message))
                    setPositiveButton(getString(R.string.delete_lesson_all_button)) { _, _ ->
                        lifecycleScope.launch {
                            withContext(Dispatchers.IO) {
                                eventViewModel.deleteAllRepeatedClasses(event)
                            }
                        }
                        Toast.makeText(
                            requireContext(),
                            getString(R.string.all_recurring_deleted),
                            Toast.LENGTH_SHORT
                        ).show()
                        findNavController().navigateUp()
                    }
                    setNegativeButton(getString(R.string.delete_lesson_one_button)) { _, _ ->
                        lifecycleScope.launch {
                            withContext(Dispatchers.IO) {
                                eventViewModel.deleteClass(lesson, event)
                            }
                        }
                        Toast.makeText(
                            requireContext(),
                            getString(R.string.lesson_deleted),
                            Toast.LENGTH_SHORT
                        ).show()
                        findNavController().navigateUp()
                    }
                    setNeutralButton(getString(R.string.delete_lesson_next_button)) { _, _ ->
                        lifecycleScope.launch {
                            withContext(Dispatchers.IO) {
                                eventViewModel.deleteAllNextClasses(event)
                            }
                        }
                        Toast.makeText(
                            requireContext(),
                            getString(R.string.this_next_deleted),
                            Toast.LENGTH_SHORT
                        ).show()
                        findNavController().navigateUp()
                    }
                }
                alertDialogBuilder.create().show()
            }
            Log.d("EVENT", "$event")
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}