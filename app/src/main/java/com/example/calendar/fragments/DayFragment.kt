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
import androidx.recyclerview.widget.RecyclerView
import com.example.calendar.R
import com.example.calendar.database.EventAdapter
import com.example.calendar.database.EventViewModel
import com.example.calendar.databinding.FragmentDayBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.time.LocalTime
import java.util.*


class DayFragment : Fragment() {

    private var _binding: FragmentDayBinding? = null
    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentDayBinding.inflate(inflater, container, false)
        return binding.root
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val textView = binding.goodMorning
        val currentTime = LocalTime.now()
        val greeting = when {
            currentTime < LocalTime.of(5, 0) -> getString(R.string.greeting_night)
            currentTime < LocalTime.NOON -> getString(R.string.greeting_morning)
            currentTime < LocalTime.of(18, 0) -> getString(R.string.greeting_afternoon)
            else -> getString(R.string.greeting_evening)
        }
        textView.text = greeting

        val sharedPrefs = PreferenceManager.getDefaultSharedPreferences(requireContext())
        val ownerName = sharedPrefs.getString("ownerName", "")

        binding.userName.text = ownerName

        // Отримання дати з args
        val dateInMillis = selectedDate
        val calendar = Calendar.getInstance()
        calendar.timeInMillis = dateInMillis

        // Отримання дня та дня тижня
        val day = calendar.get(Calendar.DAY_OF_MONTH)
        val dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK)

        val months = resources.getStringArray(R.array.months)
        val weekDays = resources.getStringArray(R.array.week_days)
        // Встановлення тексту для TextView
        binding.month2.text = months[calendar.get(Calendar.MONTH)]
        binding.year2.text = calendar.get(Calendar.YEAR).toString()
        val week = resources.getStringArray(R.array.week)
        binding.textView17.text = week[dayOfWeek - 1]
        binding.textView18.text = day.toString()
        binding.textDay.text = weekDays[dayOfWeek - 1]

        val eventView: RecyclerView = binding.recyclerEventView
        val linearLayoutManager = LinearLayoutManager(requireContext())
        eventView.layoutManager = linearLayoutManager
        eventView.visibility = View.VISIBLE
        val cal = Calendar.getInstance()
        cal.set(Calendar.YEAR, calendar.get(Calendar.YEAR))
        cal.set(Calendar.MONTH, calendar.get(Calendar.MONTH))
        cal.set(Calendar.DAY_OF_MONTH, calendar.get(Calendar.DAY_OF_MONTH))
        cal.set(Calendar.HOUR_OF_DAY, 0)
        cal.set(Calendar.MINUTE, 0)
        cal.set(Calendar.SECOND, 0)
        cal.set(Calendar.MILLISECOND, 0)
        val date = cal.timeInMillis
        // launch a coroutine on the IO dispatcher to get the events from the database
        lifecycleScope.launch(Dispatchers.IO) {
            val viewModel: EventViewModel by viewModels()
            val events = viewModel.getEventsForDay(date)
            // switch back to the main thread to update the UI
            withContext(Dispatchers.Main) {
                events.observe(viewLifecycleOwner) { events ->
                    // This code will be executed when the LiveData object emits a new value
                    Log.d("DayFragment", "Events: $events")
                    val mAdapter = EventAdapter(requireContext(), events)
                    eventView.adapter = mAdapter
                }
            }
        }
        val controller = findNavController()
        binding.weekbutton.setOnClickListener {
            controller.navigateUp()
            controller.navigate(R.id.nav_week)
        }
        binding.monthbutton.setOnClickListener {
            controller.navigate(R.id.nav_month)
        }
        binding.button.setOnClickListener {
            controller.navigate(R.id.nav_add_event)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
