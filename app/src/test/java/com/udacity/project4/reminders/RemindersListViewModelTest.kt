package com.udacity.project4.reminders

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.udacity.project4.coroutine.MainCoroutineRule
import org.junit.runner.RunWith
import com.udacity.project4.data.FakeDataSource
import com.udacity.project4.locationreminders.reminderslist.RemindersListViewModel
import org.junit.*
import org.koin.core.context.stopKoin
import com.udacity.project4.locationreminders.data.dto.ReminderDTO
import com.udacity.project4.util.getOrAwaitValue
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runBlockingTest
import org.junit.Assert.assertEquals

//Done
@ExperimentalCoroutinesApi
@RunWith(AndroidJUnit4::class)
class RemindersListViewModelTest {
    private lateinit var remindersRepository: FakeDataSource
    private lateinit var viewModel: RemindersListViewModel

    @get:Rule
    var mainCoroutineRule = MainCoroutineRule()

    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()

    @Before
    fun before() {
        remindersRepository = FakeDataSource()
        viewModel =
            RemindersListViewModel(
                app = ApplicationProvider.getApplicationContext(),
                dataSource = remindersRepository
            )
    }

    @After
    fun after() = stopKoin()

    @Test
    fun `Loading Reminders & Show Loading`() = mainCoroutineRule.runBlockingTest {
        mainCoroutineRule.pauseDispatcher()
        viewModel.loadReminders()

        assertEquals(viewModel.showLoading.getOrAwaitValue(), true)

        mainCoroutineRule.resumeDispatcher()

        assertEquals(viewModel.showLoading.getOrAwaitValue(), false)
    }

    @Test
    fun `Loading Reminders & Update SnackBar`() = mainCoroutineRule.runBlockingTest {

        remindersRepository.setError()
        viewModel.loadReminders()

        assertEquals(viewModel.showSnackBar.getOrAwaitValue(), "Error in get Reminders")
    }

    @Test
    fun `Loading Reminders & Reminders Not Empty`() = mainCoroutineRule.runBlockingTest {

        remindersRepository.saveReminder(
            ReminderDTO(
                "t",
                "d",
                "l",
                0.0,
                0.0
            )
        )
        viewModel.loadReminders()
        val size = viewModel.remindersList.getOrAwaitValue().size
        assertEquals(size, 1)
    }

    @Test
    fun `Loading Reminders & Reminders Empty`() = mainCoroutineRule.runBlockingTest {
        remindersRepository.deleteAllReminders()
        viewModel.loadReminders()
        val size = viewModel.remindersList.getOrAwaitValue().size
        assertEquals(size, 0)
    }
}