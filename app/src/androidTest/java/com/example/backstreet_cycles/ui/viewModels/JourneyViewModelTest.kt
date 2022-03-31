package com.example.backstreet_cycles.ui.viewModels

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import androidx.test.internal.runner.junit4.AndroidJUnit4ClassRunner
import com.example.backstreet_cycles.data.repository.FakeCyclistRepoImpl
import com.example.backstreet_cycles.data.repository.FakeMapboxRepoImpl
import com.example.backstreet_cycles.data.repository.FakeTflRepoImpl
import com.example.backstreet_cycles.data.repository.FakeUserRepoImpl
import com.example.backstreet_cycles.ui.viewModel.JourneyViewModel
import dagger.hilt.android.internal.Contexts
import org.junit.Before
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4ClassRunner::class)
class JourneyViewModelTest {

    private lateinit var journeyViewModel: JourneyViewModel
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
        val application = Contexts.getApplication(instrumentationContext)
        journeyViewModel = JourneyViewModel(
            FakeTflRepoImpl(),
            FakeMapboxRepoImpl(),
            FakeCyclistRepoImpl(),
            FakeUserRepoImpl(),
            application,
            instrumentationContext
        )
    }
}