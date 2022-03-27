package com.example.backstreet_cycles.ui.viewModel

import android.content.Context
import com.example.backstreet_cycles.domain.repositoryInt.CyclistRepository
import com.example.backstreet_cycles.domain.repositoryInt.LocationRepository
import com.example.backstreet_cycles.domain.repositoryInt.UserRepository
import com.example.backstreet_cycles.domain.useCase.GetDockUseCase
import com.example.backstreet_cycles.domain.useCase.GetMapboxUseCase
import com.google.firebase.auth.FirebaseUser
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

@HiltViewModel
class SignUpViewModel @Inject constructor(
    getDockUseCase: GetDockUseCase,
    getMapboxUseCase: GetMapboxUseCase,
    locationRepository: LocationRepository,
    cyclistRepository: CyclistRepository,
    userRepository: UserRepository,
    @ApplicationContext applicationContext: Context
) : BaseViewModel(getDockUseCase, getMapboxUseCase, locationRepository, cyclistRepository, userRepository, applicationContext){

    fun register(firstName:String, lastName:String, email:String, password:String): FirebaseUser?{
        return userRepository.register(firstName,lastName,email,password)
    }
}