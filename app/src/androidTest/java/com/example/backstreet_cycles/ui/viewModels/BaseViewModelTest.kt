package com.example.backstreet_cycles.ui.viewModels

import android.app.Application
import android.content.Context
import android.support.test.InstrumentationRegistry
import androidx.test.core.app.ApplicationProvider
import androidx.test.internal.runner.junit4.AndroidJUnit4ClassRunner
//import com.google.common.truth.Truth.assertThat
import com.example.backstreet_cycles.FakeCyclistRepoImpl
import com.example.backstreet_cycles.FakeMapboxRepoImpl
import com.example.backstreet_cycles.FakeTflRepoImpl
import com.example.backstreet_cycles.FakeUserRepoImpl
import com.example.backstreet_cycles.common.BackstreetApplication
import com.example.backstreet_cycles.ui.viewModel.BaseViewModel
import dagger.hilt.android.internal.Contexts.getApplication
import io.mockk.mockk
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
//import org.robolectric.RobolectricTestRunner
//import org.robolectric.annotation.Config

//@RunWith(RobolectricTestRunner::class)
@RunWith(AndroidJUnit4ClassRunner::class)
//@Config(application = BackstreetApplication::class, manifest = Config.NONE)
class BaseViewModelTest {

    private lateinit var baseViewModel: BaseViewModel
    lateinit var instrumentationContext: Context

//
//    @get:Rule
//    var hiltRule = HiltAndroidRule(this)

    @Before
    fun setUp()
    {
//        hiltRule.inject()
//        val context = mockk<Context>(relaxed = true)
//        val application = mockk<BackstreetApplication>(relaxed = true)
        instrumentationContext = ApplicationProvider.getApplicationContext();
        val application = getApplication(instrumentationContext)
        baseViewModel = BaseViewModel(
            FakeTflRepoImpl(),
            FakeMapboxRepoImpl(),
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
//        assertThat(baseViewModel.getNumCyclists()).isEqualTo(2)
    }

    //Probably wrong
    @Test
    fun test_decrement_cyclists()
    {
        baseViewModel.resetNumCyclists()
        baseViewModel.decrementNumCyclists()
        assert(baseViewModel.getNumCyclists() == 0)
//        assertThat(baseViewModel.getNumCyclists()).isEqualTo(0)
    }

    @Test
    fun test_reset_number_of_cyclists()
    {
        baseViewModel.incrementNumCyclists()
        assert(baseViewModel.getNumCyclists() == 2)
        //assertThat(baseViewModel.getNumCyclists()).isEqualTo(2)
        baseViewModel.resetNumCyclists()
        assert(baseViewModel.getNumCyclists() == 1)
        //assertThat(baseViewModel.getNumCyclists()).isEqualTo(1)
    }
}