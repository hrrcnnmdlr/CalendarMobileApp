package com.example.calendar.fragments

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.calendar.*
import com.example.calendar.database.EventAdapter
import com.example.calendar.database.MainDB
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

        // отримуємо передану дату
        val args = arguments
        val dateInMillis = args?.getLong("date")
        Log.d("DATE", "$dateInMillis")
        val calendar = Calendar.getInstance()
        if (dateInMillis != null) {
            calendar.timeInMillis = dateInMillis
        }

        // отримуємо поточний день, день тижня і місяць
        val dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK)
        val month = calendar.get(Calendar.MONTH)

        // встановлюємо назву місяця і рік в текстові поля
        binding.month2.text = months[month]
        binding.year2.text = calendar.get(Calendar.YEAR).toString()

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
            val dataBase = MainDB.getDatabase(requireContext())
            val events = dataBase.getDao().getEventsForDay(date)
            // switch back to the main thread to update the UI
            withContext(Dispatchers.Main) {
                val mAdapter = EventAdapter(requireContext(), events)
                eventView.adapter = mAdapter
            }
        }
        val bundle = Bundle()
        val controller = findNavController()
        binding.daybutton.setOnClickListener {
            if (dateInMillis != null) {
                bundle.putLong("date", dateInMillis)
            }
            controller.navigate(R.id.nav_day, bundle)
        }
        binding.monthbutton.setOnClickListener {
            if (dateInMillis != null) {
                bundle.putLong("date", dateInMillis)
            }
            controller.navigate(R.id.nav_month, bundle)
        }
        binding.button.setOnClickListener {
            if (dateInMillis != null) {
                bundle.putLong("date", dateInMillis)
            }
            controller.navigate(R.id.nav_add_event, bundle)
        }
    }
    /*
    // Методи, які змінюють відображуваний тиждень в залежності від дня тижня, на який користувач натиснув.
    fun day1() {
        changeWeek(-3)
    }

    fun day2() {
        changeWeek(-2)
    }

    fun day3() {
        changeWeek(-1)
    }

    fun day5() {
        changeWeek(1)
    }

    fun day6() {
        changeWeek(2)
    }

    fun day7() {
        changeWeek(3)
    }*/

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}