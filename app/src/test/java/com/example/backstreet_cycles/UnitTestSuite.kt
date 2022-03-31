package com.example.backstreet_cycles

import com.example.backstreet_cycles.data.repository.*
import com.example.backstreet_cycles.ui.views.*
import org.junit.runner.RunWith
import org.junit.runners.Suite

@RunWith(Suite::class)
@Suite.SuiteClasses(

    CyclistRepositoryImplTest::class,
    LocationRepositoryImplTest::class,
    MapboxRepositoryImplTest::class,
    TflRepositoryImplTest::class,
//    UserRepositoryImplTest::class
)

class UnitTestSuite