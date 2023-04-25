package com.example.calendar.activity

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.preference.*
import com.example.calendar.R
import java.text.SimpleDateFormat
import androidx.fragment.app.FragmentManager
import java.util.*

class SettingsActivity : AppCompatActivity() {
    lateinit var fragmentManager: FragmentManager
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