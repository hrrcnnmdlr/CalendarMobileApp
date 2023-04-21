package com.example.calendar.fragments

import android.os.Bundle
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
import com.example.calendar.databinding.FragmentDayBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
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

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Отримання дати з args
        val args = arguments
        val dateInMillis = args?.getLong("date")
        val calendar = Calendar.getInstance()
        if (dateInMillis != null) {
            calendar.timeInMillis = dateInMillis
        }

        // Отримання дня та дня тижня
        val day = calendar.get(Calendar.DAY_OF_MONTH)
        val dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK)

        // Встановлення тексту для TextView
        binding.month2.text = months[calendar.get(Calendar.MONTH)]
        binding.year2.text = calendar.get(Calendar.YEAR).toString()
        binding.textView17.text = week[dayOfWeek - 1]
        binding.textView18.text = day.toString()

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
        binding.weekbutton.setOnClickListener {
            if (dateInMillis != null) {
                bundle.putLong("date", dateInMillis)
            }
            controller.navigateUp()
            controller.navigate(R.id.nav_week, bundle)
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

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
