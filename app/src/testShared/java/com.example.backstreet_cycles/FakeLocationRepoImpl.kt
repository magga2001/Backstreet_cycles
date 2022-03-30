package java.com.example.backstreet_cycles
//
//import android.app.Application
//import com.example.backstreet_cycles.data.local.TouristAttractionFile
//import com.example.backstreet_cycles.data.repository.LocationRepositoryImpl
//import com.example.backstreet_cycles.domain.model.dto.Locations
//import javax.inject.Inject
//
//class FakeLocationRepoImpl @Inject constructor() : LocationRepositoryImpl(TouristAttractionFile){
//
//    override fun loadLocations(application: Application) {
//        TouristAttractionFile.loadLocations(application)
//    }
//
//    override fun getTouristLocations(): MutableList<Locations> {
//        return TouristAttractionFile.getTouristLocations()
//    }
//
//}