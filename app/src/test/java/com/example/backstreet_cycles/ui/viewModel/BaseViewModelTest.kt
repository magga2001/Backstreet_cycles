package com.example.backstreet_cycles.ui.viewModel

import android.app.Application
import android.content.Context
import androidx.test.internal.runner.junit4.AndroidJUnit4ClassRunner
import androidx.test.runner.AndroidJUnit4
import com.google.common.truth.Truth.assertThat
import com.example.backstreet_cycles.FakeCyclistRepoImpl
import com.example.backstreet_cycles.FakeMapboxRepoImpl
import com.example.backstreet_cycles.FakeTflRepoImpl
import com.example.backstreet_cycles.FakeUserRepoImpl
import com.example.backstreet_cycles.common.BackstreetApplication
import dagger.hilt.android.qualifiers.ApplicationContext
import io.mockk.mockk
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(application = BackstreetApplication::class, manifest = Config.NONE)
class BaseViewModelTest {

    private lateinit var baseViewModel: BaseViewModel
//
//    @get:Rule
//    var hiltRule = HiltAndroidRule(this)

    @Before
    fun setUp()
    {
//        hiltRule.inject()
        val context = mockk<Context>(relaxed = true)
        val application = mockk<BackstreetApplication>(relaxed = true)
        baseViewModel = BaseViewModel(
            FakeTflRepoImpl(),
            FakeMapboxRepoImpl(),
            FakeCyclistRepoImpl(),
            FakeUserRepoImpl(),
            application,
            context
        )
    }

    @Test
    fun test_increment_cyclists()
    {
        baseViewModel.resetNumCyclists()
        baseViewModel.incrementNumCyclists()
        assertThat(baseViewModel.getNumCyclists()).isEqualTo(2)
    }

    //Probably wrong
    @Test
    fun test_decrement_cyclists()
    {
        baseViewModel.resetNumCyclists()
        baseViewModel.decrementNumCyclists()
        assertThat(baseViewModel.getNumCyclists()).isEqualTo(0)
    }

    @Test
    fun test_reset_number_of_cyclists()
    {
        baseViewModel.incrementNumCyclists()
        assertThat(baseViewModel.getNumCyclists()).isEqualTo(2)
        baseViewModel.resetNumCyclists()
        assertThat(baseViewModel.getNumCyclists()).isEqualTo(1)
    }
}