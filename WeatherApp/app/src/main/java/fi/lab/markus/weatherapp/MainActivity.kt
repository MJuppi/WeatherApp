package fi.lab.markus.weatherapp

import android.os.Build
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import fi.lab.markus.weatherapp.composables.App
import fi.lab.markus.weatherapp.composables.NotificationHelper
import fi.lab.markus.weatherapp.composables.WeatherNotificationWorker
import fi.lab.markus.weatherapp.ui.theme.WeatherAppTheme
import java.util.concurrent.TimeUnit

class MainActivity : AppCompatActivity() {
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Initialize notification channel
        NotificationHelper(this).createNotificationChannel()

        // Schedule periodic weather checking
        scheduleWeatherWorker()

        setContent {
            val systemInDark = isSystemInDarkTheme()
            var isDarkTheme by remember { mutableStateOf(systemInDark) }

            WeatherAppTheme(darkTheme = isDarkTheme) {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background
                ) {
                    App(
                        isDarkTheme = isDarkTheme,
                        onThemeChange = { isDarkTheme = !isDarkTheme }
                    )
                }
            }
        }
    }

    private fun scheduleWeatherWorker() {
        val weatherWorkerRequest = PeriodicWorkRequestBuilder<WeatherNotificationWorker>(
            1, TimeUnit.HOURS
        ).build()

        WorkManager.getInstance(this).enqueueUniquePeriodicWork(
            "WeatherNotificationWorker",
            ExistingPeriodicWorkPolicy.KEEP,
            weatherWorkerRequest
        )
    }
}
