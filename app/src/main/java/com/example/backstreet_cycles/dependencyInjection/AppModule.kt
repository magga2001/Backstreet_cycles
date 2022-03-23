package com.example.backstreet_cycles.dependencyInjection

import com.example.backstreet_cycles.common.Constants
import com.example.backstreet_cycles.data.remote.TflApi
import com.example.backstreet_cycles.data.repository.TflRepositoryImpl
import com.example.backstreet_cycles.domain.repositoryInt.TflRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
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

}