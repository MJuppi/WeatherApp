package fi.lab.markus.weatherapp.composables

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
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

class WeekInfoManager(private val weatherResponse: WeatherResponse) : ViewModel() {

    var selectedDay by mutableIntStateOf(-1)
    var tempUnit by mutableStateOf("celsius")
    var userTemp by mutableStateOf("celsius")


    fun getDailyData(): List<DailyData> {
        val dailyData = mutableListOf<DailyData>()
        for (i in 0 until 13) {
            val sunrise = weatherResponse.daily.sunrise[i].let {
                if (it.startsWith("1970-01-01")) "--:--" else it.takeLast(5)
            }
            val sunset = weatherResponse.daily.sunset[i].let {
                if (it.startsWith("1970-01-01")) "--:--" else it.takeLast(5)
            }
            val date = weatherResponse.daily.time[i].takeLast(5)
            val formattedDate = if (tempUnit == "celsius") {
                date.let { it.substring(3, 5) + "." + it.substring(0, 2) }
            } else {
                date.replace("-", ".")
            }
            dailyData.add(
                DailyData(
                    formattedDate,
                    weatherResponse.daily.tempMin[i],
                    weatherResponse.daily.tempMax[i],
                    sunrise,
                    sunset,
                    i
                )
            )
        }
        return dailyData
    }

    fun toggleDaySelection(index: Int) {
        selectedDay = if (selectedDay == index) {
            -1 // Deselect if already selected
        } else {
            index // Select if not selected
        }
    }
}

data class DailyData(
    val formattedDate: String,
    val tempMin: Double,
    val tempMax: Double,
    val sunrise: String,
    val sunset: String,
    val index: Int
)

@Composable
fun WeekInfoUI(
    dailyData: List<DailyData>,
    userTemp: String,
    selectedDay: Int,
    onDayClick: (Int) -> Unit,
    onDayInfoScreen: @Composable (Int) -> Unit
) {
    LazyColumn(modifier = Modifier.fillMaxWidth()) {
        items(dailyData.size) { i ->
            val data = dailyData[i]
            val isSelected = selectedDay == data.index
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(15.dp)
                    .clickable {
                        onDayClick(data.index)
                    },
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("${data.formattedDate}.")
                Text("${data.tempMin} $userTemp")
                Text(text = "...")
                Text("${data.tempMax} $userTemp")
                Text(text = data.sunrise, fontSize = 15.sp)
                Image(
                    painter = painterResource(
                        id = if (isSystemInDarkTheme()) {
                            R.drawable.inverse_sunrise
                        } else {
                            R.drawable.sunrise
                        }
                    ), contentDescription = null, modifier = Modifier.height(40.dp)
                )
                Text(text = data.sunset, fontSize = 15.sp)
                Image(
                    painter = painterResource(
                        id = if (isSystemInDarkTheme()) {
                            R.drawable.inverse_sunset
                        } else {
                            R.drawable.sunset
                        }
                    ), contentDescription = null, modifier = Modifier.height(40.dp)
                )
            }
            HorizontalDivider(color = Color.Gray, thickness = 1.dp)
            if (isSelected) {
                onDayInfoScreen(data.index)
                HorizontalDivider(color = Color.Gray, thickness = 1.dp)
            }
        }
    }
}

@Composable
fun WeekInfoScreen(weatherResponse: WeatherResponse, tempUnit: String, userTemp: String) {
    val manager: WeekInfoManager = remember { WeekInfoManager(weatherResponse) }
    manager.userTemp = userTemp
    manager.tempUnit = tempUnit
    val dailyData = manager.getDailyData()
    val selectedDay = manager.selectedDay

    WeekInfoUI(
        dailyData = dailyData,
        userTemp = manager.userTemp,
        selectedDay = selectedDay,
        onDayClick = { index -> manager.toggleDaySelection(index) },
        onDayInfoScreen = { day ->
            DayInfoScreen(day = day, weatherResponse = weatherResponse, userTemp = manager.userTemp)
        }
    )
}