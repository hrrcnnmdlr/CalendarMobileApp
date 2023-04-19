package com.example.calendar

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class EventViewModel(application: Application) : AndroidViewModel(application) {
    private val repository: EventRepository
    private val categoryRepository: CategoryRepository
    private val allEvents: LiveData<List<Event>>

    init {
        val database = MainDB.getDatabase(application)
        val eventsDao = database.getDao()
        val categoryDao = database.categoryDao()
        repository = EventRepository(eventsDao)
        categoryRepository = CategoryRepository(categoryDao)
        allEvents = repository.allEvents
    }

    fun insert(event: Event) = viewModelScope.launch(Dispatchers.IO) {
        repository.insert(event)
    }

    fun update(event: Event) = viewModelScope.launch(Dispatchers.IO) {
        repository.update(event)
    }

    fun delete(event: Event) = viewModelScope.launch(Dispatchers.IO) {
        repository.delete(event)
    }

    suspend fun getEventById(id: Int): Event? {
        return repository.getEventById(id)
    }

    fun insertCategory(category: Category) = viewModelScope.launch(Dispatchers.IO) {
        categoryRepository.insertCategory(category)
    }

    fun updateCategory(category: Category) = viewModelScope.launch(Dispatchers.IO) {
        categoryRepository.updateCategory(category)
    }

    fun deleteCategory(category: Category) = viewModelScope.launch(Dispatchers.IO) {
        categoryRepository.deleteCategory(category)
    }

    suspend fun getCategoryById(id: Int): Category? {
        return categoryRepository.getCategoryById(id)
    }

}