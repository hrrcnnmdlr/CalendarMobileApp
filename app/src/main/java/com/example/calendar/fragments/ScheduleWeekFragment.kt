package com.example.calendar.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.preference.PreferenceManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.calendar.R
import com.example.calendar.database.EventViewModel
import com.example.calendar.database.LessonWeekAdapter
import com.example.calendar.databinding.FragmentScheduleWeekBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.*

var selectedDay: Long = Calendar.getInstance().timeInMillis

class ScheduleWeekFragment : Fragment() {
    private var _binding: FragmentScheduleWeekBinding? = null
    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentScheduleWeekBinding.inflate(inflater, container, false)
        selectedDay = selectedDate
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val dateInMillis = selectedDay
        val calendar = Calendar.getInstance().apply {
            timeZone = TimeZone.getDefault() // встановлення локального часового поясу
            timeInMillis = dateInMillis // date - це час у мілісекундах
            set(Calendar.DAY_OF_WEEK, 2)
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }
        selectedDate = calendar.timeInMillis
        val month = calendar.get(Calendar.MONTH)
        val months = resources.getStringArray(R.array.months)
        // встановлюємо назву місяця і рік в текстові поля
        binding.month.text = months[month]
        binding.year.text = calendar.get(Calendar.YEAR).toString()
        val controller = findNavController()
        for (i in 1..6) {
            val nextDay = calendar.get(Calendar.DAY_OF_MONTH)
            val dateTextView = when (i) {
                1 -> binding.date1
                2 -> binding.date2
                3 -> binding.date3
                4 -> binding.date4
                5 -> binding.date5
                6 -> binding.date6
                else -> null
            }
            dateTextView?.text = nextDay.toString()
            calendar.add(Calendar.DAY_OF_MONTH, 1)
        }
        calendar.add(Calendar.DAY_OF_MONTH, -6)
        binding.date1.setOnClickListener {
            calendar.add(Calendar.DAY_OF_MONTH, 0)
            selectedDay = calendar.timeInMillis
            controller.navigate(R.id.nav_schedule_day)
        }
        binding.date2.setOnClickListener {
            calendar.add(Calendar.DAY_OF_MONTH, 1)
            selectedDay = calendar.timeInMillis
            controller.navigate(R.id.nav_schedule_day)
        }
        binding.date3.setOnClickListener {
            calendar.add(Calendar.DAY_OF_MONTH, 2)
            selectedDay = calendar.timeInMillis
            controller.navigate(R.id.nav_schedule_day)
        }
        binding.date4.setOnClickListener {
            calendar.add(Calendar.DAY_OF_MONTH, 3)
            selectedDay = calendar.timeInMillis
            controller.navigate(R.id.nav_schedule_day)
        }
        binding.date5.setOnClickListener {
            calendar.add(Calendar.DAY_OF_MONTH, 4)
            selectedDay = calendar.timeInMillis
            controller.navigate(R.id.nav_schedule_day)
        }
        binding.date6.setOnClickListener {
            calendar.add(Calendar.DAY_OF_MONTH, 5)
            selectedDay = calendar.timeInMillis
            controller.navigate(R.id.nav_schedule_day)
        }


        val monday = listOf(binding.mondayFirst, binding.mondaySecond,
            binding.mondayThird, binding.mondayForth, binding.mondayFifth, binding.mondaySixth)
        val tuesday = listOf(binding.tuesdayFirst, binding.tuesdaySecond,
            binding.tuesdayThird, binding.tuesdayForth, binding.tuesdayFifth, binding.tuesdaySixth)
        val wednesday = listOf(binding.wednesdayFirst, binding.wednesdaySecond,
            binding.wednesdayThird, binding.wednesdayForth, binding.wednesdayFifth, binding.wednesdaySixth)
        val thursday = listOf(binding.thursdayFirst, binding.thursdaySecond,
            binding.thursdayThird, binding.thursdayForth, binding.thursdayFifth, binding.thursdaySixth)
        val friday = listOf(binding.fridayFirst, binding.fridaySecond,
            binding.fridayThird, binding.fridayForth, binding.fridayFifth, binding.fridaySixth)
        val saturday = listOf(binding.saturdayFirst, binding.saturdaySecond,
            binding.saturdayThird, binding.saturdayForth, binding.saturdayFifth, binding.saturdaySixth)
        for (item in monday) {
            val linearLayoutManager = LinearLayoutManager(requireContext())
            item.layoutManager = linearLayoutManager
            item.visibility = View.VISIBLE
        }
        for (item in tuesday) {
            val linearLayoutManager = LinearLayoutManager(requireContext())
            item.layoutManager = linearLayoutManager
            item.visibility = View.VISIBLE
        }
        for (item in wednesday) {
            val linearLayoutManager = LinearLayoutManager(requireContext())
            item.layoutManager = linearLayoutManager
            item.visibility = View.VISIBLE
        }
        for (item in thursday) {
            val linearLayoutManager = LinearLayoutManager(requireContext())
            item.layoutManager = linearLayoutManager
            item.visibility = View.VISIBLE
        }
        for (item in friday) {
            val linearLayoutManager = LinearLayoutManager(requireContext())
            item.layoutManager = linearLayoutManager
            item.visibility = View.VISIBLE
        }
        for (item in saturday) {
            val linearLayoutManager = LinearLayoutManager(requireContext())
            item.layoutManager = linearLayoutManager
            item.visibility = View.VISIBLE
        }

        lifecycleScope.launch(Dispatchers.IO) {
            val sharedPrefs = PreferenceManager.getDefaultSharedPreferences(requireContext())
            val viewModel: EventViewModel by viewModels()
            var date: Long = selectedDate
            var i = 1
            for (item in monday) {
                var timePreference = sharedPrefs.getString("start_time_preference$i", "00:00")
                var timeParts = timePreference?.split(":")
                var hour = timeParts?.get(0)?.toInt()
                var minute = timeParts?.get(1)?.toInt()
                var cal = Calendar.getInstance().apply {
                    timeZone = TimeZone.getDefault() // встановлення локального часового поясу
                    timeInMillis = date
                    hour?.let { set(Calendar.HOUR_OF_DAY, it) }
                    minute?.let { set(Calendar.MINUTE, it) }
                }
                val startTime = cal.timeInMillis
                timePreference = sharedPrefs.getString("end_time_preference$i", "00:00")
                timeParts = timePreference?.split(":")
                hour = timeParts?.get(0)?.toInt()
                minute = timeParts?.get(1)?.toInt()
                cal = Calendar.getInstance().apply {
                    timeZone = TimeZone.getDefault() // встановлення локального часового поясу
                    timeInMillis = date
                    hour?.let { set(Calendar.HOUR_OF_DAY, it) }
                    minute?.let { set(Calendar.MINUTE, it) }
                }
                val endTime = cal.timeInMillis
                val event = viewModel.getScheduleEvent(startTime, endTime)
                // switch back to the main thread to update the UI
                withContext(Dispatchers.Main) {
                    event.observe(viewLifecycleOwner) { lesson ->
                        // This code will be executed when the LiveData object emits a new value
                        val mAdapter = LessonWeekAdapter(requireContext(), lesson)
                        item.adapter = mAdapter
                    }
                }
                i++
            }
            i = 1
            val cal1 = Calendar.getInstance()
            cal1.timeInMillis = selectedDay
            cal1.add(Calendar.DAY_OF_MONTH, 1)
            date = cal1.timeInMillis
            for (item in tuesday) {
                var timePreference = sharedPrefs.getString("start_time_preference$i", "00:00")
                var timeParts = timePreference?.split(":")
                var hour = timeParts?.get(0)?.toInt()
                var minute = timeParts?.get(1)?.toInt()
                var cal = Calendar.getInstance().apply {
                    timeZone = TimeZone.getDefault() // встановлення локального часового поясу
                    timeInMillis = date
                    hour?.let { set(Calendar.HOUR_OF_DAY, it) }
                    minute?.let { set(Calendar.MINUTE, it) }
                }
                val startTime = cal.timeInMillis
                timePreference = sharedPrefs.getString("end_time_preference$i", "00:00")
                timeParts = timePreference?.split(":")
                hour = timeParts?.get(0)?.toInt()
                minute = timeParts?.get(1)?.toInt()
                cal = Calendar.getInstance().apply {
                    timeZone = TimeZone.getDefault() // встановлення локального часового поясу
                    timeInMillis = date
                    hour?.let { set(Calendar.HOUR_OF_DAY, it) }
                    minute?.let { set(Calendar.MINUTE, it) }
                }
                val endTime = cal.timeInMillis
                val event = viewModel.getScheduleEvent(startTime, endTime)
                // switch back to the main thread to update the UI
                withContext(Dispatchers.Main) {
                    event.observe(viewLifecycleOwner) { lesson ->
                        // This code will be executed when the LiveData object emits a new value
                        val mAdapter = LessonWeekAdapter(requireContext(), lesson)
                        item.adapter = mAdapter
                    }
                }
                i++
            }
            i = 1
            cal1.add(Calendar.DAY_OF_MONTH, 1)
            date = cal1.timeInMillis
            for (item in wednesday) {
                var timePreference = sharedPrefs.getString("start_time_preference$i", "00:00")
                var timeParts = timePreference?.split(":")
                var hour = timeParts?.get(0)?.toInt()
                var minute = timeParts?.get(1)?.toInt()
                var cal = Calendar.getInstance().apply {
                    timeZone = TimeZone.getDefault() // встановлення локального часового поясу
                    timeInMillis = date
                    hour?.let { set(Calendar.HOUR_OF_DAY, it) }
                    minute?.let { set(Calendar.MINUTE, it) }
                }
                val startTime = cal.timeInMillis
                timePreference = sharedPrefs.getString("end_time_preference$i", "00:00")
                timeParts = timePreference?.split(":")
                hour = timeParts?.get(0)?.toInt()
                minute = timeParts?.get(1)?.toInt()
                cal = Calendar.getInstance().apply {
                    timeZone = TimeZone.getDefault() // встановлення локального часового поясу
                    timeInMillis = date
                    hour?.let { set(Calendar.HOUR_OF_DAY, it) }
                    minute?.let { set(Calendar.MINUTE, it) }
                }
                val endTime = cal.timeInMillis
                val event = viewModel.getScheduleEvent(startTime, endTime)
                // switch back to the main thread to update the UI
                withContext(Dispatchers.Main) {
                    event.observe(viewLifecycleOwner) { lesson ->
                        // This code will be executed when the LiveData object emits a new value
                        val mAdapter = LessonWeekAdapter(requireContext(), lesson)
                        item.adapter = mAdapter
                    }
                }
                i++
            }
            i = 1
            cal1.add(Calendar.DAY_OF_MONTH, 1)
            date = cal1.timeInMillis
            for (item in thursday) {
                var timePreference = sharedPrefs.getString("start_time_preference$i", "00:00")
                var timeParts = timePreference?.split(":")
                var hour = timeParts?.get(0)?.toInt()
                var minute = timeParts?.get(1)?.toInt()
                var cal = Calendar.getInstance().apply {
                    timeZone = TimeZone.getDefault() // встановлення локального часового поясу
                    timeInMillis = date
                    hour?.let { set(Calendar.HOUR_OF_DAY, it) }
                    minute?.let { set(Calendar.MINUTE, it) }
                }
                val startTime = cal.timeInMillis
                timePreference = sharedPrefs.getString("end_time_preference$i", "00:00")
                timeParts = timePreference?.split(":")
                hour = timeParts?.get(0)?.toInt()
                minute = timeParts?.get(1)?.toInt()
                cal = Calendar.getInstance().apply {
                    timeZone = TimeZone.getDefault() // встановлення локального часового поясу
                    timeInMillis = date
                    hour?.let { set(Calendar.HOUR_OF_DAY, it) }
                    minute?.let { set(Calendar.MINUTE, it) }
                }
                val endTime = cal.timeInMillis
                val event = viewModel.getScheduleEvent(startTime, endTime)
                // switch back to the main thread to update the UI
                withContext(Dispatchers.Main) {
                    event.observe(viewLifecycleOwner) { lesson ->
                        // This code will be executed when the LiveData object emits a new value
                        val mAdapter = LessonWeekAdapter(requireContext(), lesson)
                        item.adapter = mAdapter
                    }
                }
                i++
            }
            i = 1
            cal1.add(Calendar.DAY_OF_MONTH, 1)
            date = cal1.timeInMillis
            for (item in friday) {
                var timePreference = sharedPrefs.getString("start_time_preference$i", "00:00")
                var timeParts = timePreference?.split(":")
                var hour = timeParts?.get(0)?.toInt()
                var minute = timeParts?.get(1)?.toInt()
                var cal = Calendar.getInstance().apply {
                    timeZone = TimeZone.getDefault() // встановлення локального часового поясу
                    timeInMillis = date
                    hour?.let { set(Calendar.HOUR_OF_DAY, it) }
                    minute?.let { set(Calendar.MINUTE, it) }
                }
                val startTime = cal.timeInMillis
                timePreference = sharedPrefs.getString("end_time_preference$i", "00:00")
                timeParts = timePreference?.split(":")
                hour = timeParts?.get(0)?.toInt()
                minute = timeParts?.get(1)?.toInt()
                cal = Calendar.getInstance().apply {
                    timeZone = TimeZone.getDefault() // встановлення локального часового поясу
                    timeInMillis = date
                    hour?.let { set(Calendar.HOUR_OF_DAY, it) }
                    minute?.let { set(Calendar.MINUTE, it) }
                }
                val endTime = cal.timeInMillis
                val event = viewModel.getScheduleEvent(startTime, endTime)
                // switch back to the main thread to update the UI
                withContext(Dispatchers.Main) {
                    event.observe(viewLifecycleOwner) { lesson ->
                        // This code will be executed when the LiveData object emits a new value
                        val mAdapter = LessonWeekAdapter(requireContext(), lesson)
                        item.adapter = mAdapter
                    }
                }
                i++
            }
            i = 1
            cal1.add(Calendar.DAY_OF_MONTH, 1)
            date = cal1.timeInMillis
            for (item in saturday) {
                var timePreference = sharedPrefs.getString("start_time_preference$i", "00:00")
                var timeParts = timePreference?.split(":")
                var hour = timeParts?.get(0)?.toInt()
                var minute = timeParts?.get(1)?.toInt()
                var cal = Calendar.getInstance().apply {
                    timeZone = TimeZone.getDefault() // встановлення локального часового поясу
                    timeInMillis = date
                    hour?.let { set(Calendar.HOUR_OF_DAY, it) }
                    minute?.let { set(Calendar.MINUTE, it) }
                }
                val startTime = cal.timeInMillis
                timePreference = sharedPrefs.getString("end_time_preference$i", "00:00")
                timeParts = timePreference?.split(":")
                hour = timeParts?.get(0)?.toInt()
                minute = timeParts?.get(1)?.toInt()
                cal = Calendar.getInstance().apply {
                    timeZone = TimeZone.getDefault() // встановлення локального часового поясу
                    timeInMillis = date
                    hour?.let { set(Calendar.HOUR_OF_DAY, it) }
                    minute?.let { set(Calendar.MINUTE, it) }
                }
                val endTime = cal.timeInMillis
                // switch back to the main thread to update the UI
                withContext(Dispatchers.Main) {
                    val event = viewModel.getScheduleEvent(startTime, endTime)
                    event.observe(viewLifecycleOwner) { events ->
                        val eventList = events ?: emptyList() // handle null case
                        val mAdapter = LessonWeekAdapter(requireContext(), eventList)
                        item.adapter = mAdapter
                    }
                }

                i++
            }
        }
        val calen = Calendar.getInstance().apply {
            timeZone = TimeZone.getDefault() // встановлення локального часового поясу
            timeInMillis = selectedDay
        }

        binding.buttonNextWeek.setOnClickListener {
            calen.add(Calendar.DAY_OF_MONTH, 7)
            selectedDay = calen.timeInMillis
            selectedDate = selectedDay
            controller.navigateUp()
            controller.navigate(R.id.nav_schedule_week)
        }
        binding.buttonPrevWeek.setOnClickListener {
            calen.add(Calendar.DAY_OF_MONTH, -7)
            selectedDay = calen.timeInMillis
            selectedDate = selectedDay
            controller.navigateUp()
            controller.navigate(R.id.nav_schedule_week)
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