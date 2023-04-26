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
import androidx.preference.PreferenceManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.calendar.R
import com.example.calendar.database.EventAdapter
import com.example.calendar.database.EventViewModel
import com.example.calendar.databinding.FragmentWeekBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.*

class WeekFragment : Fragment() {

    private var _binding: FragmentWeekBinding? = null
    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentWeekBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        val sharedPrefs = PreferenceManager.getDefaultSharedPreferences(requireContext())
        val ownerName = sharedPrefs.getString("ownerName", "")

        binding.userName.text = ownerName



        // отримуємо передану дату
        val dateInMillis = selectedDate
        Log.d("DATE", "$dateInMillis")
        val calendar = Calendar.getInstance()
        calendar.timeInMillis = dateInMillis


        // отримуємо поточний день, день тижня і місяць
        val weekDays = resources.getStringArray(R.array.week_days)
        val dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK)
        val month = calendar.get(Calendar.MONTH)
        val months = resources.getStringArray(R.array.months)
        // встановлюємо назву місяця і рік в текстові поля
        binding.month2.text = months[month]
        binding.year2.text = calendar.get(Calendar.YEAR).toString()
        binding.textDay3.text = weekDays[dayOfWeek - 1]
        val week = resources.getStringArray(R.array.week)
        // встановлюємо назви днів тижня в текстові поля
        binding.dayOfWeek01.text = week[if (dayOfWeek < 4){dayOfWeek+7-4}else {dayOfWeek - 4}]
        binding.dayOfWeek02.text = week[if (dayOfWeek < 3){dayOfWeek+7-3}else {dayOfWeek - 3}]
        binding.dayOfWeek03.text = week[if (dayOfWeek < 2){dayOfWeek+7-2}else {dayOfWeek - 2}]
        binding.dayOfWeek04.text = week[dayOfWeek - 1]
        binding.dayOfWeek05.text = week[if (dayOfWeek > 6){dayOfWeek-7}else {dayOfWeek}]
        binding.dayOfWeek06.text = week[if (dayOfWeek > 5){dayOfWeek-7+1}else {dayOfWeek + 1}]
        binding.dayOfWeek07.text = week[if (dayOfWeek > 4){dayOfWeek-7+2}else {dayOfWeek + 2}]

        calendar.add(Calendar.DAY_OF_MONTH, -3)

        // Отримуємо дату на кожен день тижня та встановлюємо її в відповідне поле
        for (i in 1..7) {
            val nextDay = calendar.get(Calendar.DAY_OF_MONTH)
            val dateTextView = when (i) {
                1 -> binding.date01
                2 -> binding.date02
                3 -> binding.date03
                4 -> binding.date04
                5 -> binding.date05
                6 -> binding.date06
                7 -> binding.date07
                else -> null
            }
            dateTextView?.text = nextDay.toString()
            calendar.add(Calendar.DAY_OF_MONTH, 1)
        }
        calendar.add(Calendar.DAY_OF_MONTH, -4)
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
                    val mAdapter = EventAdapter(requireContext(), events)
                    eventView.adapter = mAdapter
                }
            }
        }
        val calen = Calendar.getInstance()
        calen.timeInMillis = selectedDate
        val controller = findNavController()
        binding.daybutton.setOnClickListener {
            controller.navigateUp()
            controller.navigate(R.id.nav_day)
        }
        binding.monthbutton.setOnClickListener {

            controller.navigate(R.id.nav_month)
        }
        binding.button.setOnClickListener {
            controller.navigate(R.id.nav_add_event)
        }
        binding.date01.setOnClickListener {
            calen.add(Calendar.DAY_OF_MONTH, -3)
            selectedDate = calen.timeInMillis
            controller.navigateUp()
            controller.navigate(R.id.nav_week)
        }
        binding.date02.setOnClickListener {
            calen.add(Calendar.DAY_OF_MONTH, -2)
            selectedDate = calen.timeInMillis
            controller.navigateUp()
            controller.navigate(R.id.nav_week)
        }
        binding.date03.setOnClickListener {
            calen.add(Calendar.DAY_OF_MONTH, -1)
            selectedDate = calen.timeInMillis
            controller.navigateUp()
            controller.navigate(R.id.nav_week)
        }
        binding.date05.setOnClickListener {
            calen.add(Calendar.DAY_OF_MONTH, 1)
            selectedDate = calen.timeInMillis
            controller.navigateUp()
            controller.navigate(R.id.nav_week)
        }
        binding.date06.setOnClickListener {
            calen.add(Calendar.DAY_OF_MONTH, 2)
            selectedDate = calen.timeInMillis
            controller.navigateUp()
            controller.navigate(R.id.nav_week)
        }
        binding.date07.setOnClickListener {
            calen.add(Calendar.DAY_OF_MONTH, 3)
            selectedDate = calen.timeInMillis
            controller.navigateUp()
            controller.navigate(R.id.nav_week)
        }
        binding.dayOfWeek01.setOnClickListener {
            calen.add(Calendar.DAY_OF_MONTH, -3)
            selectedDate = calen.timeInMillis
            controller.navigateUp()
            controller.navigate(R.id.nav_week)
        }
        binding.dayOfWeek02.setOnClickListener {
            calen.add(Calendar.DAY_OF_MONTH, -2)
            selectedDate = calen.timeInMillis
            controller.navigateUp()
            controller.navigate(R.id.nav_week)
        }
        binding.dayOfWeek03.setOnClickListener {
            calen.add(Calendar.DAY_OF_MONTH, -1)
            selectedDate = calen.timeInMillis
            controller.navigateUp()
            controller.navigate(R.id.nav_week)
        }
        binding.dayOfWeek05.setOnClickListener {
            calen.add(Calendar.DAY_OF_MONTH, 1)
            selectedDate = calen.timeInMillis
            controller.navigateUp()
            controller.navigate(R.id.nav_week)
        }
        binding.dayOfWeek06.setOnClickListener {
            calen.add(Calendar.DAY_OF_MONTH, 2)
            selectedDate = calen.timeInMillis
            controller.navigateUp()
            controller.navigate(R.id.nav_week)
        }
        binding.dayOfWeek07.setOnClickListener {
            calen.add(Calendar.DAY_OF_MONTH, 3)
            selectedDate = calen.timeInMillis
            controller.navigateUp()
            controller.navigate(R.id.nav_week)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}