package com.example.backstreet_cycles

import com.example.backstreet_cycles.common.ConstantsTest
import com.example.backstreet_cycles.data.local.TouristAttractionFileTest
import com.example.backstreet_cycles.data.repository.*
import com.example.backstreet_cycles.domain.utils.*
import com.example.backstreet_cycles.ui.viewModel.ChangePasswordViewModel
import com.example.backstreet_cycles.ui.viewModel.EditUserProfileViewModel
import com.example.backstreet_cycles.ui.viewModel.JourneyHistoryViewModel
import com.example.backstreet_cycles.ui.viewModels.*
import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.junit.runner.RunWith
import org.junit.runners.Suite

@ExperimentalCoroutinesApi
@RunWith(Suite::class)
@Suite.SuiteClasses(
    ConstantsTest::class,
    TouristAttractionFileTest::class,
    CyclistRepositoryImplTest::class,
    LocationRepositoryImplTest::class,
    MapboxRepositoryImplTest::class,
    TflRepositoryImplTest::class,
    UserRepositoryImplTest::class,
    ConvertHelperTest::class,
    JsonHelperTest::class,
    MapInfoHelperTest::class,
    PlannerHelperTest::class,
    ChangePasswordViewModelTest::class,
    EditUserProfileViewModelTest::class,
    ForgotPasswordViewModelTest::class,
    HomePageViewModelTest::class,
    JourneyHistoryViewModelUnitTest::class,
    LogInViewModelTest::class,
    SignUpViewModelTest::class
)

class UnitTestSuite