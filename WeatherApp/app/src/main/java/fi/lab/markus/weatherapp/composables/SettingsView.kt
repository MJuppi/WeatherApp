package fi.lab.markus.weatherapp.composables

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Slider
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import fi.lab.markus.weatherapp.R
import kotlin.math.roundToInt

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    onBack: () -> Unit,
    tempUnit: String,
    onTempUnitToggle: () -> Unit,
    isDarkTheme: Boolean,
    onThemeToggle: () -> Unit,
    forecastDays: Int,
    onForecastDaysChange: (Int) -> Unit,
    windUnit: String,
    onWindUnitChange: (String) -> Unit,
    precipUnit: String,
    onPrecipUnitChange: (String) -> Unit
) {
    // Local state for demonstration purposes
    var notificationsEnabled by remember { mutableStateOf(true) }
    var userName by remember { mutableStateOf("") }
    
    val scrollState = rememberScrollState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(id = R.string.settings)) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = stringResource(id = R.string.back)
                        )
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(scrollState)
                .padding(16.dp)
        ) {
            // Appearance Section
            SettingsSectionHeader(stringResource(id = R.string.appearance))
            SettingRow(
                label = stringResource(id = R.string.dark_mode),
                checked = isDarkTheme,
                onCheckedChange = { onThemeToggle() }
            )
            
            SettingsDivider()

            // Units Section
            SettingsSectionHeader(stringResource(id = R.string.units))
            SettingRow(
                label = stringResource(id = R.string.temp_fahrenheit),
                checked = tempUnit == "fahrenheit",
                onCheckedChange = { onTempUnitToggle() }
            )
            
            Column(modifier = Modifier.padding(vertical = 8.dp)) {
                Text(text = stringResource(id = R.string.wind_speed_unit), style = MaterialTheme.typography.bodyLarge)
                Row(
                    modifier = Modifier.fillMaxWidth().padding(top = 8.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    listOf("kmh", "ms", "mph", "kn").forEach { unit ->
                        FilterChip(
                            selected = windUnit == unit,
                            onClick = { onWindUnitChange(unit) },
                            label = { 
                                Text(when(unit) {
                                    "kmh" -> "km/h"
                                    "ms" -> "m/s"
                                    else -> unit
                                }) 
                            }
                        )
                    }
                }
            }

            Column(modifier = Modifier.padding(vertical = 8.dp)) {
                Text(text = stringResource(id = R.string.precip_unit), style = MaterialTheme.typography.bodyLarge)
                Row(
                    modifier = Modifier.fillMaxWidth().padding(top = 8.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    listOf("mm", "inch").forEach { unit ->
                        FilterChip(
                            selected = precipUnit == unit,
                            onClick = { onPrecipUnitChange(unit) },
                            label = { Text(unit) }
                        )
                    }
                }
            }
            
            SettingsDivider()

            // Preferences Section
            SettingsSectionHeader(stringResource(id = R.string.preferences))
            
            Column(modifier = Modifier.padding(vertical = 8.dp)) {
                Text(
                    text = stringResource(id = R.string.forecast_days_label, forecastDays),
                    style = MaterialTheme.typography.bodyLarge
                )
                Slider(
                    value = forecastDays.toFloat(),
                    onValueChange = { onForecastDaysChange(it.roundToInt()) },
                    valueRange = 1f..14f,
                    steps = 12
                )
            }

            SettingRow(
                label = stringResource(id = R.string.notifications),
                checked = notificationsEnabled,
                onCheckedChange = { notificationsEnabled = it }
            )

            SettingsDivider()

            // User Section
            SettingsSectionHeader(stringResource(id = R.string.account))
            OutlinedTextField(
                value = userName,
                onValueChange = { userName = it },
                label = { Text(stringResource(id = R.string.display_name)) },
                modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
                placeholder = { Text(stringResource(id = R.string.enter_name)) }
            )
            
            Spacer(modifier = Modifier.padding(bottom = 32.dp))
        }
    }
}

@Composable
fun SettingsSectionHeader(text: String) {
    Text(
        text = text,
        style = MaterialTheme.typography.titleMedium,
        color = MaterialTheme.colorScheme.primary,
        modifier = Modifier.padding(vertical = 8.dp)
    )
}

@Composable
fun SettingsDivider() {
    HorizontalDivider(
        modifier = Modifier.padding(vertical = 12.dp),
        color = Color.Gray.copy(alpha = 0.3f)
    )
}

@Composable
fun SettingRow(
    label: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(text = label, style = MaterialTheme.typography.bodyLarge)
        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange
        )
    }
}
