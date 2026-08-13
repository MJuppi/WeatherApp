package fi.lab.markus.weatherapp.composables

import androidx.compose.foundation.Image
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.Dp
import fi.lab.markus.weatherapp.R

/**
 * Represents the state of the weather icon to be displayed.
 *
 * @property iconResId The resource ID of the drawable icon.
 */
data class WeatherIconState(val iconResId: Int)

/**
 * Determines the appropriate weather icon based on the given weather code, dark theme status, and night status.
 *
 * @param code The weather code representing the type of weather.
 * @param isDarkTheme Whether the system is in dark theme.
 * @param isNight Whether it is night or day.
 * @return A WeatherIconState containing the resource ID of the appropriate weather icon.
 */
fun getWeatherIconState(code: Int, isDarkTheme: Boolean, isNight: Boolean): WeatherIconState {
    val iconResId = when (code) {
        // Clear sky
        0 -> when {
            isDarkTheme && isNight -> R.drawable.inverse_night_clear_sky
            isDarkTheme -> R.drawable.inverse_day_clear_sky
            isNight -> R.drawable.night_clear_sky
            else -> R.drawable.day_clear_sky
        }
        // Mainly clear
        1 -> when {
            isDarkTheme && isNight -> R.drawable.inverse_night_few_clouds
            isDarkTheme -> R.drawable.inverse_day_few_clouds
            isNight -> R.drawable.night_few_clouds
            else -> R.drawable.day_few_clouds
        }
        // Partly cloudy
        2 -> when {
            isDarkTheme && isNight -> R.drawable.inverse_night_scattered_clouds
            isDarkTheme -> R.drawable.inverse_day_scattered_clouds
            isNight -> R.drawable.night_scattered_clouds
            else -> R.drawable.day_scattered_clouds
        }
        // Overcast
        3 -> when {
            isDarkTheme && isNight -> R.drawable.inverse_night_broken_clouds
            isDarkTheme -> R.drawable.inverse_day_broken_clouds
            isNight -> R.drawable.night_broken_clouds
            else -> R.drawable.day_broken_clouds
        }
        // Fog/Depositing rime fog
        45, 48 -> when {
            isDarkTheme && isNight -> R.drawable.inverse_night_mist
            isDarkTheme -> R.drawable.inverse_day_mist
            isNight -> R.drawable.night_mist
            else -> R.drawable.day_mist
        }
        // Drizzle: Light intensity
        51, 56 -> when {
            isDarkTheme && isNight -> R.drawable.inverse_night_drizzle_light
            isDarkTheme -> R.drawable.inverse_day_drizzle_light
            isNight -> R.drawable.night_drizzle_light
            else -> R.drawable.day_drizzle_light
        }
        // Drizzle: Moderate intensity
        53, 57 -> when {
            isDarkTheme && isNight -> R.drawable.inverse_night_drizzle_moderate
            isDarkTheme -> R.drawable.inverse_day_drizzle_moderate
            isNight -> R.drawable.night_drizzle_moderate
            else -> R.drawable.day_drizzle_moderate
        }
        // Drizzle: Dense intensity
        55 -> when {
            isDarkTheme && isNight -> R.drawable.inverse_night_drizzle_heavy
            isDarkTheme -> R.drawable.inverse_day_drizzle_heavy
            isNight -> R.drawable.night_drizzle_heavy
            else -> R.drawable.day_drizzle_heavy
        }
        // Rain: Slight intensity
        61, 66, 80 -> when {
            isDarkTheme && isNight -> R.drawable.inverse_night_rain_light
            isDarkTheme -> R.drawable.inverse_day_rain_light
            isNight -> R.drawable.night_rain_light
            else -> R.drawable.day_rain_light
        }
        // Rain: Moderate intensity
        63, 81 -> when {
            isDarkTheme && isNight -> R.drawable.inverse_night_rain_moderate
            isDarkTheme -> R.drawable.inverse_day_rain_moderate
            isNight -> R.drawable.night_rain_moderate
            else -> R.drawable.day_rain_moderate
        }
        // Rain: Heavy/Violent intensity
        65, 67, 82 -> when {
            isDarkTheme && isNight -> R.drawable.inverse_night_rain_heavy
            isDarkTheme -> R.drawable.inverse_day_rain_heavy
            isNight -> R.drawable.night_rain_heavy
            else -> R.drawable.day_rain_heavy
        }
        // Snow fall: Slight intensity
        71, 77, 85 -> when {
            isDarkTheme && isNight -> R.drawable.inverse_night_snow_light
            isDarkTheme -> R.drawable.inverse_day_snow_light
            isNight -> R.drawable.night_snow_light
            else -> R.drawable.day_snow_light
        }
        // Snow fall: Moderate intensity
        73 -> when {
            isDarkTheme && isNight -> R.drawable.inverse_night_snow_moderate
            isDarkTheme -> R.drawable.inverse_day_snow_moderate
            isNight -> R.drawable.night_snow_moderate
            else -> R.drawable.day_snow_moderate
        }
        // Snow fall: Heavy intensity
        75, 86 -> when {
            isDarkTheme && isNight -> R.drawable.inverse_night_snow_heavy
            isDarkTheme -> R.drawable.inverse_day_snow_heavy
            isNight -> R.drawable.night_snow_heavy
            else -> R.drawable.day_snow_heavy
        }
        // Thunderstorm
        95, 96, 99 -> when {
            isDarkTheme && isNight -> R.drawable.inverse_night_thunderstorm
            isDarkTheme -> R.drawable.inverse_day_thunderstorm
            isNight -> R.drawable.night_thunderstorm
            else -> R.drawable.day_thunderstorm
        }
        else -> if (isDarkTheme) R.drawable.inverse_wind else R.drawable.wind
    }
    return WeatherIconState(iconResId)
}

/**
 * Renders a weather icon based on the given code, height, and night status.
 *
 * @param code The weather code representing the type of weather.
 * @param height The height of the icon in pixels.
 * @param isNight Whether it is night or day.
 */
@Composable
fun WeatherCode(code: Int, height: Float, isNight: Boolean) {
    val isDarkTheme = isSystemInDarkTheme()
    val iconState = getWeatherIconState(code, isDarkTheme, isNight)
    WeatherIcon(iconState, height)
}

/**
 * Composable function to display a weather icon.
 *
 * @param iconState The state of the weather icon to display.
 * @param height The height of the icon in pixels.
 */
@Composable
fun WeatherIcon(iconState: WeatherIconState, height: Float) {
    Image(
        painter = painterResource(id = iconState.iconResId),
        contentDescription = null,
        modifier = Modifier.height(Dp(height))
    )
}