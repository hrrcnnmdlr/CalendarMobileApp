package com.example.calendar.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.preference.PreferenceManager
import com.example.calendar.databinding.ActivityFirstBinding

class FirstActivity : AppCompatActivity() {
    private lateinit var binding: ActivityFirstBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // ініціалізуємо байндинг
        binding = ActivityFirstBinding.inflate(layoutInflater)

        // отримуємо кореневий елемент інтерфейсу
        val view = binding.root

        // встановлюємо інтерфейс активності
        setContentView(view)

        // обробник натискання на кнопку
        binding.buttonSave.setOnClickListener {
            // отримуємо нове ім'я з поля введення
            val newName = binding.editTextUserName.text.toString()

            // зберігаємо нове ім'я в SharedPreferences
            val sharedPrefs = PreferenceManager.getDefaultSharedPreferences(this)
            val editor = sharedPrefs.edit()
            editor.putString("ownerName", newName)
            editor.apply()
            Log.d("TAG", "Нове ім'я: $newName")

            // закриваємо активність
            finish()
        }
    }
}