package com.udacity.project4.locationreminders

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.udacity.project4.base.NavigationCommand
import com.udacity.project4.coroutine.MainCoroutineRule
import com.udacity.project4.data.FakeDataSource
import com.udacity.project4.locationreminders.reminderslist.ReminderDataItem
import com.udacity.project4.locationreminders.savereminder.SaveReminderViewModel
import com.udacity.project4.util.getOrAwaitValue
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runBlockingTest
import org.junit.*
import org.junit.Assert.assertEquals
import org.junit.runner.RunWith
import org.koin.core.context.stopKoin
import com.udacity.project4.R

@ExperimentalCoroutinesApi
@RunWith(AndroidJUnit4::class)
class SaveReminderViewModelTest {
    private lateinit var remindersRepository: FakeDataSource
    private lateinit var viewModel: SaveReminderViewModel

    @get:Rule
    var mainCoroutineRule = MainCoroutineRule()

    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()

    @Before
    fun before() {
        remindersRepository = FakeDataSource()
        viewModel =
            SaveReminderViewModel(
                app = ApplicationProvider.getApplicationContext(),
                dataSource = remindersRepository
            )
    }

    @After
    fun after() = stopKoin()

    @Test
    fun `Save Reminders & Show Loading`() = mainCoroutineRule.runBlockingTest {
        mainCoroutineRule.pauseDispatcher()
        viewModel.saveReminder(
            ReminderDataItem(
                "t1",
                "d1.",
                "l1",
                0.0,
                0.0
            )
        )
        assertEquals(viewModel.showLoading.getOrAwaitValue(), true)
        mainCoroutineRule.resumeDispatcher()
        assertEquals(viewModel.showLoading.getOrAwaitValue(), false)

        assertEquals(
            viewModel.showToast.getOrAwaitValue(),
            viewModel.app.getString(R.string.reminder_saved)
        )
        assertEquals(viewModel.navigationCommand.getOrAwaitValue(), NavigationCommand.Back)

    }

    @Test
    fun `validateAndSaveReminder & Show SnackBar`() = mainCoroutineRule.runBlockingTest {
        // title null
        remindersRepository.setError()
        val reminder = ReminderDataItem(
            null,
            "d",
            "l",
            0.0,
            0.0
        )

        viewModel.validateAndSaveReminder(reminder)
        assertEquals(
            viewModel.showSnackBarInt.getOrAwaitValue(),
            R.string.err_enter_title
        )
        // location null
        reminder.location = null
        reminder.title = "t"

        viewModel.validateAndSaveReminder(reminder)
        assertEquals(viewModel.showSnackBarInt.getOrAwaitValue(), R.string.err_select_location)
    }

    @Test
    fun `saveReminder & ShowLoading`() = mainCoroutineRule.runBlockingTest {
        mainCoroutineRule.pauseDispatcher()
        viewModel.saveReminder(
            ReminderDataItem(
                "t",
                "d",
                "l",
                0.0,
                0.0
            )
        )

        assertEquals(viewModel.showLoading.getOrAwaitValue(), true)

        mainCoroutineRule.resumeDispatcher()

        assertEquals(viewModel.showLoading.getOrAwaitValue(), false)
    }

}