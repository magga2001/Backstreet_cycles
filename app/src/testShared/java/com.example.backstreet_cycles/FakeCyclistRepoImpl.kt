package java.com.example.backstreet_cycles

import com.example.backstreet_cycles.data.repository.CyclistRepositoryImpl
import javax.inject.Inject

class FakeCyclistRepoImpl @Inject constructor() : CyclistRepositoryImpl(){

    private var testNumCyclists = 1

    override fun incrementNumCyclists(){
        ++testNumCyclists
    }

    override fun decrementNumCyclists(){
        --testNumCyclists
    }

    override fun resetNumCyclist() {
        testNumCyclists = 1
    }

    override fun getNumCyclists(): Int{
        return testNumCyclists
    }
}