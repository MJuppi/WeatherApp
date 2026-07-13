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
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.ViewModel
import fi.lab.markus.weatherapp.R

class DayInfoManager(private val weatherResponse: WeatherResponse) : ViewModel() {

    private var weekDay by mutableStateOf<Int?>(null)
    var userTemp by mutableStateOf("celsius")

    fun setDay(newDay: Int?) {
        weekDay = newDay
    }

    @SuppressLint("DefaultLocale")
    fun getHourlyData(): List<HourlyData> {
        val day = weekDay ?: return emptyList()
        val startIndex = day * 24
        val endIndex = startIndex + 24
        
        // Ensure we don't go out of bounds
        val maxIndex = weatherResponse.hourly.temp.size
        val safeEndIndex = if (endIndex > maxIndex) maxIndex else endIndex
        
        if (startIndex >= maxIndex) return emptyList()

        return (startIndex until safeEndIndex).map { i ->
            val hourIndex = i % 24
            val hour = String.format("%02d:00", hourIndex)
            val hourTemp = weatherResponse.hourly.temp[i]
            val hourWeatherCode = weatherResponse.hourly.weatherCode[i]
            val isNight = weatherResponse.hourly.isDay[i] == 0
            val windSpeed = weatherResponse.hourly.windSpeed[i]
            val precipitation = weatherResponse.hourly.precipitation[i]
            val humidity = weatherResponse.hourly.humidity[i]
            val apparentTemp = weatherResponse.hourly.apparentTemp[i]
            
            HourlyData(
                hour, 
                hourTemp, 
                hourWeatherCode, 
                isNight, 
                windSpeed, 
                precipitation, 
                humidity, 
                apparentTemp
            )
        }
    }
}

data class HourlyData(
    val hour: String,
    val hourTemp: Double,
    val hourWeatherCode: Int,
    val isNight: Boolean,
    val windSpeed: Double,
    val precipitation: Double,
    val humidity: Int,
    val apparentTemp: Double
)

@Composable
fun DayInfoUI(
    hourlyData: List<HourlyData>,
    userTemp: String,
    windUnit: String,
    precipUnit: String
) {
    val windLabel = when(windUnit) {
        "kmh" -> "km/h"
        "ms" -> "m/s"
        else -> windUnit
    }

    LazyRow(modifier = Modifier.fillMaxWidth()) {
        items(hourlyData, key = { it.hour }) { data ->
            Column(
                modifier = Modifier.padding(10.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text(data.hour, fontSize = 14.sp, color = Color.Gray)
                Text("${data.hourTemp} $userTemp", fontSize = 18.sp)
                WeatherCode(code = data.hourWeatherCode, height = 50F, data.isNight)
                
                // Wind
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Image(
                        painter = painterResource(id = R.drawable.wind),
                        contentDescription = null,
                        modifier = Modifier.size(12.dp)
                    )
                    Spacer(modifier = Modifier.width(2.dp))
                    Text("${data.windSpeed} $windLabel", fontSize = 10.sp)
                }
                
                // Precipitation
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Image(
                        painter = painterResource(id = R.drawable.water),
                        contentDescription = null,
                        modifier = Modifier.size(12.dp)
                    )
                    Spacer(modifier = Modifier.width(2.dp))
                    Text("${data.precipitation} $precipUnit", fontSize = 10.sp)
                }

                // Humidity
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Image(
                        painter = painterResource(id = R.drawable.air),
                        contentDescription = null,
                        modifier = Modifier.size(12.dp)
                    )
                    Spacer(modifier = Modifier.width(2.dp))
                    Text("${data.humidity}%", fontSize = 10.sp)
                }
            }
        }
    }
}

@Composable
fun DayInfoScreen(
    day: Int?,
    weatherResponse: WeatherResponse,
    userTemp: String,
    windUnit: String,
    precipUnit: String
) {
    val manager: DayInfoManager = remember(weatherResponse) { DayInfoManager(weatherResponse) }
    
    // Update manager state
    if (manager.userTemp != userTemp) manager.userTemp = userTemp
    manager.setDay(day)
    
    val hourlyData = remember(weatherResponse, day) {
        manager.getHourlyData()
    }

    DayInfoUI(hourlyData, manager.userTemp, windUnit, precipUnit)
}
