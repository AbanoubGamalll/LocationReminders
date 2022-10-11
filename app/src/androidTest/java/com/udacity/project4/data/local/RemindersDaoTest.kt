package com.udacity.project4.data.local

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.SmallTest
import com.udacity.project4.locationreminders.data.dto.ReminderDTO
import com.udacity.project4.locationreminders.data.local.RemindersDatabase
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runBlockingTest
import org.hamcrest.core.Is.`is`
import org.hamcrest.core.IsNull
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertThat
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@SmallTest
@ExperimentalCoroutinesApi
@RunWith(AndroidJUnit4::class)
class RemindersDaoTest {

    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()

    private lateinit var db: RemindersDatabase

    @Before
    fun before() {
        db = Room.inMemoryDatabaseBuilder(
            ApplicationProvider.getApplicationContext(),
            RemindersDatabase::class.java
        ).build()
    }

    @After
    fun after() = db.close()

    @Test
    fun insertReminderById() = runBlockingTest {
        //Given insert reminder
        val reminder = ReminderDTO(
            "t1",
            "d1.",
            "l1",
            0.0,
            0.0
        )
        db.reminderDao().saveReminder(reminder)

        //When
        val getReminder = db.reminderDao().getReminderById(reminder.id)
        //Then
        assertThat<ReminderDTO>(getReminder as ReminderDTO, IsNull.notNullValue())
        assertThat(getReminder.toString(), `is`(reminder.toString()))

    }

    @Test
    fun RemoveAllReminders_GetEmpty() = runBlockingTest {
        //Given insert reminder
        db.reminderDao().deleteAllReminders()
        //When
        val getReminders = db.reminderDao().getReminders()
        //Then
        assertEquals(getReminders.size,0)
    }
}