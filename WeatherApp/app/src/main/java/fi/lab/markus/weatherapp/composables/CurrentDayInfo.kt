package fi.lab.markus.weatherapp.composables

import android.annotation.SuppressLint
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import fi.lab.markus.weatherapp.R

/**
 * Renders the current day's weather information in a LazyRow.
 *
 * @param currentTime The current time in hours.
 * @param weatherResponse The weather response object containing the hourly weather data.
 * @param userTemp The user's preferred temperature unit.
 * @param windUnit The user's preferred wind speed unit.
 * @param precipUnit The user's preferred precipitation unit.
 */
@SuppressLint("DefaultLocale")
@Composable
fun CurrentDayInfo(
    currentTime: Int,
    weatherResponse: WeatherResponse,
    userTemp: String,
    windUnit: String,
    precipUnit: String
) {
    val windLabel = when (windUnit) {
        "kmh" -> "km/h"
        "ms" -> "m/s"
        else -> windUnit
    }

    // Creates a LazyRow that takes up the full width of its parent
    LazyRow(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Iterates over the hours from currentTime + 1 to currentTime + 13
        for (i in currentTime + 1 until currentTime + 13) {
            // Calculates the index of the hour in the 24-hour cycle
            val index = i % 24
            // Formats the hour as a string with leading zeros
            val hour = String.format("%02d:00", index)
            // Retrieves the temperature for the current hour from the weatherResponse object
            val hourTemp = weatherResponse.hourly.temp[i]
            // Checks if it's nighttime for the current hour
            val isNight = weatherResponse.hourly.isDay[i] == 0
            
            val hourWind = weatherResponse.hourly.windSpeed[i]
            val hourPrecip = weatherResponse.hourly.precipitation[i]

            // Creates a Column with padding and centers its content horizontally and vertically
            item {
                Column(
                    modifier = Modifier.padding(10.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.SpaceBetween
                ) {
                    // Displays the formatted hour
                    Text(text = hour, fontSize = 14.sp, color = Color.Gray)
                    // Displays the temperature and userTemp string
                    Text(text = "$hourTemp $userTemp", fontSize = 18.sp)
                    // Displays a weather code icon based on the weather code for the current hour
                    // and night status
                    WeatherCode(weatherResponse.hourly.weatherCode[i], 60F, isNight)
                    
                    // Wind info
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Image(
                            painter = painterResource(id = R.drawable.wind),
                            contentDescription = null,
                            modifier = Modifier.size(12.dp)
                        )
                        Spacer(modifier = Modifier.width(2.dp))
                        Text(text = "$hourWind $windLabel", fontSize = 10.sp)
                    }
                    
                    // Precip info
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Image(
                            painter = painterResource(id = R.drawable.water),
                            contentDescription = null,
                            modifier = Modifier.size(12.dp)
                        )
                        Spacer(modifier = Modifier.width(2.dp))
                        Text(text = "$hourPrecip $precipUnit", fontSize = 10.sp)
                    }
                }
            }
        }
    }
}
