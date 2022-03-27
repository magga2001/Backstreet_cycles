package com.example.backstreet_cycles.dependencyInjection

import com.example.backstreet_cycles.common.Constants
import com.example.backstreet_cycles.data.local.TouristAttractionFile
import com.example.backstreet_cycles.data.remote.MapboxApi
import com.example.backstreet_cycles.data.remote.TflApi
import com.example.backstreet_cycles.data.repository.CyclistRepositoryImpl
import com.example.backstreet_cycles.data.repository.LocationRepositoryImpl
import com.example.backstreet_cycles.data.repository.MapboxRepositoryImpl
import com.example.backstreet_cycles.data.repository.TflRepositoryImpl
import com.example.backstreet_cycles.domain.repositoryInt.CyclistRepository
import com.example.backstreet_cycles.domain.repositoryInt.LocationRepository
import com.example.backstreet_cycles.domain.repositoryInt.MapboxRepository
import com.example.backstreet_cycles.domain.repositoryInt.TflRepository
import com.example.backstreet_cycles.domain.useCase.GetDockUseCase
import com.example.backstreet_cycles.service.WorkerService
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideTflApi(): TflApi {
        return Retrofit.Builder()
            .baseUrl(Constants.BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(TflApi::class.java)
    }

    @Provides
    @Singleton
    fun provideTflRepository(api: TflApi): TflRepository {
        return TflRepositoryImpl(api)
    }

    @Provides
    @Singleton
    fun provideMapboxApi(): MapboxApi {
        return MapboxApi
    }

    @Provides
    @Singleton
    fun provideMapboxRepository(api: MapboxApi): MapboxRepository {
        return MapboxRepositoryImpl(api)
    }

    @Provides
    @Singleton
    fun provideTouristAttractionFile(): TouristAttractionFile {
        return TouristAttractionFile
    }

    @Provides
    @Singleton
    fun provideLocationRepository(file: TouristAttractionFile): LocationRepository {
        return LocationRepositoryImpl(file)
    }

    @Provides
    @Singleton
    fun provideCyclistRepository(): CyclistRepository {
        return CyclistRepositoryImpl()
    }

    @Provides
    @Singleton
    fun provideWorkerService(tflRepository: TflRepository): GetDockUseCase {
        return GetDockUseCase(tflRepository)
    }

}