package com.example.calendar.fragments

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.calendar.R
import com.example.calendar.database.EventViewModel
import com.example.calendar.database.LessonAdapter
import com.example.calendar.database.LessonWeekAdapter
import com.example.calendar.database.Schedule
import com.example.calendar.databinding.FragmentScheduleDayBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.*

class ScheduleDayFragment : Fragment() {
    private var _binding: FragmentScheduleDayBinding? = null
    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentScheduleDayBinding.inflate(inflater, container, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val dateInMillis = selectedDay
        val calendar = Calendar.getInstance()
        calendar.timeInMillis = dateInMillis
        calendar.set(Calendar.HOUR_OF_DAY, 0)
        calendar.set(Calendar.MINUTE, 0)
        calendar.set(Calendar.SECOND, 0)
        calendar.set(Calendar.MILLISECOND, 0)
        selectedDate = calendar.timeInMillis

        val month = calendar.get(Calendar.MONTH)
        val months = resources.getStringArray(R.array.months)
        // встановлюємо назву місяця і рік в текстові поля
        binding.month.text = months[month]
        binding.year.text = calendar.get(Calendar.YEAR).toString()
        binding.date1.text = calendar.get(Calendar.DAY_OF_MONTH).toString()
        binding.dayOfWeek.text = "Monday"

        val lessons = listOf(binding.lessonFirst, binding.lessonSecond,
            binding.lessonThird, binding.lessonForth, binding.lessonFifth, binding.lessonSixth)
        for (item in lessons) {
            val linearLayoutManager = LinearLayoutManager(requireContext())
            item.layoutManager = linearLayoutManager
            item.visibility = View.VISIBLE
        }
        lifecycleScope.launch(Dispatchers.IO) {
            val viewModel: EventViewModel by viewModels()
            val date: Long = selectedDate
            for (item in lessons) {
                val startDateTime = date + 2 // start lesson
                val endDateTime = date + 2 // end lesson
                val event = viewModel.getScheduleEvent(startDateTime, endDateTime)
                lifecycleScope.launch {
                    event.observe(viewLifecycleOwner) { lesson ->
                        if (lesson.isNotEmpty()) {
                            val id = lesson[0].id
                            lifecycleScope.launch {
                                // switch back to the main thread to update the UI
                                val schedule = viewModel.getClassById(id)
                                val mAdapter = LessonAdapter(requireContext(), lesson, schedule)
                                item.adapter = mAdapter
                            }
                        }
                    }
                }
            }
        }
        val calen = Calendar.getInstance()
        calen.timeInMillis = selectedDay
        val controller = findNavController()
        binding.buttonNextDay.setOnClickListener {
            selectedDay = if (calen.get(Calendar.DAY_OF_WEEK) != 7){
                calen.add(Calendar.DAY_OF_MONTH, 1)
                calen.timeInMillis
            } else {
                calen.add(Calendar.DAY_OF_MONTH, 2)
                calen.timeInMillis
            }
            controller.navigateUp()
            controller.navigate(R.id.nav_schedule_day)
        }
        binding.buttonPrevDay.setOnClickListener {
            selectedDay = if (calen.get(Calendar.DAY_OF_WEEK) != 2){
                calen.add(Calendar.DAY_OF_MONTH, -1)
                calen.timeInMillis
            } else {
                calen.add(Calendar.DAY_OF_MONTH, -2)
                calen.timeInMillis
            }
            controller.navigateUp()
            controller.navigate(R.id.nav_schedule_day)
        }
        binding.addLessonButton.setOnClickListener {
            controller.navigate(R.id.nav_schedule_add)
        }

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}