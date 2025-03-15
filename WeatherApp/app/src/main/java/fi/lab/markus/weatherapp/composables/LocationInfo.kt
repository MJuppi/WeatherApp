package fi.lab.markus.weatherapp.composables

import android.location.Location
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

// Manages the state and logic for location and temperature unit information.
class LocationInfoManager(private val viewModel: LocationViewModel) {

    // State variables
    var gpsBool by mutableStateOf(true)
        private set

    // Flow to hold the current location.
    private val _locationState = MutableStateFlow<Location?>(null)
    val locationState: StateFlow<Location?> = _locationState.asStateFlow()

    // Lambdas to update the state in the composable
    private var _onSearchCity: (String) -> Unit = {}
    private var _onTempUnitChange: () -> Unit = {}

    init {
        // Collect location updates from the ViewModel.
        viewModel.viewModelScope.launch {
            viewModel.location.collect {
                _locationState.value = it
            }
        }
    }

    // Event handlers
    fun onTempUnitChange() {
        _onTempUnitChange()
    }

    fun onSearchCity(city: String) {
        _onSearchCity(city)
        gpsBool = false
    }

    fun onUseGpsLocation() {
        _onSearchCity("")
        gpsBool = true
    }

    // Set listeners for state changes
    fun setOnSearchCityListener(listener: (String) -> Unit) {
        _onSearchCity = listener
    }

    fun setOnTempUnitChangeListener(listener: () -> Unit) {
        _onTempUnitChange = listener
    }
}

// UI for entering location information and selecting temperature units.
@Composable
fun LocationInfoUI(
    cityName: String,
    onCityNameChange: (String) -> Unit,
    tempUnit: String,
    onTempUnitChange: () -> Unit,
    onSearchCity: (String) -> Unit,
    onUseGpsLocation: () -> Unit,
    gpsBool: Boolean,
    searchCity: String,
    locationContent: @Composable () -> Unit
) {
    val keyboardController = LocalSoftwareKeyboardController.current
    Column(modifier = Modifier.fillMaxWidth()) {
        TextField(
            value = cityName,
            onValueChange = onCityNameChange,
            label = { Text("Enter city name") },
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
            keyboardActions = KeyboardActions(onSearch = {
                onSearchCity(cityName.trim())
                keyboardController?.hide()
            }),
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 25.dp, vertical = 10.dp),
        )
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 25.dp, vertical = 10.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Button(
                onClick = onTempUnitChange,
                modifier = Modifier
                    .height(50.dp)
                    .padding(end = 10.dp)
            ) { Text("℃/℉") }
            Button(
                onClick = { onSearchCity(cityName) },
                modifier = Modifier
                    .height(50.dp)
                    .fillMaxWidth()
            ) { Text("Search") }
        }
        if (!gpsBool) {
            Button(
                onClick = onUseGpsLocation,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 25.dp)
            ) { Text("Use GPS location") }
        }
        if (searchCity != "") {
            locationContent()
        }
        if (gpsBool) {
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                locationContent()
            }
        }
    }
}

// Main screen composable for displaying location and weather information.
@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun LocationInfoScreen(viewModel: LocationViewModel) {
    val manager = LocationInfoManager(viewModel)
    val locationState by manager.locationState.collectAsState()

    // State variables for search city and temperature unit.
    var searchCity by remember { mutableStateOf("") }
    var tempUnit by remember { mutableStateOf("celsius") }
    var cityName by remember { mutableStateOf("") }

    // Set listeners to update the state in the composable
    manager.setOnSearchCityListener { city ->
        searchCity = city
    }

    manager.setOnTempUnitChangeListener {
        tempUnit = if (tempUnit == "celsius") "fahrenheit" else "celsius"
    }

    LocationInfoUI(
        cityName = cityName,
        onCityNameChange = { cityName = it },
        tempUnit = tempUnit,
        onTempUnitChange = manager::onTempUnitChange,
        onSearchCity = manager::onSearchCity,
        onUseGpsLocation = manager::onUseGpsLocation,
        gpsBool = manager.gpsBool,
        searchCity = searchCity,
        locationContent = {
            if (searchCity != "") {
                LocationView(searchCity, tempUnit)
            }
            if (manager.gpsBool) {
                if (locationState?.latitude != null || locationState?.longitude != null) {
                    Text(text = "Using current GPS location", modifier = Modifier.padding(10.dp))
                    locationState?.let { loc ->
                        WeatherView(
                            loc.latitude.toFloat(), loc.longitude.toFloat(), "auto", tempUnit
                        )
                    }
                } else {
                    Column(modifier = Modifier.padding(30.dp)) {
                        Text(text = "Location permissions not granted!")
                    }
                }
            }
        }
    )
}