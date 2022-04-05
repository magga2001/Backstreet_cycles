package com.example.backstreet_cycles.ui.viewModels

import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.junit.runner.RunWith
import org.junit.runners.Suite

@ExperimentalCoroutinesApi
@RunWith(Suite::class)
@Suite.SuiteClasses(

    BaseViewModelTest::class,
    JourneyHistoryViewModelTest::class,
    JourneyViewModelTest::class,
    LoadingViewModelTest::class,
    SplashScreenViewModelTest::class,
)

class ViewModelTestSuite