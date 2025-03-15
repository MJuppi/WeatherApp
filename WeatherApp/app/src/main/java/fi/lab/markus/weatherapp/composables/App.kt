package fi.lab.markus.weatherapp.composables

import android.Manifest
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.lifecycle.viewmodel.compose.viewModel

/**
 * Renders the main application UI.
 *
 * This function is the entry point for the application's UI. It initializes the LocationViewModel
 * and requests location permissions using the rememberLauncherForActivityResult API. If all
 * permissions are granted, it starts location updates through the LocationViewModel. The UI is
 * composed using the Column composable, which displays the LocationInfo composable.
 *
 * @return The rendered application UI.
 */
@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun App() {
    val viewModel: LocationViewModel = viewModel()
    val permissionLauncher =
        rememberLauncherForActivityResult(
            contract = ActivityResultContracts.RequestMultiplePermissions(),
            onResult = { permissions ->
                // Check if all requested permissions have been granted
                val allPermissionsGranted = permissions.entries.all { it.value }
                if (allPermissionsGranted) {
                    // Start location updates through the ViewModel if permissions are
                    // granted
                    viewModel.startLocationUpdates()
                }
            }
        )
    LaunchedEffect(Unit) {
        permissionLauncher.launch(
            arrayOf(
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION
            )
        )
    }
    Column { LocationInfoScreen(viewModel) }
}
