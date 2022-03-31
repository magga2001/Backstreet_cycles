package com.example.backstreet_cycles

import com.example.backstreet_cycles.data.local.TouristAttractionFileTest
import com.example.backstreet_cycles.data.repository.CyclistRepositoryImplTest
import com.example.backstreet_cycles.data.repository.LocationRepositoryImplTest
import com.example.backstreet_cycles.data.repository.MapboxRepositoryImplTest
import com.example.backstreet_cycles.data.repository.TflRepositoryImplTest
import org.junit.runner.RunWith
import org.junit.runners.Suite

@RunWith(Suite::class)
@Suite.SuiteClasses(

    TouristAttractionFileTest::class,
    CyclistRepositoryImplTest::class,
    LocationRepositoryImplTest::class,
    MapboxRepositoryImplTest::class,
    TflRepositoryImplTest::class,
//    UserRepositoryImplTest::class
)

class UnitTestSuite