package com.udacity.project4.locationreminders.reminderlist

import android.app.Application
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.fragment.app.testing.launchFragmentInContainer
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.test.core.app.ApplicationProvider.getApplicationContext
import androidx.test.espresso.Espresso
import androidx.test.espresso.IdlingRegistry
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.MediumTest
import com.udacity.project4.locationreminders.reminderslist.ReminderListFragment
import org.junit.Test
import org.junit.runner.RunWith
import com.udacity.project4.R
import com.udacity.project4.data.local.FakeDataSource
import com.udacity.project4.locationreminders.data.ReminderDataSource
import com.udacity.project4.locationreminders.data.dto.ReminderDTO
import com.udacity.project4.locationreminders.reminderslist.ReminderListFragmentDirections
import com.udacity.project4.locationreminders.reminderslist.RemindersListViewModel
import com.udacity.project4.util.DataBindingIdlingResource
import com.udacity.project4.util.monitorFragment
import com.udacity.project4.utils.EspressoIdlingResource
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.runBlockingTest
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.core.context.startKoin
import org.koin.core.context.stopKoin
import org.koin.dsl.module
import org.mockito.Mockito.mock
import org.mockito.Mockito.verify
import java.lang.Thread.sleep

@ExperimentalCoroutinesApi
@RunWith(AndroidJUnit4::class)
@MediumTest
class ReminderListFragmentTest {

    private lateinit var db: FakeDataSource
    private val dataBindingIdlingResource = DataBindingIdlingResource()

    @get:Rule
    val instantExecutor = InstantTaskExecutorRule()

    @Before
    fun before() {
        stopKoin()
        db = FakeDataSource()
        val myModules = module {
            viewModel {
                RemindersListViewModel(
                    getApplicationContext(),
                    db as ReminderDataSource
                )
            }
        }
        startKoin {
            modules(listOf(myModules))
        }
        runBlocking {
            db.deleteAllReminders()
        }
    }

    @Before
    fun registerIdlingResource(): Unit = IdlingRegistry.getInstance().run {
        IdlingRegistry.getInstance().register(EspressoIdlingResource.countingIdlingResource)
        IdlingRegistry.getInstance().register(dataBindingIdlingResource)
    }

    @After
    fun unregisterIdlingResource(): Unit = IdlingRegistry.getInstance().run {
        IdlingRegistry.getInstance().unregister(EspressoIdlingResource.countingIdlingResource)
        IdlingRegistry.getInstance().unregister(dataBindingIdlingResource)
    }

    @Test
    fun activeReminderDetails_NoDataDisplay() = runBlockingTest {

        activeFragment()

        Espresso.onView(withId(R.id.noDataTextView))
            .check(matches(isDisplayed()))

        Espresso.onView(withText((getApplicationContext() as Application).getString(R.string.no_data)))
            .check(matches(isDisplayed()))
        sleep(2000)

    }

    @Test
    fun activeReminderDetails_1DataDisplay() = runBlockingTest {
        val reminder = ReminderDTO(
            "t1",
            "d1.",
            "l1",
            0.0,
            0.0
        )
        db.saveReminder(reminder)

        activeFragment()

        Espresso.onView(withText(reminder.title)).check(matches(isDisplayed()))
        Espresso.onView(withText(reminder.description)).check(matches(isDisplayed()))
        Espresso.onView(withText(reminder.location)).check(matches(isDisplayed()))

        sleep(2000)
    }

    @Test
    fun activeReminder_clickReminder_NavigateToDetail() {
        val navController = activeFragment()
        Espresso.onView(withId(R.id.addReminderFAB)).perform(click())
        verify(navController).navigate(ReminderListFragmentDirections.toSaveReminder())

        sleep(2000)
    }

    private fun activeFragment(): NavController {
        val scenario =
            launchFragmentInContainer<ReminderListFragment>(null, R.style.AppTheme)
        dataBindingIdlingResource.monitorFragment(scenario)
        val navController = mock(NavController::class.java)

        scenario.onFragment {
            Navigation.setViewNavController(it.view!!, navController)
        }
        return navController
    }
}
