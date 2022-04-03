package com.example.backstreet_cycles.ui.viewModels

import android.content.Context
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.test.core.app.ApplicationProvider
import androidx.test.internal.runner.junit4.AndroidJUnit4ClassRunner
import com.example.backstreet_cycles.common.TestAppModule
//import com.google.common.truth.Truth.assertThat
import com.example.backstreet_cycles.data.repository.FakeCyclistRepoImpl
import com.example.backstreet_cycles.data.repository.FakeMapboxRepoImpl
import com.example.backstreet_cycles.data.repository.FakeTflRepoImpl
import com.example.backstreet_cycles.data.repository.FakeUserRepoImpl
import com.example.backstreet_cycles.ui.viewModel.BaseViewModel
import dagger.hilt.android.internal.Contexts.getApplication
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4ClassRunner::class)
class BaseViewModelTest {

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    private lateinit var baseViewModel: BaseViewModel
    lateinit var instrumentationContext: Context

    @Before
    fun setUp()
    {
        instrumentationContext = ApplicationProvider.getApplicationContext();
        val application = getApplication(instrumentationContext)
        baseViewModel = BaseViewModel(
            FakeTflRepoImpl(),
            FakeMapboxRepoImpl(TestAppModule.provideRoute()),
            FakeCyclistRepoImpl(),
            FakeUserRepoImpl(),
            application,
            instrumentationContext
        )
    }

    @Test
    fun test_increment_cyclists()
    {
        baseViewModel.resetNumCyclists()
        baseViewModel.incrementNumCyclists()
        assert(baseViewModel.getNumCyclists() == 2)
    }

    @Test
    fun test_decrement_cyclists()
    {
        baseViewModel.resetNumCyclists()
        baseViewModel.incrementNumCyclists()
        baseViewModel.decrementNumCyclists()
        assert(baseViewModel.getNumCyclists() == 1)
    }

    @Test
    fun test_reset_number_of_cyclists()
    {
        baseViewModel.incrementNumCyclists()
        assert(baseViewModel.getNumCyclists() == 2)
        baseViewModel.resetNumCyclists()
        assert(baseViewModel.getNumCyclists() == 1)
    }
}