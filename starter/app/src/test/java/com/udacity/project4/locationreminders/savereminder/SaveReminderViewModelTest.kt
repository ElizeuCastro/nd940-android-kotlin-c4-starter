package com.udacity.project4.locationreminders.savereminder

import android.app.Application
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.PointOfInterest
import com.udacity.project4.R
import com.udacity.project4.base.NavigationCommand
import com.udacity.project4.locationreminders.MainCoroutineRule
import com.udacity.project4.locationreminders.data.FakeDataSource
import com.udacity.project4.locationreminders.getOrAwaitValue
import com.udacity.project4.locationreminders.reminderslist.ReminderDataItem

import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runBlockingTest
import org.hamcrest.Matchers
import org.hamcrest.Matchers.`is`
import org.hamcrest.Matchers.nullValue
import org.junit.Assert
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.koin.core.context.stopKoin

@ExperimentalCoroutinesApi
@RunWith(AndroidJUnit4::class)
class SaveReminderViewModelTest {


    @ExperimentalCoroutinesApi
    @get:Rule
    var mainCoroutineRule = MainCoroutineRule()

    private lateinit var app: Application
    private lateinit var viewModel: SaveReminderViewModel
    private lateinit var remindersLocalRepository: FakeDataSource

    @Before
    fun setup() {
        stopKoin()
        app = ApplicationProvider.getApplicationContext()
        remindersLocalRepository = FakeDataSource()
        viewModel = SaveReminderViewModel(
            app,
            remindersLocalRepository
        )
    }

    @Test
    fun validateAndSaveReminder_EmptyTitle() {

        // Give a reminder with empty title
        val reminderDataItem = ReminderDataItem(
            "",
            "",
            "Central Park",
            40.785091,
            73.968285
        )

        // When validate and save reminder
        viewModel.validateAndSaveReminder(reminderDataItem)

        // The snackBar with enter title is shown
        Assert.assertThat(
            viewModel.showSnackBarInt.getOrAwaitValue(),
            (Matchers.`is`(R.string.err_enter_title))
        )
    }

    @Test
    fun validateAndSaveReminder_NullTitle() {

        // Give a reminder with null title
        val reminderDataItem = ReminderDataItem(
            null,
            "",
            "Central Park",
            40.785091,
            73.968285
        )

        // When validate and save reminder
        viewModel.validateAndSaveReminder(reminderDataItem)

        // The snackBar with enter title is shown
        Assert.assertThat(
            viewModel.showSnackBarInt.getOrAwaitValue(),
            (Matchers.`is`(R.string.err_enter_title))
        )
    }

    @Test
    fun validateAndSaveReminder_EmptyLocation() {

        // Give a reminder with empty location
        val reminderDataItem = ReminderDataItem(
            "Central Park",
            "",
            "",
            40.785091,
            73.968285
        )

        // When validate and save reminder
        viewModel.validateAndSaveReminder(reminderDataItem)

        // The snackBar with select location message is shown
        Assert.assertThat(
            viewModel.showSnackBarInt.getOrAwaitValue(),
            (Matchers.`is`(R.string.err_select_location))
        )
    }

    @Test
    fun validateAndSaveReminder_NullLocation() {

        // Give a reminder with null location
        val reminderDataItem = ReminderDataItem(
            "Central Park",
            "",
            null,
            40.785091,
            73.968285
        )

        // When validate and save reminder
        viewModel.validateAndSaveReminder(reminderDataItem)

        // The snackBar with select location message is shown
        Assert.assertThat(
            viewModel.showSnackBarInt.getOrAwaitValue(),
            (Matchers.`is`(R.string.err_select_location))
        )
    }

    @Test
    fun validateAndSaveReminder_SaveReminder() = mainCoroutineRule.runBlockingTest {

        // Give a reminder
        val reminderDataItem = ReminderDataItem(
            "Central Park",
            "",
            "Central Park",
            40.785091,
            73.968285
        )

        // When save reminder
        viewModel.validateAndSaveReminder(reminderDataItem)


        // The reminder is saved and saved toast message is shown
        Assert.assertThat(
            viewModel.reminderId.getOrAwaitValue(),
            (`is`(reminderDataItem.id))
        )
        Assert.assertThat(
            viewModel.showToast.getOrAwaitValue(),
            (`is`(app.getString(R.string.reminder_saved)))
        )
        Assert.assertThat<NavigationCommand>(
            viewModel.navigationCommand.getOrAwaitValue(),
            (`is`(NavigationCommand.Back))
        )
    }

    @Test
    fun validateAndSaveReminder_SaveReminder_Loading() = mainCoroutineRule.runBlockingTest {

        // Pause dispatcher
        mainCoroutineRule.pauseDispatcher()

        val reminderDataItem = ReminderDataItem(
            "Central Park",
            "",
            "Central Park",
            40.785091,
            73.968285
        )

        // Given Save Reminder
        viewModel.validateAndSaveReminder(reminderDataItem)

        // Then - show loading
        Assert.assertThat(viewModel.showLoading.getOrAwaitValue(), (`is`(true)))

        // Execute pending coroutines
        mainCoroutineRule.resumeDispatcher()

        // Then - hide loading
        Assert.assertThat(viewModel.showLoading.getOrAwaitValue(), (`is`(false)))

    }

    @Test
    fun onClear() {

        viewModel.onClear()

        Assert.assertThat(viewModel.reminderId.getOrAwaitValue(), nullValue())
        Assert.assertThat(viewModel.reminderTitle.getOrAwaitValue(), nullValue())
        Assert.assertThat(viewModel.reminderDescription.getOrAwaitValue(), nullValue())
        Assert.assertThat(viewModel.selectedPOI.getOrAwaitValue(), nullValue())
        Assert.assertThat(viewModel.latitude.getOrAwaitValue(), nullValue())
        Assert.assertThat(viewModel.longitude.getOrAwaitValue(), nullValue())
    }

    @Test
    fun reminder_selected_location_str() {

        // Given selected POI
        viewModel.selectedPOI.value = PointOfInterest(LatLng(40.785091, 73.968285), "", "Central Park")

        // Then transform location name
        Assert.assertThat(viewModel.reminderSelectedLocationStr.getOrAwaitValue(), (`is`("Central Park")))
    }


}