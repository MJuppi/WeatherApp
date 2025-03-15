package fi.lab.markus.weatherapp.composables

import android.app.Application
import android.location.Location
import androidx.lifecycle.AndroidViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class LocationViewModel(application: Application) : AndroidViewModel(application) {
    private val locationRepository = LocationRepository(application)
    private val _location = MutableStateFlow<Location?>(null)
    val location: StateFlow<Location?> = _location.asStateFlow()

    /**
     * Starts location updates by calling the startLocationUpdates function from the
     * locationRepository.
     */
    fun startLocationUpdates() {
        locationRepository.startLocationUpdates { location -> _location.value = location }
    }
}
