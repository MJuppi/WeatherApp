package fi.lab.markus.weatherapp.composables

import androidx.compose.foundation.Image
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import fi.lab.markus.weatherapp.R

/**
 * Represents the state of the weather icon to be displayed.
 *
 * @property iconResId The resource ID of the drawable icon.
 */
data class WeatherIconState(val iconResId: Int)

/**
 * Determines the appropriate weather icon based on the given weather code and night status.
 *
 * @param code The weather code representing the type of weather.
 * @param isNight Whether it is night or day.
 * @return A WeatherIconState containing the resource ID of the appropriate weather icon.
 */
fun getWeatherIconState(code: Int, isDarkTheme: Boolean, isNight: Boolean): WeatherIconState {
    val iconResId = when (code) {
        // Clear sky
        0 -> if (isNight) {
            if (isDarkTheme) R.drawable.ic_inverse_moon else R.drawable.ic_moon
        } else {
            if (isDarkTheme) R.drawable.ic_inverse_sun else R.drawable.ic_sun
        }
        // Mainly clear, Partly cloudy
        1, 2 -> if (isNight) {
            if (isDarkTheme) R.drawable.ic_inverse_cloud_moon else R.drawable.ic_cloud_moon
        } else {
            if (isDarkTheme) R.drawable.ic_inverse_cloud_sun else R.drawable.ic_cloud_sun
        }
        // Overcast, Cloudy
        3 -> if (isDarkTheme) R.drawable.ic_inverse_cloud else R.drawable.ic_cloud
        // Fog/Depositing rime fog
        45, 48 -> if (isDarkTheme) R.drawable.ic_inverse_fog else R.drawable.ic_fog
        // Drizzle
        51, 53, 55, 56, 57 -> if (isDarkTheme) R.drawable.ic_inverse_drizzle else R.drawable.ic_drizzle
        // Rain
        61, 63, 65, 66, 67, 80, 81, 82 -> if (isDarkTheme) R.drawable.ic_inverse_rain else R.drawable.ic_rain
        // Snow
        71, 73, 75, 77, 85, 86 -> if (isDarkTheme) R.drawable.ic_inverse_snow else R.drawable.ic_snow
        // Thunderstorm
        95, 96, 99 -> if (isDarkTheme) R.drawable.ic_inverse_thunder else R.drawable.ic_thunder
        else -> if (isDarkTheme) R.drawable.ic_inverse_wind else R.drawable.ic_wind
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
        modifier = Modifier.height(Dp(height)).width(Dp(height))
    )
}
