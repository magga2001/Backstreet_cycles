package java.com.example.backstreet_cycles
//
//import com.example.backstreet_cycles.data.repository.TflRepositoryImpl
//import com.example.backstreet_cycles.dependencyInjection.AppModule
//import com.example.backstreet_cycles.domain.model.dto.Dock
//import javax.inject.Inject
//
//class FakeTflRepoImpl @Inject constructor() : TflRepositoryImpl(AppModule.provideTflApi()) {
//
//    var testDocks = mutableListOf<Dock>()
//
//    override fun getCurrentDocks(): MutableList<Dock> {
//        return super.getCurrentDocks()
//    }
//
//    override fun setCurrentDocks(docks: MutableList<Dock>) {
//        this.testDocks = docks
//    }
//
//}