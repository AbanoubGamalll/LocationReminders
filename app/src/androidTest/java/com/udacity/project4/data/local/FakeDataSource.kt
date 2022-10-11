package com.udacity.project4.data.local

import com.udacity.project4.locationreminders.data.ReminderDataSource
import com.udacity.project4.locationreminders.data.dto.ReminderDTO
import com.udacity.project4.locationreminders.data.dto.Result

// Done
class FakeDataSource : ReminderDataSource {

    private var reminders = mutableListOf<ReminderDTO>()
    private var errorState = false

    override suspend fun getReminders(): Result<List<ReminderDTO>> =
        if (errorState) Result.Error("Error in get Reminders")
        else Result.Success(reminders)

    override suspend fun saveReminder(reminder: ReminderDTO) {
        reminders.add(reminder)
    }

    override suspend fun getReminder(id: String): Result<ReminderDTO> {
        if (errorState) return Result.Error("Error on Get Reminder")
        else {
            val reminder = reminders.find { it.id == id }
            reminder?.let { return Result.Success(reminder) }
        }
        return Result.Error("not found")
    }

    override suspend fun deleteAllReminders() {
        reminders.clear()
    }

    fun setError() {
        errorState = true
    }
}
