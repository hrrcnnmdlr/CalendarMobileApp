package com.example.calendar.activity

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.FragmentManager
import androidx.preference.*
import com.example.calendar.R
import java.text.SimpleDateFormat
import java.util.*


class SettingsActivity : AppCompatActivity() {
    private lateinit var fragmentManager: FragmentManager
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.settings_activity)
        if (savedInstanceState == null) {
            supportFragmentManager
                .beginTransaction()
                .replace(R.id.settings, SettingsFragment())
                .commit()
        }
        fragmentManager = supportFragmentManager
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }

    class SettingsFragment : PreferenceFragmentCompat(),
        Preference.OnPreferenceChangeListener,
        Preference.OnPreferenceClickListener {

        override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
            setPreferencesFromResource(R.xml.activity_settings, rootKey)

        }

        override fun onPreferenceTreeClick(preference: Preference): Boolean {
            val key = preference.key
            // Якщо натиснуто загальні налаштування
            if (key == getString(R.string.general_settings_key)) {
                // Запускаємо загальні налаштування
                val intent = Intent(requireActivity(), SettingsActivityGeneral::class.java)
                startActivity(intent)
            } else if (key == getString(R.string.schedule_settings_key)) {
                // Запускаємо налаштування розкладу
                val intent = Intent(requireActivity(), SettingsActivitySchedule::class.java)
                startActivity(intent)
            }
            return super.onPreferenceTreeClick(preference)
        }

        override fun onPreferenceChange(preference: Preference, newValue: Any?): Boolean {
            return true
        }

        override fun onPreferenceClick(preference: Preference): Boolean {
            // обробка кліків на Preference елементи, що не відносяться до зміни значення
            return true
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val id: Int = item.itemId

        // Обробляємо натискання кнопки "Назад" в меню
        if (id == android.R.id.home) {
            finish()
            return true
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }
}

class SettingsActivityGeneral : AppCompatActivity() {
    private lateinit var fragmentManager: FragmentManager
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.settings_activity)
        if (savedInstanceState == null) {
            supportFragmentManager
                .beginTransaction()
                .replace(R.id.settings, SettingsFragment())
                .commit()
        }
        fragmentManager = supportFragmentManager
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }

    class SettingsFragment : PreferenceFragmentCompat(),
        Preference.OnPreferenceChangeListener,
        Preference.OnPreferenceClickListener {

        override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
            setPreferencesFromResource(R.xml.root_preferences, rootKey)

            val namePreference: EditTextPreference? = findPreference("name_preference")
            namePreference?.summaryProvider = EditTextPreference.SimpleSummaryProvider.getInstance()
            namePreference?.onPreferenceChangeListener = this

            val timeFormatPreference: ListPreference? = findPreference("time_format_preference")
            timeFormatPreference?.summaryProvider =
                ListPreference.SimpleSummaryProvider.getInstance()
            timeFormatPreference?.onPreferenceChangeListener = this

            val dateFormatPreference: ListPreference? = findPreference("date_format_preference")
            dateFormatPreference?.summaryProvider =
                ListPreference.SimpleSummaryProvider.getInstance()
            dateFormatPreference?.onPreferenceChangeListener = this

            val languagePreference: ListPreference? = findPreference("language_preference")
            languagePreference?.summaryProvider = ListPreference.SimpleSummaryProvider.getInstance()
            languagePreference?.onPreferenceChangeListener = this
        }

        override fun onPreferenceTreeClick(preference: Preference): Boolean {
            if (preference.key == "choose_photo") {
                // логіка відкриття вікна вибору фото
                return true
            }
            return super.onPreferenceTreeClick(preference)
        }

        override fun onPreferenceChange(preference: Preference, newValue: Any?): Boolean {
            // обробка зміни значення в Preference елементах
            when (preference.key) {
                "name_preference" -> {
                    val newName = newValue as String
                    val sharedPrefs = PreferenceManager.getDefaultSharedPreferences(requireContext())
                    val editor = sharedPrefs.edit()
                    editor.putString("ownerName", newName)
                    editor.apply()
                    Log.d("TAG", "Нове ім'я: $newName")
                }
                "time_format_preference" -> {
                    val newTimeFormat = newValue as String
                    val preferences =
                        PreferenceManager.getDefaultSharedPreferences(requireContext())
                    preferences.edit().putString("time_format_preference", newTimeFormat).apply()
                    Log.d("TAG", "Новий формат часу: $newTimeFormat")
                    // Форматування поточного часу за новим форматом
                    val currentTime = Calendar.getInstance().time
                    val formattedTime =
                        SimpleDateFormat(newTimeFormat, Locale.getDefault()).format(currentTime)
                    Log.d("TAG", "Поточний час: $formattedTime")
                }
                "date_format_preference" -> {
                    val newDateFormat = newValue as String
                    val preferences =
                        PreferenceManager.getDefaultSharedPreferences(requireContext())
                    preferences.edit().putString("date_format_preference", newDateFormat).apply()
                    Log.d("TAG", "Новий формат дати: $newDateFormat")
                    // Форматування поточної дати за новим форматом
                    val currentDate = Calendar.getInstance().time
                    val formattedDate =
                        SimpleDateFormat(newDateFormat, Locale.getDefault()).format(currentDate)
                    Log.d("TAG", "Поточна дата: $formattedDate")
                }
                "language_preference" -> {
                    val newLanguage = newValue as String
                    val preferences = PreferenceManager.getDefaultSharedPreferences(requireContext())
                    preferences.edit().putString("language_preference", newLanguage).apply()

                    // Оновлення всіх текстових ресурсів відповідно до нової мови
                    val resources = resources
                    val configuration = resources.configuration
                    configuration.setLocale(Locale(newLanguage))
                    resources.updateConfiguration(configuration, resources.displayMetrics)

                    // Перезапуск активності для оновлення всіх текстових ресурсів
                    requireActivity().recreate()
                    val intent = requireActivity().packageManager.getLaunchIntentForPackage(requireActivity().packageName)
                    intent?.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                    requireActivity().finish()
                    if (intent != null) {
                        startActivity(intent)
                    }
                    Log.d("TAG", "Нова мова: $newLanguage")
                }
            }

            return true
        }

        override fun onPreferenceClick(preference: Preference): Boolean {
            // обробка кліків на Preference елементи, що не відносяться до зміни значення
            return true
        }
    }
}

class SettingsActivitySchedule : AppCompatActivity() {
    private lateinit var fragmentManager: FragmentManager
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.settings_activity)
        if (savedInstanceState == null) {
            supportFragmentManager
                .beginTransaction()
                .replace(R.id.settings, SettingsFragment())
                .commit()
        }
        fragmentManager = supportFragmentManager
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }

    class SettingsFragment : PreferenceFragmentCompat(),
        Preference.OnPreferenceChangeListener,
        Preference.OnPreferenceClickListener {

        override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
            setPreferencesFromResource(R.xml.root_preferance2, rootKey)
            val startDatePreference: EditTextPreference? = findPreference("start_date_preference")
            startDatePreference?.summaryProvider = EditTextPreference.SimpleSummaryProvider.getInstance()
            startDatePreference?.onPreferenceChangeListener = this

            val endDatePreference: EditTextPreference? = findPreference("end_date_preference")
            endDatePreference?.summaryProvider = EditTextPreference.SimpleSummaryProvider.getInstance()
            endDatePreference?.onPreferenceChangeListener = this

            val startTimePreference1: EditTextPreference? = findPreference("start_time_preference1")
            startTimePreference1?.summaryProvider = EditTextPreference.SimpleSummaryProvider.getInstance()
            startTimePreference1?.onPreferenceChangeListener = this

            val endTimePreference1: EditTextPreference? = findPreference("end_time_preference1")
            endTimePreference1?.summaryProvider = EditTextPreference.SimpleSummaryProvider.getInstance()
            endTimePreference1?.onPreferenceChangeListener = this

            val startTimePreference2: EditTextPreference? = findPreference("start_time_preference2")
            startTimePreference2?.summaryProvider = EditTextPreference.SimpleSummaryProvider.getInstance()
            startTimePreference2?.onPreferenceChangeListener = this

            val endTimePreference2: EditTextPreference? = findPreference("end_time_preference2")
            endTimePreference2?.summaryProvider = EditTextPreference.SimpleSummaryProvider.getInstance()
            endTimePreference2?.onPreferenceChangeListener = this

            val startTimePreference3: EditTextPreference? = findPreference("start_time_preference3")
            startTimePreference3?.summaryProvider = EditTextPreference.SimpleSummaryProvider.getInstance()
            startTimePreference3?.onPreferenceChangeListener = this

            val endTimePreference3: EditTextPreference? = findPreference("end_time_preference3")
            endTimePreference3?.summaryProvider = EditTextPreference.SimpleSummaryProvider.getInstance()
            endTimePreference3?.onPreferenceChangeListener = this

            val startTimePreference4: EditTextPreference? = findPreference("start_time_preference4")
            startTimePreference4?.summaryProvider = EditTextPreference.SimpleSummaryProvider.getInstance()
            startTimePreference4?.onPreferenceChangeListener = this

            val endTimePreference4: EditTextPreference? = findPreference("end_time_preference4")
            endTimePreference4?.summaryProvider = EditTextPreference.SimpleSummaryProvider.getInstance()
            endTimePreference4?.onPreferenceChangeListener = this

            val startTimePreference5: EditTextPreference? = findPreference("start_time_preference5")
            startTimePreference5?.summaryProvider = EditTextPreference.SimpleSummaryProvider.getInstance()
            startTimePreference5?.onPreferenceChangeListener = this

            val endTimePreference5: EditTextPreference? = findPreference("end_time_preference5")
            endTimePreference5?.summaryProvider = EditTextPreference.SimpleSummaryProvider.getInstance()
            endTimePreference5?.onPreferenceChangeListener = this

            val startTimePreference6: EditTextPreference? = findPreference("start_time_preference6")
            startTimePreference6?.summaryProvider = EditTextPreference.SimpleSummaryProvider.getInstance()
            startTimePreference6?.onPreferenceChangeListener = this

            val endTimePreference6: EditTextPreference? = findPreference("end_time_preference6")
            endTimePreference6?.summaryProvider = EditTextPreference.SimpleSummaryProvider.getInstance()
            endTimePreference6?.onPreferenceChangeListener = this

            val weeksPreference: ListPreference? = findPreference("weeks_preference")
            weeksPreference?.summaryProvider = ListPreference.SimpleSummaryProvider.getInstance()
            weeksPreference?.onPreferenceChangeListener = this
        }


        override fun onPreferenceChange(preference: Preference, newValue: Any?): Boolean {
            when (preference.key) {
                "start_date_preference" -> {
                    val newStartDate = newValue as String
                    val sharedPrefs = PreferenceManager.getDefaultSharedPreferences(requireContext())
                    val editor = sharedPrefs.edit()
                    editor.putString("start_date_preference", newStartDate)
                    editor.apply()
                    Log.d("TAG", "Нова дата початку: $newStartDate")
                }
                "end_date_preference" -> {
                    val newEndDate = newValue as String
                    val sharedPrefs = PreferenceManager.getDefaultSharedPreferences(requireContext())
                    val editor = sharedPrefs.edit()
                    editor.putString("end_date_preference", newEndDate)
                    editor.apply()
                    Log.d("TAG", "Нова дата закінчення: $newEndDate")
                }
                "start_time_preference1" -> {
                    val newStartTime1 = newValue as String
                    val sharedPrefs = PreferenceManager.getDefaultSharedPreferences(requireContext())
                    val editor = sharedPrefs.edit()
                    editor.putString("start_time_preference1", newStartTime1)
                    editor.apply()
                    Log.d("TAG", "Новий час початку першої пари: $newStartTime1")
                }
                "end_time_preference1" -> {
                    val newEndTime1 = newValue as String
                    val sharedPrefs = PreferenceManager.getDefaultSharedPreferences(requireContext())
                    val editor = sharedPrefs.edit()
                    editor.putString("end_time_preference1", newEndTime1)
                    editor.apply()
                    Log.d("TAG", "Новий час закінчення першої пари: $newEndTime1")
                }
                "start_time_preference2" -> {
                    val newStartTime2 = newValue as String
                    val sharedPrefs = PreferenceManager.getDefaultSharedPreferences(requireContext())
                    val editor = sharedPrefs.edit()
                    editor.putString("start_time_preference2", newStartTime2)
                    editor.apply()
                    Log.d("TAG", "Новий час початку другої пари: $newStartTime2")
                }
                "end_time_preference2" -> {
                    val newEndTime2 = newValue as String
                    val sharedPrefs = PreferenceManager.getDefaultSharedPreferences(requireContext())
                    val editor = sharedPrefs.edit()
                    editor.putString("end_time_preference2", newEndTime2)
                    editor.apply()
                    Log.d("TAG", "Новий час закінчення другої пари: $newEndTime2")
                }
                "start_time_preference3" -> {
                    val newStartTime3 = newValue as String
                    val sharedPrefs = PreferenceManager.getDefaultSharedPreferences(requireContext())
                    val editor = sharedPrefs.edit()
                    editor.putString("start_time_preference3", newStartTime3)
                    editor.apply()
                    Log.d("TAG", "Новий час початку третьої пари: $newStartTime3")
                }
                "end_time_preference3" -> {
                    val newEndTime3 = newValue as String
                    val sharedPrefs = PreferenceManager.getDefaultSharedPreferences(requireContext())
                    val editor = sharedPrefs.edit()
                    editor.putString("end_time_preference3", newEndTime3)
                    editor.apply()
                    Log.d("TAG", "Новий час закінчення третьої пари: $newEndTime3")
                }
                "start_time_preference4" -> {
                    val newStartTime4 = newValue as String
                    val sharedPrefs = PreferenceManager.getDefaultSharedPreferences(requireContext())
                    val editor = sharedPrefs.edit()
                    editor.putString("start_time_preference4", newStartTime4)
                    editor.apply()
                    Log.d("TAG", "Новий час початку четвертої пари: $newStartTime4")
                }
                "end_time_preference4" -> {
                    val newEndTime4 = newValue as String
                    val sharedPrefs = PreferenceManager.getDefaultSharedPreferences(requireContext())
                    val editor = sharedPrefs.edit()
                    editor.putString("end_time_preference4", newEndTime4)
                    editor.apply()
                    Log.d("TAG", "Новий час закінчення четвертої пари: $newEndTime4")
                }
                "start_time_preference5" -> {
                    val newStartTime5 = newValue as String
                    val sharedPrefs = PreferenceManager.getDefaultSharedPreferences(requireContext())
                    val editor = sharedPrefs.edit()
                    editor.putString("start_time_preference5", newStartTime5)
                    editor.apply()
                    Log.d("TAG", "Новий час початку п'ятої пари: $newStartTime5")
                }
                "end_time_preference5" -> {
                    val newEndTime5 = newValue as String
                    val sharedPrefs = PreferenceManager.getDefaultSharedPreferences(requireContext())
                    val editor = sharedPrefs.edit()
                    editor.putString("end_time_preference5", newEndTime5)
                    editor.apply()
                    Log.d("TAG", "Новий час закінчення п'ятої пари: $newEndTime5")
                }
                "start_time_preference6" -> {
                    val newStartTime6 = newValue as String
                    val sharedPrefs = PreferenceManager.getDefaultSharedPreferences(requireContext())
                    val editor = sharedPrefs.edit()
                    editor.putString("start_time_preference6", newStartTime6)
                    editor.apply()
                    Log.d("TAG", "Новий час початку шостої пари: $newStartTime6")
                }
                "end_time_preference6" -> {
                    val newEndTime6 = newValue as String
                    val sharedPrefs = PreferenceManager.getDefaultSharedPreferences(requireContext())
                    val editor = sharedPrefs.edit()
                    editor.putString("end_time_preference6", newEndTime6)
                    editor.apply()
                    Log.d("TAG", "Новий час закінчення шостої пари: $newEndTime6")
                }
                "weeks_preference" -> {
                    val newWeeks = newValue as String
                    val sharedPrefs = PreferenceManager.getDefaultSharedPreferences(requireContext())
                    val editor = sharedPrefs.edit()
                    editor.putString("weeks_preference", newWeeks)
                    editor.apply()
                    Log.d("TAG", "Нова кількість тижнів навчання: $newWeeks")
                }
            }
            return true
        }

        override fun onPreferenceClick(preference: Preference): Boolean {
            // обробка кліків на Preference елементи, що не відносяться до зміни значення
            return true
        }
    }
}