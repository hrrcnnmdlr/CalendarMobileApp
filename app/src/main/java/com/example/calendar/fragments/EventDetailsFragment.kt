package com.example.calendar.fragments

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.calendar.R
import com.example.calendar.database.Event
import com.example.calendar.database.EventAdapter
import com.example.calendar.database.EventRepetition
import com.example.calendar.database.EventViewModel
import com.example.calendar.databinding.FragmentEventDetailsBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
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
        eventId = args?.getInt("event_id")!!

        // Створити ViewModel для доступу до даних
        eventViewModel = ViewModelProvider(this)[EventViewModel::class.java]
        // Отримати дані про зустріч за заданим ідентифікатором
        lifecycleScope.launch {
            val event: Event
            withContext(Dispatchers.IO) {
                event = eventId.let { eventViewModel.getEventById(it) }
            }
            // Відобразити дані про зустріч
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
            // Додати обробник для кнопки редагування
            binding.editEventButton.setOnClickListener {
                lifecycleScope.launch {
                    val bundle = Bundle()
                    bundle.putInt("event_id", eventId)
                    findNavController().navigate(R.id.nav_edit_event, bundle)
                }
            }
            // Додати обробник для кнопки видалення
            binding.deleteEventButton.setOnClickListener {
                if (event.repeat != EventRepetition.NONE.toString()) {
                    val alertDialogBuilder = AlertDialog.Builder(requireContext())
                    alertDialogBuilder.apply {
                        setTitle("Delete Event")
                        setMessage("Do you want to delete all recurring events or just this one?")
                        setPositiveButton("All Recurring Events") { _, _ ->
                            lifecycleScope.launch {
                                withContext(Dispatchers.IO) {
                                    eventViewModel.deleteAllRepeated(event)
                                }
                            }
                            Toast.makeText(
                                requireContext(),
                                "All recurring events deleted",
                                Toast.LENGTH_SHORT
                            ).show()
                            findNavController().navigateUp()
                        }
                        setNegativeButton("Just This One") { _, _ ->
                            lifecycleScope.launch {
                                withContext(Dispatchers.IO) {
                                    eventViewModel.delete(event)
                                }
                            }
                            Toast.makeText(
                                requireContext(),
                                "Event deleted",
                                Toast.LENGTH_SHORT
                            ).show()
                            findNavController().navigateUp()
                        }
                        setNeutralButton("This And All Next Events") { _, _ ->
                            lifecycleScope.launch {
                                withContext(Dispatchers.IO) {
                                    eventViewModel.deleteAllNext(event)
                                }
                            }
                            Toast.makeText(
                                requireContext(),
                                "This and next events deleted",
                                Toast.LENGTH_SHORT
                            ).show()
                            findNavController().navigateUp()
                        }
                    }
                    alertDialogBuilder.create().show()
                } else {
                    lifecycleScope.launch {
                        withContext(Dispatchers.IO) {
                            eventViewModel.delete(event)
                        }
                    }
                    Toast.makeText(
                        requireContext(),
                        "Event deleted",
                        Toast.LENGTH_SHORT
                    ).show()
                    findNavController().navigateUp()
                }
            }
            Log.d("EVENT", "$event")
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}