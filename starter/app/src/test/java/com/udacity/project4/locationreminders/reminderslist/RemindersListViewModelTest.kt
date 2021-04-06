package com.udacity.project4.locationreminders.reminderslist

import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.udacity.project4.locationreminders.MainCoroutineRule
import com.udacity.project4.locationreminders.data.FakeDataSource
import com.udacity.project4.locationreminders.data.dto.ReminderDTO
import com.udacity.project4.locationreminders.getOrAwaitValue
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runBlockingTest
import org.hamcrest.Matchers.`is`
import org.junit.Assert
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.koin.core.context.stopKoin

@RunWith(AndroidJUnit4::class)
@ExperimentalCoroutinesApi
class RemindersListViewModelTest {

    @ExperimentalCoroutinesApi
    @get:Rule
    var mainCoroutineRule = MainCoroutineRule()

    private lateinit var viewModel: RemindersListViewModel
    private lateinit var remindersLocalRepository: FakeDataSource

    @Before
    fun setup() {
        stopKoin()
        remindersLocalRepository = FakeDataSource()
        viewModel = RemindersListViewModel(
            ApplicationProvider.getApplicationContext(),
            remindersLocalRepository
        )
    }

    @Test
    fun loadReminders_showNoData_ReminderListIsEmpty() = mainCoroutineRule.runBlockingTest {

        // When loading reminders
        viewModel.loadReminders()

        // Then reminders list is empty and no data is shown
        val reminderList = viewModel.remindersList.getOrAwaitValue()
        val noData = viewModel.showNoData.getOrAwaitValue()
        Assert.assertThat(reminderList.size, (`is`(0)))
        Assert.assertThat(noData, (`is`(true)))

    }

    @Test
    fun loadReminders_ShowSnackBarError() = mainCoroutineRule.runBlockingTest {
        //Given any Error
        remindersLocalRepository.setReturnError(true)

        // When loading reminders
        viewModel.loadReminders()

        // Then reminders list is empty and no data is shown
        val showSnackBar = viewModel.showSnackBar.getOrAwaitValue()
        Assert.assertThat(showSnackBar, (`is`("Exception getReminder")))

    }

    @Test
    fun loadReminders_Loading() = mainCoroutineRule.runBlockingTest {

        // Pause dispatcher
        mainCoroutineRule.pauseDispatcher()

        // When loading reminders
        viewModel.loadReminders()

        // Then - show loading
        Assert.assertThat(viewModel.showLoading.getOrAwaitValue(), (`is`(true)))

        // Execute pending coroutines
        mainCoroutineRule.resumeDispatcher()

        // Then - hide loading
        Assert.assertThat(viewModel.showLoading.getOrAwaitValue(), (`is`(false)))

    }

    @Test
    fun loadReminders_WithData() = mainCoroutineRule.runBlockingTest {
        // Given I have some reminders
        remindersLocalRepository.saveReminder(
            ReminderDTO(
                "Central Park",
                "",
                "Central Park",
                40.785091,
                73.968285
            )
        )

        // When loading reminders
        viewModel.loadReminders()

        // Then reminders list is empty and no data is shown
        val reminderList = viewModel.remindersList.getOrAwaitValue()
        Assert.assertThat(reminderList.size, (`is`(1)))
        Assert.assertThat(reminderList.first().title, (`is`("Central Park")))
        Assert.assertThat(reminderList.first().description, (`is`("")))
        Assert.assertThat(reminderList.first().location, (`is`("Central Park")))
        Assert.assertThat(reminderList.first().latitude, (`is`(40.785091)))
        Assert.assertThat(reminderList.first().longitude, (`is`(73.968285)))
    }

}