package com.example.calendar.fragments

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.calendar.R
import com.example.calendar.database.EventViewModel
import com.example.calendar.databinding.FragmentEventDetailsBinding
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

class EventDetailsFragment : Fragment() {

    private var _binding: FragmentEventDetailsBinding? = null
    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!
    private lateinit var eventViewModel: EventViewModel
    private var eventId: Int = -1

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentEventDetailsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Отримати ідентифікатор зустрічі, переданий з попередньої активності
        val args = arguments
        val eventId = args?.getInt("event_id")

        // Створити ViewModel для доступу до даних
        eventViewModel = ViewModelProvider(this)[EventViewModel::class.java]

        // Отримати дані про зустріч за заданим ідентифікатором
        lifecycleScope.launch {
            val event = eventId?.let { eventViewModel.getEventById(it) }
            // Відобразити дані про зустріч
            if (event != null) {
                binding.eventNameTextView.text = event.eventName
                binding.descriptionTextView.text = event.description
                binding.startDateTimeTextView.text =
                    SimpleDateFormat("dd.MM.yyyy HH:mm", Locale.getDefault())
                        .format(event.startDateTime)
                binding.endDateTimeTextView.text =
                    SimpleDateFormat("dd.MM.yyyy HH:mm", Locale.getDefault())
                        .format(event.endDateTime)
                binding.locationTextView.text = event.location
                val category = eventViewModel.getCategoryById(event.categoryId)
                if (category != null) {
                    binding.categoryTextView.text = category.name
                }
                binding.repeatTextView.text = event.repeat
                binding.reminder5Min3.isChecked = event.remind5MinutesBefore
                binding.reminder15Min3.isChecked = event.remind15MinutesBefore
                binding.reminder30Min3.isChecked = event.remind30MinutesBefore
                binding.reminder1Hour3.isChecked = event.remind1HourBefore
                binding.reminder1Day3.isChecked = event.remind1DayBefore

            }
            // Додати обробник для кнопки редагування
            binding.editEventButton.setOnClickListener {
                val bundle = Bundle()
                if (eventId != null) {
                    bundle.putInt("event_id", eventId)
                }
                findNavController().navigate(R.id.nav_edit_event, bundle)
            }
            // Додати обробник для кнопки видалення
            binding.deleteEventButton.setOnClickListener {
                if (event != null) {
                    eventViewModel.delete(event)
                }
                Toast.makeText(requireContext(), "Event deleted", Toast.LENGTH_SHORT).show()
                findNavController().navigateUp()
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}