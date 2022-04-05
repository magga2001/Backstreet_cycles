package com.example.backstreet_cycles.dependencyInjection

import com.example.backstreet_cycles.common.Constants
import com.example.backstreet_cycles.data.local.TouristAttractionFile
import com.example.backstreet_cycles.data.remote.TflApi
import com.example.backstreet_cycles.data.repository.*
import com.example.backstreet_cycles.domain.repositoryInt.*
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import javax.inject.Singleton


/**
 * All the dependencies bound into the dependency graph
 */
@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideTflApi(): TflApi {
        val adapterFactory = KotlinJsonAdapterFactory()
        val moshi = Moshi.Builder().add(adapterFactory).build()
        val moshiFactory = MoshiConverterFactory.create(moshi)

        return Retrofit.Builder()
            .baseUrl(Constants.BASE_URL)
            .addConverterFactory(moshiFactory)
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
    fun provideMapboxRepository(): MapboxRepository {
        return MapboxRepositoryImpl()
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
    fun provideFirestore() = FirebaseFirestore.getInstance()

    @Provides
    @Singleton
    fun provideFirebaseAuth() = FirebaseAuth.getInstance()

    @Provides
    fun provideUserRepository(firebaseAuth: FirebaseAuth, fireStore: FirebaseFirestore): UserRepository {
        return UserRepositoryImpl(firebaseAuth, fireStore)
    }
}