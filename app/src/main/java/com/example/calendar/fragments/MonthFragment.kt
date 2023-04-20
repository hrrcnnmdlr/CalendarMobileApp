package com.example.calendar.fragments

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.calendar.database.EventAdapter
import com.example.calendar.reminder.EventService
import com.example.calendar.database.MainDB
import com.example.calendar.R
import com.example.calendar.databinding.FragmentMonthBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.*


class MonthFragment : Fragment() {

    private var _binding: FragmentMonthBinding? = null
    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!
    // Початкова дата - 0, щоб при запуску вибрати поточну дату
    var selectedDate: Long = 0L

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // val monthViewModel = ViewModelProvider(this).get(MonthViewModel::class.java)

        _binding = FragmentMonthBinding.inflate(inflater, container, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val eventView: RecyclerView = binding.recyclerEventView
        // Поле зі зв'язкою до елементів UI через ViewBinding

        // Початкова дата - 0, щоб при запуску вибрати поточну дату
        // Створення сповіщення
        val notification = NotificationCompat.Builder(requireContext(), "CHANNEL_ID")
            .setContentTitle("Запущено службу нагадувань")
            .setContentText("Ваші нагадування запущено у фоновому режимі")
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setSmallIcon(R.drawable.ic_notification)
            .build()

        // Запуск служби
        val serviceIntent = Intent(requireContext(), EventService::class.java)
        ContextCompat.startForegroundService(requireContext(), serviceIntent)

        // Показ сповіщення
        val notificationManager = NotificationManagerCompat.from(requireContext())
        if (ActivityCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.POST_NOTIFICATIONS
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            return
        }
        notificationManager.notify(1, notification)

        val calendarView = binding.calendarView
        // Встановлення вибраної дати, якщо вона передана із попередньої активності
        selectedDate = calendarView.date

        calendarView.date = selectedDate
        calendarView.setOnDateChangeListener { _ , year, month, dayOfMonth ->
            // Виконати дії, коли користувач вибирає дату
            // Наприклад, оновити список подій для вибраної дати
            val selectedDateInMillis = Calendar.getInstance().apply {
                set(year, month, dayOfMonth, 0, 0, 0)
            }.timeInMillis

            lifecycleScope.launch(Dispatchers.IO) {
                val dataBase = MainDB.getDatabase(requireContext())
                val events = dataBase.getDao().getEventsForDay(selectedDateInMillis)
                // switch back to the main thread to update the UI
                withContext(Dispatchers.Main) {
                    val mAdapter = EventAdapter(requireContext(), events)
                    eventView.adapter = mAdapter
                }
            }
            val calendar = Calendar.getInstance()
            calendar.set(year, month, dayOfMonth)
            // Збереження вибраної дати в поле класу
            selectedDate = calendar.timeInMillis
        }

        val linearLayoutManager = LinearLayoutManager(requireContext())
        eventView.layoutManager = linearLayoutManager
        eventView.visibility = View.VISIBLE

        var dateInMillis = selectedDate
        if (selectedDate == 0L) {
            dateInMillis = binding.calendarView.date
        }
        val calendar = Calendar.getInstance()
        calendar.timeInMillis = dateInMillis

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
            bundle.putLong("date", selectedDate)
            controller.navigate(R.id.nav_week, bundle)
        }
        binding.daybutton.setOnClickListener {
            bundle.putLong("date", selectedDate)
            controller.navigate(R.id.nav_day, bundle)
        }
        binding.button.setOnClickListener {
            bundle.putLong("date", selectedDate)
            controller.navigate(R.id.nav_add_event, bundle)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
