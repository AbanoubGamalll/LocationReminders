package com.udacity.project4.locationreminders.savereminder

import androidx.fragment.app.testing.launchFragmentInContainer
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.test.espresso.Espresso
import androidx.test.espresso.Espresso.closeSoftKeyboard
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.IdlingRegistry
import androidx.test.espresso.action.ViewActions
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.action.ViewActions.typeText
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.MediumTest
import com.udacity.project4.R
import com.udacity.project4.locationreminders.reminderslist.ReminderListFragment
import com.udacity.project4.locationreminders.reminderslist.ReminderListFragmentDirections
import com.udacity.project4.util.DataBindingIdlingResource
import com.udacity.project4.util.monitorFragment
import com.udacity.project4.utils.EspressoIdlingResource
import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mockito
import org.mockito.Mockito.verify


@ExperimentalCoroutinesApi
@RunWith(AndroidJUnit4::class)
@MediumTest
class SaveReminderFragmentTest {
    private val dataBindingIdlingResource = DataBindingIdlingResource()

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
    fun activeReminder_clickselectLocation_NavigateToLocation() {
        val navController = activeFragment()

        onView(withId(R.id.selectLocation)).perform(click())

        verify(navController).navigate(SaveReminderFragmentDirections.actionSaveReminderFragmentToSelectLocationFragment())
        Thread.sleep(2000)
    }

    @Test
    fun activeReminder_clickSaveReminder_showSnackbar() {
        activeFragment()

        onView(withId(R.id.saveReminder)).perform(click())
        onView(withText("Please enter title")).check(matches(isDisplayed()))
        onView(withId(R.id.reminderTitle)).perform(typeText("T1"))
        closeSoftKeyboard()
        Thread.sleep(5000)
        onView(withId(R.id.saveReminder)).perform(click())
        closeSoftKeyboard()
        Thread.sleep(500)
        onView(withText("Please select location")).check(matches(isDisplayed()))

        Thread.sleep(2000)
    }

    private fun activeFragment(): NavController {
        val scenario = launchFragmentInContainer<SaveReminderFragment>(null, R.style.AppTheme)
        dataBindingIdlingResource.monitorFragment(scenario)
        val navController = Mockito.mock(NavController::class.java)
        scenario.onFragment { Navigation.setViewNavController(it.view!!, navController) }
        return navController
    }
}