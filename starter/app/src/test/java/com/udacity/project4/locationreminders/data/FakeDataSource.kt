package com.udacity.project4.locationreminders.data

import com.udacity.project4.locationreminders.data.dto.ReminderDTO
import com.udacity.project4.locationreminders.data.dto.Result

class FakeDataSource : ReminderDataSource {

    private var shouldReturnError = false
    private var reminders = mutableListOf<ReminderDTO>()

    fun setReturnError(value: Boolean) {
        shouldReturnError = value
    }

    override suspend fun getReminders(): Result<List<ReminderDTO>> {
        if (shouldReturnError) {
            return Result.Error("Exception getReminder")
        }
        return Result.Success(reminders)
    }

    override suspend fun saveReminder(reminder: ReminderDTO) {
        reminders.add(reminder)
    }

    override suspend fun getReminder(id: String): Result<ReminderDTO> {
        if (shouldReturnError) {
            return Result.Error("Exception getReminder")
        }
        val found = reminders.find { it.id == id }
        return if (found != null) {
            Result.Success(found)
        } else {
            Result.Error("Reminder Id $id not found!")
        }
    }

    override suspend fun deleteAllReminders() {
        reminders.clear()
    }

}