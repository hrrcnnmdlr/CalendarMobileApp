package com.example.calendar.fragments

import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.preference.PreferenceManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.calendar.R
import com.example.calendar.database.EventViewModel
import com.example.calendar.database.LessonAdapter
import com.example.calendar.databinding.FragmentScheduleDayBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
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

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        var calendar = Calendar.getInstance().apply {
            timeZone = TimeZone.getDefault() // встановлення локального часового поясу
            timeInMillis = selectedDay // date - це час у мілісекундах
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }
        Log.d("TAG", "${calendar.timeInMillis / 86400000 * 86400000} ${calendar.timeInMillis}")
        selectedDay = calendar.timeInMillis

        val month = calendar.get(Calendar.MONTH)
        val months = resources.getStringArray(R.array.months)
        val weekDays = resources.getStringArray(R.array.week_days)
        // встановлюємо назву місяця і рік в текстові поля
        binding.month.text = months[month]
        binding.year.text = calendar.get(Calendar.YEAR).toString()
        binding.date1.text = calendar.get(Calendar.DAY_OF_MONTH).toString()
        binding.dayOfWeek.text = weekDays[calendar.get(Calendar.DAY_OF_WEEK)-1]

        val lessons = listOf(binding.lessonFirst, binding.lessonSecond,
            binding.lessonThird, binding.lessonForth, binding.lessonFifth, binding.lessonSixth)
        for (item in lessons) {
            val linearLayoutManager = LinearLayoutManager(requireContext())
            item.layoutManager = linearLayoutManager
            item.visibility = View.VISIBLE
        }
        val sharedPrefs = PreferenceManager.getDefaultSharedPreferences(requireContext())
        lifecycleScope.launch(Dispatchers.IO) {
            val viewModel: EventViewModel by viewModels()
            val date: Long = calendar.timeInMillis
            var i = 1
            for (item in lessons) {
                var timePreference = sharedPrefs.getString("start_time_preference$i", "00:00")
                var timeParts = timePreference?.split(":")
                var hour = timeParts?.get(0)?.toInt()
                var minute = timeParts?.get(1)?.toInt()
                calendar = Calendar.getInstance().apply {
                    timeZone = TimeZone.getDefault() // встановлення локального часового поясу
                    timeInMillis = date // date - це час у мілісекундах
                    hour?.let { set(Calendar.HOUR_OF_DAY, it) } // hour - це година від користувацьких налаштувань
                    minute?.let { set(Calendar.MINUTE, it) } // minute - це хвилина від користувацьких налаштувань
                    set(Calendar.SECOND, 0)
                    set(Calendar.MILLISECOND, 0)
                }
                val startTime = calendar.timeInMillis
                timePreference = sharedPrefs.getString("end_time_preference$i", "00:00")
                timeParts = timePreference?.split(":")
                hour = timeParts?.get(0)?.toInt()
                minute = timeParts?.get(1)?.toInt()
                Log.d("TAG", "$hour  $minute ${calendar.timeInMillis} ${calendar.get(Calendar.DAY_OF_MONTH)}")
                calendar = Calendar.getInstance().apply {
                    timeZone = TimeZone.getDefault() // встановлення локального часового поясу
                    timeInMillis = date // date - це час у мілісекундах
                    hour?.let { set(Calendar.HOUR_OF_DAY, it) } // hour - це година від користувацьких налаштувань
                    minute?.let { set(Calendar.MINUTE, it) } // minute - це хвилина від користувацьких налаштувань
                    set(Calendar.SECOND, 0)
                    set(Calendar.MILLISECOND, 0)
                }
                val endTime = calendar.timeInMillis
//                Log.d("TAG", "start_time_preference$i = ${sharedPrefs.getString("start_time_preference$i", "00:00")} " +
//                        "end_time_preference$i = ${sharedPrefs.getString("end_time_preference$i", "00:00")}")
//                Log.d("TAG", "$startTime  $endTime")
                val event = viewModel.getScheduleEvent(startTime, endTime)
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
                i++
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