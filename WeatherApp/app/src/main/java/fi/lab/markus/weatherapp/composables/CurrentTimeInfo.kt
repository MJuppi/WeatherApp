package fi.lab.markus.weatherapp.composables

import androidx.compose.foundation.Image
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import fi.lab.markus.weatherapp.R
import java.util.Locale

/**
 * Displays the current time information along with sunrise and sunset times, temperature, and
 * weather code.
 *
 * @param formattedTime The formatted time to display.
 * @param weatherResponse The weather response object containing the necessary data.
 * @param currentTime The current time index.
 * @param userTemp The user's preferred temperature unit.
 * @param isNight A boolean indicating whether it is nighttime.
 */
@Composable
fun CurrentTimeInfo(
    formattedTime: String,
    weatherResponse: WeatherResponse,
    currentTime: Int,
    userTemp: String,
    isNight: Boolean,
    windUnit: String,
    precipUnit: String
) {
    // Extract data from weatherResponse
    val sunrise = weatherResponse.daily.sunrise[0].let {
        if (it.startsWith("1970-01-01")) "--:--" else it.takeLast(5)
    }
    val sunset = weatherResponse.daily.sunset[0].let {
        if (it.startsWith("1970-01-01")) "--:--" else it.takeLast(5)
    }
    
    val currentTemp = weatherResponse.hourly.temp[currentTime]
    val apparentTemp = weatherResponse.hourly.apparentTemp[currentTime]
    val currentWind = weatherResponse.hourly.windSpeed[currentTime]
    val currentHumidity = weatherResponse.hourly.humidity[currentTime]
    val currentPrecipSum = weatherResponse.daily.precipSum[0]
    val uvIndex = weatherResponse.daily.uvIndexMax[0]
    val weatherCode = weatherResponse.hourly.weatherCode[currentTime]

    val windLabel = when(windUnit) {
        "kmh" -> "km/h"
        "ms" -> "m/s"
        else -> windUnit
    }

    val isDark = isSystemInDarkTheme()
    val sunriseIcon = if (isDark) R.drawable.inverse_sunrise else R.drawable.sunrise
    val sunsetIcon = if (isDark) R.drawable.inverse_sunset else R.drawable.sunset
    val windIcon = if (isDark) R.drawable.inverse_wind else R.drawable.wind
    val waterIcon = if (isDark) R.drawable.inverse_water else R.drawable.water
    val airIcon = if (isDark) R.drawable.inverse_air else R.drawable.air

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Top Section: GPS Coordinates & Condition Icon
        Text(
            text = String.format(Locale.ROOT, "GPS: %.4f, %.4f", weatherResponse.latitude, weatherResponse.longitude),
            fontSize = 14.sp,
            color = Color.Gray,
            fontWeight = FontWeight.Medium
        )
        
        Spacer(modifier = Modifier.height(8.dp))
        
        WeatherCode(weatherCode, 120F, isNight)
        
        Spacer(modifier = Modifier.height(8.dp))

        // Main Temperature
        Text(
            text = "$currentTemp $userTemp",
            fontSize = 56.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary
        )
        
        Text(
            text = "Feels like $apparentTemp $userTemp",
            fontSize = 16.sp,
            color = Color.Gray
        )

        Spacer(modifier = Modifier.height(24.dp))

        // Details Grid
        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                WeatherDetailItem(icon = windIcon, label = "Wind", value = "$currentWind $windLabel")
                WeatherDetailItem(icon = airIcon, label = "Humidity", value = "$currentHumidity%")
                WeatherDetailItem(icon = R.drawable.ic_launcher_foreground, label = "UV Index", value = "$uvIndex", isUv = true)
            }
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                WeatherDetailItem(icon = waterIcon, label = "Precipitation", value = "$currentPrecipSum $precipUnit")
                WeatherDetailItem(icon = sunriseIcon, label = "Sunrise", value = sunrise)
                WeatherDetailItem(icon = sunsetIcon, label = "Sunset", value = sunset)
            }
        }
    }
}

@Composable
fun WeatherDetailItem(icon: Int, label: String, value: String, isUv: Boolean = false) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.width(100.dp)
    ) {
        if (isUv) {
            // UV doesn't have a specific icon in resources, maybe use text or a generic one
            Box(modifier = Modifier.size(32.dp), contentAlignment = Alignment.Center) {
                Text("UV", fontWeight = FontWeight.Bold, fontSize = 12.sp)
            }
        } else {
            Image(
                painter = painterResource(id = icon),
                contentDescription = null,
                modifier = Modifier.size(32.dp)
            )
        }
        Text(text = label, fontSize = 12.sp, color = Color.Gray)
        Text(text = value, fontSize = 14.sp, fontWeight = FontWeight.Medium)
    }
}
