package com.example.backstreet_cycles.ui.viewModels

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import androidx.test.internal.runner.junit4.AndroidJUnit4ClassRunner
import com.example.backstreet_cycles.data.repository.FakeCyclistRepoImpl
import com.example.backstreet_cycles.data.repository.FakeMapboxRepoImpl
import com.example.backstreet_cycles.data.repository.FakeTflRepoImpl
import com.example.backstreet_cycles.data.repository.FakeUserRepoImpl
import com.example.backstreet_cycles.ui.viewModel.ChangePasswordViewModel
import dagger.hilt.android.internal.Contexts
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
//import org.robolectric.RobolectricTestRunner
//import org.robolectric.annotation.Config

//@RunWith(RobolectricTestRunner::class)
//@Config(application = BackstreetApplication::class, manifest = Config.NONE)
@RunWith(AndroidJUnit4ClassRunner::class)

class EditUserProfileViewModelTest {

    private lateinit var changePasswordViewModel: ChangePasswordViewModel
    private lateinit var instrumentationContext: Context

    @Before
    fun setUp() {
        instrumentationContext = ApplicationProvider.getApplicationContext()
        val application = Contexts.getApplication(instrumentationContext)
        changePasswordViewModel = ChangePasswordViewModel(
            FakeTflRepoImpl(),
            FakeMapboxRepoImpl(),
            FakeCyclistRepoImpl(),
            FakeUserRepoImpl(),
            application,
            instrumentationContext
        )
    }

    @Test
    fun test_update_firstName_successfully()
    {

    }
}