package fi.lab.markus.weatherapp.composables

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.ViewModel
import fi.lab.markus.weatherapp.R
import java.time.LocalDate
import java.time.format.TextStyle
import java.util.Locale

@RequiresApi(Build.VERSION_CODES.O)
class WeekInfoManager(private val weatherResponse: WeatherResponse) : ViewModel() {

    var selectedDay by mutableIntStateOf(-1)
    var tempUnit by mutableStateOf("celsius")
    var userTemp by mutableStateOf("celsius")


    fun getDailyData(): List<DailyData> {
        val daily = weatherResponse.daily
        return daily.time.indices.map { i ->
            val sunrise = daily.sunrise.getOrNull(i)?.let {
                if (it.startsWith("1970-01-01")) "--:--" else it.takeLast(5)
            } ?: "--:--"
            val sunset = daily.sunset.getOrNull(i)?.let {
                if (it.startsWith("1970-01-01")) "--:--" else it.takeLast(5)
            } ?: "--:--"
            
            val rawDate = daily.time[i] // Expected YYYY-MM-DD
            val localDate = try { LocalDate.parse(rawDate) } catch (e: Exception) { null }
            
            val dayOfWeek = localDate?.dayOfWeek?.getDisplayName(TextStyle.SHORT, Locale.getDefault()) ?: ""
            val datePart = rawDate.takeLast(5) // MM-DD
            
            val formattedDate = if (tempUnit == "celsius") {
                datePart.let { it.substring(3, 5) + "." + it.substring(0, 2) }
            } else {
                datePart.replace("-", ".")
            }
            
            DailyData(
                dayOfWeek = dayOfWeek,
                formattedDate = formattedDate,
                tempMin = daily.tempMin[i],
                tempMax = daily.tempMax[i],
                precipSum = daily.precipSum[i],
                sunrise = sunrise,
                sunset = sunset,
                index = i
            )
        }
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
    val dayOfWeek: String,
    val formattedDate: String,
    val tempMin: Double,
    val tempMax: Double,
    val precipSum: Double,
    val sunrise: String,
    val sunset: String,
    val index: Int
)

@Composable
fun WeekInfoUI(
    dailyData: List<DailyData>,
    userTemp: String,
    windUnit: String,
    precipUnit: String,
    selectedDay: Int,
    onDayClick: (Int) -> Unit,
    onDayInfoScreen: @Composable (Int) -> Unit
) {
    val isDark = isSystemInDarkTheme()
    val sunriseIcon = if (isDark) R.drawable.inverse_sunrise else R.drawable.sunrise
    val sunsetIcon = if (isDark) R.drawable.inverse_sunset else R.drawable.sunset
    val waterIcon = if (isDark) R.drawable.inverse_water else R.drawable.water

    Column(modifier = Modifier.fillMaxWidth().padding(horizontal = 8.dp)) {
        dailyData.forEach { data ->
            val isSelected = selectedDay == data.index
            val backgroundColor = if (isSelected) {
                MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f)
            } else {
                Color.Transparent
            }

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp)
                    .background(backgroundColor, shape = RoundedCornerShape(8.dp))
                    .clickable { onDayClick(data.index) }
                    .padding(8.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Date & Weekday
                    Column(modifier = Modifier.weight(1.5f)) {
                        Text(
                            text = data.dayOfWeek,
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp
                        )
                        Text(
                            text = data.formattedDate,
                            fontSize = 11.sp,
                            color = Color.Gray
                        )
                    }

                    // Temperature Range
                    Row(
                        modifier = Modifier.weight(3f),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "${data.tempMin.toInt()} $userTemp",
                            color = Color(0xFF42A5F5), // Blue
                            fontWeight = FontWeight.Medium,
                            fontSize = 14.sp
                        )
                        Text(
                            text = " / ",
                            color = Color.Gray.copy(alpha = 0.5f),
                            fontSize = 12.sp
                        )
                        Text(
                            text = "${data.tempMax.toInt()} $userTemp",
                            color = Color(0xFFFF7043), // Orange/Red
                            fontWeight = FontWeight.Medium,
                            fontSize = 14.sp
                        )
                    }

                    // Precipitation
                    Row(
                        modifier = Modifier.weight(1.5f),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.End
                    ) {
                        Image(
                            painter = painterResource(id = waterIcon),
                            contentDescription = null,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(2.dp))
                        Text(
                            text = "${data.precipSum} $precipUnit",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Light
                        )
                    }
                    
                    // Sunrise/Sunset & Chevron
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.End,
                        modifier = Modifier.weight(2.5f)
                    ) {
                        Image(
                            painter = painterResource(id = sunriseIcon),
                            contentDescription = null,
                            modifier = Modifier.height(24.dp)
                        )
                        Text(text = data.sunrise, fontSize = 10.sp)
                        Spacer(modifier = Modifier.width(4.dp))
                        Image(
                            painter = painterResource(id = sunsetIcon),
                            contentDescription = null,
                            modifier = Modifier.height(24.dp)
                        )
                        Text(text = data.sunset, fontSize = 10.sp)
                        
                        Icon(
                            imageVector = if (isSelected) Icons.Default.KeyboardArrowDown else Icons.Default.KeyboardArrowRight,
                            contentDescription = null,
                            tint = Color.Gray.copy(alpha = 0.6f),
                            modifier = Modifier.padding(start = 4.dp).size(20.dp)
                        )
                    }
                }
                
                if (isSelected) {
                    HorizontalDivider(
                        modifier = Modifier.padding(vertical = 8.dp),
                        color = MaterialTheme.colorScheme.outlineVariant,
                        thickness = 1.dp
                    )
                    onDayInfoScreen(data.index)
                }
            }
            if (!isSelected) {
                HorizontalDivider(color = Color.Gray.copy(alpha = 0.1f), thickness = 0.5.dp)
            }
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun WeekInfoScreen(
    weatherResponse: WeatherResponse,
    tempUnit: String,
    userTemp: String,
    windUnit: String,
    precipUnit: String
) {
    val manager: WeekInfoManager = remember(weatherResponse) { WeekInfoManager(weatherResponse) }

    // Update manager state only if values actually change
    if (manager.userTemp != userTemp) manager.userTemp = userTemp
    if (manager.tempUnit != tempUnit) manager.tempUnit = tempUnit

    val dailyData = remember(weatherResponse, tempUnit) {
        manager.getDailyData()
    }
    val selectedDay = manager.selectedDay

    WeekInfoUI(
        dailyData = dailyData,
        userTemp = manager.userTemp,
        windUnit = windUnit,
        precipUnit = precipUnit,
        selectedDay = selectedDay,
        onDayClick = { index -> manager.toggleDaySelection(index) },
        onDayInfoScreen = { day ->
            DayInfoScreen(
                day = day,
                weatherResponse = weatherResponse,
                userTemp = manager.userTemp,
                windUnit = windUnit,
                precipUnit = precipUnit
            )
        }
    )
}
