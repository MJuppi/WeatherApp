package fi.lab.markus.weatherapp.composables

import android.location.Location
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
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
import java.util.Locale
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

// Manages the state and logic for location and temperature unit information.
class LocationInfoManager(private val viewModel: LocationViewModel) {

    // State variables
    var gpsBool by mutableStateOf(true)

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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WeatherTopAppBar(
    isGpsMode: Boolean,
    onModeChange: (Boolean) -> Unit,
    onRefresh: () -> Unit,
    onNavigateToSettings: () -> Unit
) {
    TopAppBar(
        title = {
            Text(if (isGpsMode) "GPS Weather" else "City Search")
        },
        actions = {
            IconButton(onClick = onRefresh) {
                Icon(imageVector = Icons.Default.Refresh, contentDescription = "Refresh")
            }
            IconButton(onClick = { onModeChange(!isGpsMode) }) {
                Icon(
                    imageVector = if (isGpsMode) Icons.Default.Search else Icons.Default.LocationOn,
                    contentDescription = "Switch Mode"
                )
            }
            IconButton(onClick = onNavigateToSettings) {
                Icon(imageVector = Icons.Default.Settings, contentDescription = "Settings")
            }
        }
    )
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
    onRefresh: () -> Unit,
    onNavigateToSettings: () -> Unit,
    locationContent: @Composable () -> Unit
) {
    val keyboardController = LocalSoftwareKeyboardController.current

    Scaffold(
        topBar = {
            WeatherTopAppBar(
                isGpsMode = gpsBool,
                onModeChange = { useGps ->
                    if (useGps) onUseGpsLocation() else onSearchCity("")
                },
                onRefresh = onRefresh,
                onNavigateToSettings = onNavigateToSettings
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
        ) {
            if (!gpsBool) {
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
            }

            if (searchCity != "" || gpsBool) {
                locationContent()
            }
            
            // Add some spacer at the bottom for better scroll feel
            Spacer(modifier = Modifier.height(50.dp))
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun LocationContent(
    gpsBool: Boolean,
    searchCity: String,
    tempUnit: String,
    windUnit: String,
    precipUnit: String,
    permissionsGranted: Boolean?,
    locationState: Location?,
    refreshTrigger: Int,
    forecastDays: Int,
    onRequestPermissions: () -> Unit
) {
    if (searchCity.isNotEmpty() && !gpsBool) {
        LocationView(searchCity, tempUnit, refreshTrigger, forecastDays, windUnit, precipUnit)
    }
    
    if (gpsBool) {
        when (permissionsGranted) {
            true -> {
                if (locationState != null) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = String.format(Locale.ROOT, "Location: %.4f, %.4f", locationState.latitude, locationState.longitude),
                            modifier = Modifier.padding(10.dp)
                        )
                        WeatherView(
                            locationState.latitude.toFloat(),
                            locationState.longitude.toFloat(),
                            "auto",
                            tempUnit,
                            refreshTrigger,
                            forecastDays,
                            windUnit,
                            precipUnit
                        )
                    }
                } else {
                    Box(modifier = Modifier.fillMaxWidth().padding(30.dp), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator()
                    }
                }
            }
            false -> {
                Column(
                    modifier = Modifier.padding(30.dp).fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(text = "Location permissions not granted!")
                    Button(
                        onClick = onRequestPermissions,
                        modifier = Modifier.padding(top = 8.dp)
                    ) {
                        Text("Grant Permissions")
                    }
                }
            }
            null -> {
                Box(modifier = Modifier.fillMaxWidth().padding(30.dp), contentAlignment = Alignment.Center) {
                    Text(text = "Checking permissions...")
                }
            }
        }
    }
}

// Main screen composable for displaying location and weather information.
@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun LocationInfoScreen(
    viewModel: LocationViewModel,
    onRequestPermissions: () -> Unit,
    tempUnit: String,
    onTempUnitChange: () -> Unit,
    forecastDays: Int,
    windUnit: String,
    precipUnit: String,
    onNavigateToSettings: () -> Unit
) {
    val manager = remember { LocationInfoManager(viewModel) }
    val locationState by manager.locationState.collectAsState()
    val permissionsGranted by viewModel.permissionsGranted.collectAsState()

    var searchCity by remember { mutableStateOf("") }
    var cityName by remember { mutableStateOf("") }
    var refreshTrigger by remember { androidx.compose.runtime.mutableIntStateOf(0) }

    manager.setOnSearchCityListener { city -> 
        searchCity = city
        refreshTrigger++
    }
    manager.setOnTempUnitChangeListener { onTempUnitChange() }

    LaunchedEffect(permissionsGranted) {
        if (permissionsGranted == false) {
            manager.gpsBool = false
        }
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
        onRefresh = { refreshTrigger++ },
        onNavigateToSettings = onNavigateToSettings,
        locationContent = {
            LocationContent(
                gpsBool = manager.gpsBool,
                searchCity = searchCity,
                tempUnit = tempUnit,
                windUnit = windUnit,
                precipUnit = precipUnit,
                permissionsGranted = permissionsGranted,
                locationState = locationState,
                refreshTrigger = refreshTrigger,
                forecastDays = forecastDays,
                onRequestPermissions = onRequestPermissions
            )
        }
    )
}
