package com.udacity.project4.data.local

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.MediumTest
import com.udacity.project4.locationreminders.data.dto.ReminderDTO
import com.udacity.project4.locationreminders.data.local.RemindersDatabase
import com.udacity.project4.locationreminders.data.local.RemindersLocalRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.runBlocking
import org.junit.*
import org.junit.Assert.assertEquals
import org.junit.runner.RunWith
import com.udacity.project4.locationreminders.data.dto.Result
import kotlinx.coroutines.test.runBlockingTest

@MediumTest
@ExperimentalCoroutinesApi
@RunWith(AndroidJUnit4::class)
class RemindersLocalRepositoryTest {

    private lateinit var remindersLocalRepository: RemindersLocalRepository
    private lateinit var db: RemindersDatabase

    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()

    @Before
    fun before() {
        db = Room.inMemoryDatabaseBuilder(
            ApplicationProvider.getApplicationContext(),
            RemindersDatabase::class.java
        ).allowMainThreadQueries().build()

        remindersLocalRepository = RemindersLocalRepository(
            db.reminderDao(),
            Dispatchers.Main
        )
    }

    @After
    fun after() = db.close()


    @Test
    fun insertReminderById() = runBlocking {
        //Given
        val reminder = ReminderDTO(
            "t1",
            "d1.",
            "l1",
            0.0,
            0.0
        )
        remindersLocalRepository.saveReminder(reminder)
        // When
        val getReminder = remindersLocalRepository.getReminder(reminder.id)
        //Then
        assertEquals(getReminder is Result.Success, true)
        getReminder as Result.Success
        assertEquals(getReminder.data, reminder)
    }

    @Test
    fun RemoveAllReminders_GetEmpty() = runBlocking {
        //Given insert reminder
        remindersLocalRepository.deleteAllReminders()
        //When
        val getReminders = remindersLocalRepository.getReminders()
        //Then
        assertEquals(getReminders is Result.Success, true)
        getReminders as Result.Success
        assertEquals(getReminders.data.size,0)
    }

    @Test
    fun insertReminderById_GetError() = runBlocking {

        remindersLocalRepository.deleteAllReminders()

        val result = remindersLocalRepository.getReminder("id")

        assertEquals(result is Result.Error, true)
        result as Result.Error
        assertEquals(result.message, "Reminder not found!")
    }
}