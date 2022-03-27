package com.example.backstreet_cycles.ui.viewModel

import android.app.Application
import com.example.backstreet_cycles.data.remote.dto.DockDto
import com.example.backstreet_cycles.domain.repositoryInt.CyclistRepository
import com.example.backstreet_cycles.domain.repositoryInt.LocationRepository
import com.example.backstreet_cycles.domain.useCase.GetDockUseCase
import com.example.backstreet_cycles.domain.useCase.GetMapboxUseCase
import dagger.hilt.android.qualifiers.ApplicationContext
import org.junit.Assert.*
import org.junit.runner.RunWith
import org.junit.runners.JUnit4
import java.com.example.backstreet_cycles.FakeUserRepoImpl


@RunWith(JUnit4::class)
class LogInViewModelTest(app: Application){

    //private var logInViewModel: LogInViewModel

    private lateinit var getDockUseCase: GetDockUseCase

    private lateinit var getMapboxUseCase: GetMapboxUseCase

    private lateinit var locationRepository: LocationRepository

    private lateinit var cyclistRepository: CyclistRepository

//    init{
//        logInViewModel = LogInViewModel(getDockUseCase,
//            getMapboxUseCase,
//            locationRepository,
//            cyclistRepository,
//            app,
//            FakeUserRepoImpl()
//            )
//    }



}