package com.example.backstreet_cycles_instrumentation_testing

import com.example.backstreet_cycles.dependencyInjection.AppModule
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import dagger.hilt.android.testing.HiltTestApplication
import dagger.hilt.android.testing.UninstallModules
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import org.robolectric.annotation.LooperMode

@UninstallModules(AppModule::class)
@HiltAndroidTest
@Config(application = HiltTestApplication::class)
@RunWith(RobolectricTestRunner::class)
@LooperMode(LooperMode.Mode.PAUSED)
class ExampleTest {

    @get:Rule(order = 0)
    var hiltRule = HiltAndroidRule(this)

//    @Inject
//    lateinit var touristAttractionFileFactory: TouristAttractionFile
//
//    @Inject
//    lateinit var userRepository: UserRepository
//
//    @Inject
//    lateinit var firestore: FirebaseFirestore
//
//    @Inject
//    lateinit var firebaseAuth: FirebaseAuth
//
//    @Inject
//    lateinit var cyclistRepositoryImpl: CyclistRepositoryImpl



    @Before
    fun init(){
        hiltRule.inject()

    }

    @Test
    fun someTest(){
        assert(true)
    }

//    @Module
//    @InstallIn(SingletonComponent::class)
//    object TestModule {
//
//        @Provides
//        @Singleton
//        fun provideTflApi(): TflApi {
//            val adapterFactory = KotlinJsonAdapterFactory()
//            val moshi = Moshi.Builder().add(adapterFactory).build()
//            val moshiFactory = MoshiConverterFactory.create(moshi)
//
//            return Retrofit.Builder()
//                .baseUrl(Constants.BASE_URL)
//                .addConverterFactory(moshiFactory)
//                .build()
//                .create(TflApi::class.java)
//        }
//
//        @Provides
//        @Singleton
//        fun provideTflRepository(api: TflApi): TflRepository {
//            return TflRepositoryImpl(api)
//        }
//
//        @Provides
//        @Singleton
//        fun provideMapboxApi(): MapboxApi {
//            return MapboxApi
//        }
//
//        @Provides
//        @Singleton
//        fun provideMapboxRepository(api: MapboxApi): MapboxRepository {
//            return MapboxRepositoryImpl(api)
//        }
//
//        @Provides
//        @Singleton
//        fun provideTouristAttractionFile(): TouristAttractionFile {
//            return TouristAttractionFile
//        }
//
//        @Provides
//        @Singleton
//        fun provideLocationRepository(file: TouristAttractionFile): LocationRepository {
//            return LocationRepositoryImpl(file)
//        }
//
//        @Provides
//        @Singleton
//        fun provideCyclistRepository(): CyclistRepository {
//            return CyclistRepositoryImpl()
//        }
//
//        @Provides
//        @Singleton
//        fun provideFirestore() = FirebaseFirestore.getInstance()
//
//        @Provides
//        @Singleton
//        fun provideFirebaseAuth() = FirebaseAuth.getInstance()
//
//        @Provides
//        fun provideUserRepository(
//            fireStore: FirebaseFirestore,
//            firebaseAuth: FirebaseAuth
//        ): UserRepository {
//            return UserRepositoryImpl(fireStore, firebaseAuth)
//        }
//    }

}