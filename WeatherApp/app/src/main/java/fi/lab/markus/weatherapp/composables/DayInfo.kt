package fi.lab.markus.weatherapp.composables

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.ViewModel

class DayInfoManager(private val weatherResponse: WeatherResponse) : ViewModel() {

    private var weekDay by mutableStateOf<Int?>(null)
    var userTemp by mutableStateOf("celsius")

    fun setDay(newDay: Int?) {
        weekDay = newDay
    }

    @SuppressLint("DefaultLocale")
    fun getHourlyData(): List<HourlyData> {
        val hourlyData = mutableListOf<HourlyData>()
        if (weekDay != null) {
            for (i in weekDay!! * 24 until weekDay!! * 24 + 24) {
                val index = i % 24
                val hour = String.format("%02d:00", index)
                val hourTemp = weatherResponse.hourly.temp[i]
                val hourWeatherCode = weatherResponse.hourly.weatherCode[i]
                val isNight = weatherResponse.hourly.isDay[i] == 0
                hourlyData.add(HourlyData(hour, hourTemp, hourWeatherCode, isNight))
            }
        }
        return hourlyData
    }
}

data class HourlyData(
    val hour: String,
    val hourTemp: Double,
    val hourWeatherCode: Int,
    val isNight: Boolean
)

@SuppressLint("DefaultLocale")
@Composable
fun DayInfoUI(
    hourlyData: List<HourlyData>,
    userTemp: String
) {
    LazyRow(modifier = Modifier.fillMaxWidth()) {
        items(hourlyData.size) { i ->
            val data = hourlyData[i]
            Column(
                modifier = Modifier.padding(Dp(10F)),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text(data.hour)
                Text("${data.hourTemp} $userTemp", fontSize = 20.sp)
                WeatherCode(code = data.hourWeatherCode, height = 50F, data.isNight)
            }
        }
    }
}

@Composable
fun DayInfoScreen(
    day: Int?,
    weatherResponse: WeatherResponse,
    userTemp: String
) {
    val manager: DayInfoManager = remember { DayInfoManager(weatherResponse) }
    manager.setDay(day)
    manager.userTemp = userTemp
    val hourlyData = manager.getHourlyData()

    DayInfoUI(hourlyData, manager.userTemp)
}
