package com.example.calendar.activity

import android.os.Bundle
import android.view.MenuItem
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.calendar.R
import com.example.calendar.database.EventAdapter
import com.example.calendar.database.MainDB
import com.example.calendar.databinding.ActivitySearchBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class SearchActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySearchBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySearchBinding.inflate(layoutInflater)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        setContentView(binding.root)
        var searchText: String
        val searchView = binding.recyclerViewSearch
        val linearLayoutManager = LinearLayoutManager(this)
        searchView.layoutManager = linearLayoutManager
        searchView.visibility = View.VISIBLE

        binding.editTextSearch.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus) {
                searchText = binding.editTextSearch.text.toString()
                lifecycleScope.launch(Dispatchers.IO) {
                    val dataBase = MainDB.getDatabase(this@SearchActivity)
                    val events = dataBase.getDao().getEventsForSearchRecycler(searchText)
                    // switch back to the main thread to update the UI
                    withContext(Dispatchers.Main) {
                        events.observe(this@SearchActivity) { events ->
                            // This code will be executed when the LiveData object emits a new value
                            val mAdapter = EventAdapter(this@SearchActivity, events)
                            searchView.adapter = mAdapter
                        }
                    }
                }
            }
        }
        binding.searchButton.setOnClickListener {
            searchText = binding.editTextSearch.text.toString()
            lifecycleScope.launch(Dispatchers.IO) {
                val dataBase = MainDB.getDatabase(this@SearchActivity)
                val events = dataBase.getDao().getEventsForSearchRecycler(searchText)
                // switch back to the main thread to update the UI
                withContext(Dispatchers.Main) {
                    events.observe(this@SearchActivity) { events ->
                        // This code will be executed when the LiveData object emits a new value
                        val mAdapter = EventAdapter(this@SearchActivity, events)
                        searchView.adapter = mAdapter
                    }
                }
            }
        }
    }
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                onBackPressed()
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }
}