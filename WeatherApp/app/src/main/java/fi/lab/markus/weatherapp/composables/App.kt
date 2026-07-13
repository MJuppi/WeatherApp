package fi.lab.markus.weatherapp.composables

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.ContextCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import kotlinx.coroutines.launch

/**
 * Renders the main application UI.
 */
@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun App(
    isDarkTheme: Boolean,
    onThemeChange: () -> Unit
) {
    val viewModel: LocationViewModel = viewModel()
    val context = LocalContext.current
    val navController = rememberNavController()
    val scope = rememberCoroutineScope()
    val settingsManager = remember { SettingsManager(context) }
    
    val tempUnit by settingsManager.tempUnitFlow.collectAsState(initial = "celsius")
    val forecastDays by settingsManager.forecastDaysFlow.collectAsState(initial = 7)
    val windUnit by settingsManager.windUnitFlow.collectAsState(initial = "kmh")
    val precipUnit by settingsManager.precipUnitFlow.collectAsState(initial = "mm")

    val permissionLauncher =
        rememberLauncherForActivityResult(
            contract = ActivityResultContracts.RequestMultiplePermissions(),
            onResult = { permissions ->
                val allPermissionsGranted = permissions.entries.all { it.value }
                viewModel.setPermissionsGranted(allPermissionsGranted)
            }
        )

    LaunchedEffect(Unit) {
        val hasFine = ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED
        val hasCoarse = ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.ACCESS_COARSE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED

        if (hasFine && hasCoarse) {
            viewModel.setPermissionsGranted(true)
        } else {
            permissionLauncher.launch(
                arrayOf(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                )
            )
        }
    }

    NavHost(navController = navController, startDestination = "home") {
        composable("home") {
            LocationInfoScreen(
                viewModel = viewModel,
                onRequestPermissions = {
                    permissionLauncher.launch(
                        arrayOf(
                            Manifest.permission.ACCESS_FINE_LOCATION,
                            Manifest.permission.ACCESS_COARSE_LOCATION
                        )
                    )
                },
                tempUnit = tempUnit,
                forecastDays = forecastDays,
                windUnit = windUnit,
                precipUnit = precipUnit,
                onNavigateToSettings = { navController.navigate("settings") }
            )
        }
        composable("settings") {
            SettingsScreen(
                onBack = { navController.popBackStack() },
                tempUnit = tempUnit,
                onTempUnitToggle = {
                    val newUnit = if (tempUnit == "celsius") "fahrenheit" else "celsius"
                    scope.launch { settingsManager.saveTempUnit(newUnit) }
                },
                isDarkTheme = isDarkTheme,
                onThemeToggle = onThemeChange,
                forecastDays = forecastDays,
                onForecastDaysChange = {
                    scope.launch { settingsManager.saveForecastDays(it) }
                },
                windUnit = windUnit,
                onWindUnitChange = {
                    scope.launch { settingsManager.saveWindUnit(it) }
                },
                precipUnit = precipUnit,
                onPrecipUnitChange = {
                    scope.launch { settingsManager.savePrecipUnit(it) }
                }
            )
        }
    }
}
