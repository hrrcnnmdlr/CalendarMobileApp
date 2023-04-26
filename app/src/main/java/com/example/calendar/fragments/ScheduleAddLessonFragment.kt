package com.example.calendar.fragments

import android.R
import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.core.view.get
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.calendar.database.*
import com.example.calendar.databinding.FragmentScheduleAddLessonBinding
import kotlinx.coroutines.*

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
        var selectedNumberOfLesson: Int = 0
        var startDateTime = 0L
        var endDateTime = 0L

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
                startDateTime = startDateTime, // Встановити дату та час початку події
                endDateTime = endDateTime, // Встановити дату та час закінчення події
                location = "Class",
                categoryId = 2,
                remind5MinutesBefore = true,
                remind15MinutesBefore = true,
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