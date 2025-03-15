package fi.lab.markus.weatherapp.composables

import androidx.compose.foundation.Image
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import fi.lab.markus.weatherapp.R

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
    isNight: Boolean
) {
    // Extract sunrise and sunset times from weatherResponse
    val sunrise = weatherResponse.daily.sunrise[0].let {
        if (it.startsWith("1970-01-01")) "--:--" else it.takeLast(5)
    }
    val sunset = weatherResponse.daily.sunset[0].let {
        if (it.startsWith("1970-01-01")) "--:--" else it.takeLast(5)
    }

    // Display the current time, sunrise time, temperature, and sunset time
    Row {
        Column(
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(text = sunrise, fontSize = 15.sp)
            Image(
                painter = painterResource(
                    id = if (isSystemInDarkTheme()) R.drawable.inverse_sunrise
                    else R.drawable.sunrise
                ), contentDescription = null, modifier = Modifier.height(75.dp)
            )
        }
        Column(
            modifier = Modifier.padding(10.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(text = formattedTime, fontSize = 20.sp)
            Text(text = "${weatherResponse.hourly.temp[currentTime]} $userTemp", fontSize = 40.sp)
            WeatherCode(weatherResponse.hourly.weatherCode[currentTime], 100F, isNight)
        }
        Column(
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(text = sunset, fontSize = 15.sp)
            Image(
                painter = painterResource(
                    id = if (isSystemInDarkTheme()) R.drawable.inverse_sunset
                    else R.drawable.sunset
                ), contentDescription = null, modifier = Modifier.height(75.dp)
            )
        }
    }
}
