package fi.lab.markus.weatherapp.composables

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import java.time.ZoneId
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

/**
 * Renders the location view UI.
 *
 * This function is a Composable that displays the location view UI. It takes the city name and the
 * temperature unit as parameters. It uses the Retrofit library to make an API call to retrieve the
 * location information based on the city name. The location information includes the city, country,
 * and timezone. The UI is composed using the Column composable, which displays the city, country,
 * and local time. If the location information is not found, it displays a message indicating that
 * the location was not found. While waiting for the API response, it displays a
 * CircularProgressIndicator.
 *
 * @param cityName The name of the city to retrieve the location information for.
 * @param tempUnit The unit of temperature to display in the weather view.
 * @return The rendered location view UI.
 */
@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun LocationView(cityName: String, tempUnit: String) {
    val api = remember {
        Retrofit.Builder().baseUrl("https://geocoding-api.open-meteo.com/v1/")
            .addConverterFactory(GsonConverterFactory.create()).build()
            .create(LocationService::class.java)
    }
    var locationResponse by remember { mutableStateOf<LocationResponse?>(null) }
    // Wait for the API response
    LaunchedEffect(key1 = api) { locationResponse = api.getLocation(cityName) }
    // Display the location information
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        if (locationResponse != null) {
            val city = locationResponse?.results?.get(0)?.name
            val country = locationResponse?.results?.get(0)?.country
            val timezone = locationResponse?.results?.get(0)?.timezone ?: "GMT"
            val zoneId = ZoneId.of(timezone)
            val zonedDateTime = ZonedDateTime.now(zoneId)
            val formatter = DateTimeFormatter.ofPattern("HH:mm")
            val formattedTime = zonedDateTime.format(formatter)
            if (city != null || country != null) {
                Text(
                    text = "$city, $country\nUsing local time: $formattedTime",
                    modifier = Modifier.padding(10.dp),
                    textAlign = TextAlign.Center
                )
                locationResponse?.results?.get(0)?.latitude?.let {
                    locationResponse?.results?.get(0)?.longitude?.let { it1 ->
                        WeatherView(latitude = it, longitude = it1, timezone, tempUnit)
                    }
                }
            } else {
                Column(modifier = Modifier.padding(30.dp)) { Text(text = "Location not found") }
            }
        } else {
            Column(modifier = Modifier.padding(30.dp)) { CircularProgressIndicator() }
        }
    }
}
