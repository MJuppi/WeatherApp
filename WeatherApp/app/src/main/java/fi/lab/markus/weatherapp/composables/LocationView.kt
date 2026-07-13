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
 * @param refreshTrigger A trigger to refresh the weather data.
 * @param forecastDays The number of days to forecast.
 * @param windUnit The unit of wind speed to display.
 * @param precipUnit The unit of precipitation to display.
 * @return The rendered location view UI.
 */
@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun LocationView(
    cityName: String,
    tempUnit: String,
    refreshTrigger: Int,
    forecastDays: Int,
    windUnit: String,
    precipUnit: String
) {
    val api = remember {
        Retrofit.Builder().baseUrl("https://geocoding-api.open-meteo.com/v1/")
            .addConverterFactory(GsonConverterFactory.create()).build()
            .create(LocationService::class.java)
    }
    var locationResponse by remember(cityName) { mutableStateOf<LocationResponse?>(null) }
    
    // Wait for the API response
    LaunchedEffect(cityName, refreshTrigger) { 
        locationResponse = api.getLocation(cityName) 
    }
    
    // Display the location information
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        val result = locationResponse?.results?.getOrNull(0)
        if (result != null) {
            val city = result.name
            val country = result.country
            val timezone = result.timezone ?: "GMT"
            val zoneId = ZoneId.of(timezone)
            val zonedDateTime = ZonedDateTime.now(zoneId)
            val formatter = DateTimeFormatter.ofPattern("HH:mm")
            val formattedTime = zonedDateTime.format(formatter)
            
            Text(
                text = "$city, $country\nUsing local time: $formattedTime",
                modifier = Modifier.padding(10.dp),
                textAlign = TextAlign.Center
            )
            
            WeatherView(
                latitude = result.latitude, 
                longitude = result.longitude, 
                timezone = timezone, 
                tempUnit = tempUnit,
                refreshTrigger = refreshTrigger,
                forecastDays = forecastDays,
                windUnit = windUnit,
                precipUnit = precipUnit
            )
        } else if (locationResponse != null) {
            Column(modifier = Modifier.padding(30.dp)) { 
                Text(text = "Location not found", textAlign = TextAlign.Center)
            }
        } else {
            Column(modifier = Modifier.padding(30.dp)) { CircularProgressIndicator() }
        }
    }
}
