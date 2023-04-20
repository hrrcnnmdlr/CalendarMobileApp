package com.example.calendar.database

import androidx.lifecycle.LiveData
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class CategoryRepository(private val categoryDao: CategoryDao) {
    val allCategories: LiveData<List<Category>> = categoryDao.getAllCategories()

    suspend fun insertCategory(category: Category) {
        categoryDao.insertCategory(category)
    }

    suspend fun updateCategory(category: Category) {
        categoryDao.updateCategory(category)
    }

    suspend fun deleteCategory(category: Category) {
        categoryDao.deleteCategory(category)
    }

    suspend fun deleteAllCategories() {
        categoryDao.deleteAllCategories()
    }

    suspend fun getCategoryById(id: Int): Category? {
        return withContext(Dispatchers.IO) {
            categoryDao.getCategoryById(id)
        }
    }
}