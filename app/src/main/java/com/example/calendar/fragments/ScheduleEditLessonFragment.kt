package com.example.calendar.fragments

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.calendar.database.Event
import com.example.calendar.database.EventRepetition
import com.example.calendar.database.EventViewModel
import com.example.calendar.database.Schedule
import com.example.calendar.databinding.FragmentScheduleEditLessonBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class ScheduleEditLessonFragment : Fragment() {
    private var _binding: FragmentScheduleEditLessonBinding? = null
    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!
    private val viewModel: EventViewModel by viewModels()
    private var startDateTime: Long = 0
    private var endDateTime: Long = 0

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
        updateData()
    }

    private val eventIdEdit = eventId
    private var event: Event? = null
    private var lesson: Schedule? = null
    private var selectedNumberOfLesson: Int = 0
    private fun updateData() {
        val eventViewModel = ViewModelProvider(requireActivity())[EventViewModel::class.java]
        lifecycleScope.launch {
            withContext(Dispatchers.IO) {
                event = viewModel.getEventById(eventIdEdit)
                lesson = viewModel.getClassById(eventIdEdit)
                Log.d("EDITEVENT", "$eventIdEdit")
                if (event != null) {
                    startDateTime = event!!.startDateTime
                    endDateTime = event!!.endDateTime
                    binding.lessonNameEditText2.setText(event!!.eventName)
                    binding.lessonDescriptionEditText2.setText(event!!.description)
                    binding.spinnerLessons2.setSelection(lesson!!.classNumber)
                    binding.switchAttendance.isChecked = lesson!!.attendance == true
                    binding.homeworkSwitch.isChecked = lesson!!.completedHomework == true
                    binding.obtainedGrade.setText(lesson!!.obtainedGrade.toString())
                } else {
                    Toast.makeText(requireContext(), "Class not found", Toast.LENGTH_SHORT).show()
                    findNavController().navigateUp()
                }
                // Set selected category ID after loading categories
                selectedNumberOfLesson = lesson!!.classNumber
            }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        updateData()
        val eventViewModel = ViewModelProvider(requireActivity())[EventViewModel::class.java]
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
                startDateTime = startDateTime, // Встановити дату та час початку події
                endDateTime = endDateTime, // Встановити дату та час закінчення події
                categoryId = 2,

            )
            val updatedLesson = lesson!!.copy(
                classNumber = selectedNumberOfLesson,
                obtainedGrade = binding.obtainedGrade.text.toString().toFloat(),
                homework = binding.homeWork.text.toString(),
                attendance = binding.switchAttendance.isChecked,
                completedHomework = binding.homeworkSwitch.isChecked,
            )
            lifecycleScope.launch {
                withContext(Dispatchers.IO) {
                    if (event != null) {
                        if (event!!.repeat != EventRepetition.NONE.toString()) {
                            val alertDialogBuilder =
                                androidx.appcompat.app.AlertDialog.Builder(requireContext())
                            alertDialogBuilder.apply {
                                setTitle("Update Сlasses")
                                setMessage("What do you want to update?")
                                setPositiveButton("All Repeated Сlasses") { _, _ ->
                                    lifecycleScope.launch {
                                        withContext(Dispatchers.IO) {
                                            Log.d("UPDATE all", "$updatedEvent")
                                            eventViewModel.updateAllRepeatedClasses(
                                                updatedEvent
                                            )
                                        }
                                    }
                                    Toast.makeText(
                                        context,
                                        "All repeated сlasses updated",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }
                                setNegativeButton("Just This One") { _, _ ->
                                    lifecycleScope.launch {
                                        withContext(Dispatchers.IO) {
                                            Log.d("UPDATE one", "$updatedEvent")
                                            eventViewModel.updateClass(
                                                updatedLesson, updatedEvent
                                            )
                                        }
                                    }
                                    Toast.makeText(
                                        context,
                                        "Сlass updated",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }
                                setNeutralButton("This And All Next Сlasses") { _, _ ->
                                    lifecycleScope.launch {
                                        withContext(Dispatchers.IO) {
                                            Log.d("UPDATE next", "$updatedEvent")
                                            eventViewModel.updateAllNextClasses(
                                                updatedEvent
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
            }
            findNavController().navigateUp()
        }

//        binding.cancelButton.setOnClickListener {
//            findNavController().navigateUp()
//        }

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}