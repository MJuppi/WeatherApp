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
import okhttp3.Cache
import okhttp3.OkHttpClient
import java.time.ZoneId
import java.time.ZonedDateTime
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.io.File

/**
 * Fetches weather data from the Open-Meteo API.
 *
 * @param latitude The latitude coordinate of the location.
 * @param longitude The longitude coordinate of the location.
 * @param timezone The timezone of the location.
 * @param tempUnit The unit of temperature to use.
 * @param api The Retrofit service for making API calls.
 * @return The weather data response, or null if an error occurred.
 */
@RequiresApi(Build.VERSION_CODES.O)
suspend fun fetchWeatherData(
    latitude: Float,
    longitude: Float,
    timezone: String,
    tempUnit: String,
    api: WeatherService
): WeatherResponse? {
    return try {
        api.getWeather(latitude, longitude, timezone = timezone, temperatureUnit = tempUnit)
    } catch (e: Exception) {
        // Handle network or API errors here, e.g., log the error
        println("Error fetching weather data: ${e.message}")
        null
    }
}

/**
 * Calculates and formats the current time based on the weather data.
 *
 * @param weatherResponse The weather data response.
 * @return A pair containing the formatted time string and the current hour.
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

fun createOkHttpClient(cacheDir: File): OkHttpClient {
    val cacheSize = 10 * 1024 * 1024 // 10 MiB
    val cache = Cache(cacheDir, cacheSize.toLong())

    return OkHttpClient.Builder()
        .cache(cache)
        .build()
}

/**
 * Renders the weather information UI.
 *
 * This function is a Composable that displays the weather information UI. It takes the latitude,
 * longitude, timezone, and temperature unit as parameters. It uses the Retrofit library to make a
 * network request to the Open-Meteo API to retrieve weather data. The weather data is stored in the
 * [WeatherResponse] variable, which is updated using the [LaunchedEffect] function. The UI is
 * composed using the Column composable, which displays the current time, current day information,
 * and weekly weather information. If the weather data is not available, it displays a loading
 * indicator.
 *
 * @param latitude The latitude coordinate of the location.
 * @param longitude The longitude coordinate of the location.
 * @param timezone The timezone of the location.
 * @param tempUnit The unit of temperature to use.
 */
@SuppressLint("DefaultLocale")
@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun WeatherView(latitude: Float, longitude: Float, timezone: String, tempUnit: String) {
    val context = LocalContext.current
    val cacheDir = context.cacheDir
    val client = remember { createOkHttpClient(cacheDir) }

    val api = remember {
        Retrofit.Builder()
            .baseUrl("https://api.open-meteo.com/v1/")
            .client(client)
            .addConverterFactory(GsonConverterFactory.create()).build()
            .create(WeatherService::class.java)
    }
    var weatherResponse by remember { mutableStateOf<WeatherResponse?>(null) }

    // Wait for the API response
    LaunchedEffect(key1 = api) {
        weatherResponse = fetchWeatherData(latitude, longitude, timezone, tempUnit, api)
    }

    WeatherViewContent(weatherResponse, tempUnit)
}

/**
 * Renders the content of the WeatherView based on the weather data.
 *
 * @param weatherResponse The weather data response.
 * @param tempUnit The unit of temperature to use.
 */
@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun WeatherViewContent(weatherResponse: WeatherResponse?, tempUnit: String) {
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
                isNight = isNight
            )
            HorizontalDivider(color = Color.Gray, thickness = 1.dp)
            CurrentDayInfo(
                currentTime = currentTime, weatherResponse = weatherResponse, userTemp = userTemp
            )
            HorizontalDivider(color = Color.Gray, thickness = 1.dp)
            WeekInfoScreen(weatherResponse = weatherResponse, tempUnit = tempUnit, userTemp = userTemp)
        } else {
            Column(modifier = Modifier.padding(30.dp)) { CircularProgressIndicator() }
        }
    }
}