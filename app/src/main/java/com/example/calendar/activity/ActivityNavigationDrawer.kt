package com.example.calendar.activity

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.drawerlayout.widget.DrawerLayout
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import androidx.preference.PreferenceManager
import com.example.calendar.R
import com.example.calendar.database.Category
import com.example.calendar.database.EventViewModel
import com.example.calendar.databinding.ActivityNavigationDrawerBinding
import com.google.android.material.navigation.NavigationView


class ActivityNavigationDrawer : AppCompatActivity() {

    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var binding: ActivityNavigationDrawerBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityNavigationDrawerBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Перевірка, чи програма запускається вперше
        // Перевірка, чи програма запускається вперше
        val prefs: SharedPreferences = PreferenceManager.getDefaultSharedPreferences(this)
        val isFirstLaunch = prefs.getBoolean("isFirstLaunch", true)
        if (isFirstLaunch) {
            // Запуск активності налаштувань
            val intent = Intent(this, FirstActivity::class.java)
            startActivity(intent)
            // Збереження статусу першого запуску
            val editor = prefs.edit()
            editor.putBoolean("isFirstLaunch", false)
            editor.apply()
            val eventViewModel = ViewModelProvider(this)[EventViewModel::class.java]
            eventViewModel.insertCategory(Category(0, "Business"))
            eventViewModel.insertCategory(Category(0, "Education"))
            val categories = listOf("Entertainment", "Food and Drink",
                "Health and Wellness", "Hobbies", "Music", "Networking", "Sports", "Technology", "Travel")
            for (category in categories) {
                eventViewModel.insertCategory(Category(0, category))
            }
        }

        setSupportActionBar(binding.appBarActivityNavigationDrawer.toolbar)

        val drawerLayout: DrawerLayout = binding.drawerLayout
        val navView: NavigationView = binding.navView
        val navController = findNavController(R.id.nav_host_fragment_content_activity_navigation_drawer)

        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.nav_month, R.id.nav_schedule_week
            ), drawerLayout
        )
        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_settings -> {
                val startSettingsActivity = Intent(this, SettingsActivity::class.java)
                startActivity(startSettingsActivity)
                return true
            }
            R.id.action_search -> {
                val startSearchActivity = Intent(this, SearchActivity::class.java)
                startActivity(startSearchActivity)
                return true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.activity_navigation_drawer, menu)
        return true
    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_host_fragment_content_activity_navigation_drawer)
        return navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
    }
}
