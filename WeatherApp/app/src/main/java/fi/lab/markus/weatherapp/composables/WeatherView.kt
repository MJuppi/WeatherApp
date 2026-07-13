package fi.lab.markus.weatherapp.composables

import android.annotation.SuppressLint
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import java.time.ZoneId
import java.time.ZonedDateTime

/**
 * Fetches weather data from the Open-Meteo API.
 */
@RequiresApi(Build.VERSION_CODES.O)
suspend fun fetchWeatherData(
    latitude: Float,
    longitude: Float,
    timezone: String,
    tempUnit: String,
    api: WeatherService,
    forecastDays: Int,
    windUnit: String,
    precipUnit: String
): WeatherResponse? {
    return try {
        api.getWeather(
            latitude, 
            longitude, 
            timezone = timezone, 
            temperatureUnit = tempUnit,
            forecastDays = forecastDays,
            windSpeedUnit = windUnit,
            precipitationUnit = precipUnit
        )
    } catch (e: Exception) {
        println("Error fetching weather data: ${e.message}")
        null
    }
}

/**
 * Calculates and formats the current time based on the weather data.
 */
@SuppressLint("DefaultLocale")
@RequiresApi(Build.VERSION_CODES.O)
fun getCurrentTimeInfo(weatherResponse: WeatherResponse): Pair<String, Int> {
    val locationTimeZoneId = weatherResponse.timezone
    val zoneId = ZoneId.of(locationTimeZoneId)
    val currentTime = ZonedDateTime.now(zoneId).hour
    val formattedTime = String.format("%02d:00", currentTime)
    return Pair(formattedTime, currentTime)
}

/**
 * Renders the weather information UI.
 */
@SuppressLint("DefaultLocale")
@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun WeatherView(
    latitude: Float,
    longitude: Float,
    timezone: String,
    tempUnit: String,
    refreshTrigger: Int,
    forecastDays: Int,
    windUnit: String,
    precipUnit: String
) {
    val context = LocalContext.current
    var weatherResponse by remember { mutableStateOf<WeatherResponse?>(null) }
    var isLoading by remember { mutableStateOf(true) }
    var isError by remember { mutableStateOf(false) }

    val api = remember { RetrofitClient.getService(context) }

    // Only re-fetch if refreshTrigger, tempUnit, forecastDays or units changes.
    LaunchedEffect(refreshTrigger, tempUnit, forecastDays, windUnit, precipUnit) {
        isLoading = true
        isError = false
        val response = fetchWeatherData(
            latitude, longitude, timezone, tempUnit, api, forecastDays, windUnit, precipUnit
        )
        if (response != null) {
            weatherResponse = response
        } else {
            isError = true
        }
        isLoading = false
    }

    if (isLoading) {
        Column(
            modifier = Modifier.fillMaxWidth().padding(30.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            CircularProgressIndicator()
        }
    } else if (isError) {
        Column(
            modifier = Modifier.fillMaxWidth().padding(30.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text("Failed to load weather data.")
        }
    } else {
        WeatherViewContent(weatherResponse, tempUnit, windUnit, precipUnit)
    }
}

/**
 * Renders the content of the WeatherView based on the weather data.
 */
@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun WeatherViewContent(
    weatherResponse: WeatherResponse?, 
    tempUnit: String,
    windUnit: String,
    precipUnit: String
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        if (weatherResponse != null) {
            val (formattedTime, currentTime) = getCurrentTimeInfo(weatherResponse)
            val userTemp = if (tempUnit == "celsius") "℃" else "℉"
            val isNight = weatherResponse.hourly.isDay[currentTime] == 0

            CurrentTimeInfo(
                formattedTime = formattedTime,
                weatherResponse = weatherResponse,
                currentTime = currentTime,
                userTemp = userTemp,
                isNight = isNight,
                windUnit = windUnit,
                precipUnit = precipUnit
            )
            HorizontalDivider(color = Color.Gray, thickness = 1.dp)
            CurrentDayInfo(
                currentTime = currentTime, 
                weatherResponse = weatherResponse, 
                userTemp = userTemp,
                windUnit = windUnit,
                precipUnit = precipUnit
            )
            HorizontalDivider(color = Color.Gray, thickness = 1.dp)
            WeekInfoScreen(
                weatherResponse = weatherResponse,
                tempUnit = tempUnit,
                userTemp = userTemp,
                windUnit = windUnit,
                precipUnit = precipUnit
            )
        } else {
            Column(modifier = Modifier.padding(30.dp)) { CircularProgressIndicator() }
        }
    }
}
