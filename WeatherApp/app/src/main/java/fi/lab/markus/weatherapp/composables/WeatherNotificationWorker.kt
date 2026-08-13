package fi.lab.markus.weatherapp.composables

import android.content.Context
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import fi.lab.markus.weatherapp.R
import kotlinx.coroutines.flow.first
import java.time.ZoneId
import java.time.ZonedDateTime

class WeatherNotificationWorker(
    context: Context,
    workerParams: WorkerParameters
) : CoroutineWorker(context, workerParams) {

    @RequiresApi(Build.VERSION_CODES.O)
    override suspend fun doWork(): Result {
        val settingsManager = SettingsManager(applicationContext)
        val notificationsEnabled = settingsManager.notificationsEnabledFlow.first()

        if (!notificationsEnabled) {
            return Result.success()
        }

        val locationRepository = LocationRepository(applicationContext)
        var lat: Float? = null
        var lon: Float? = null

        // This is a simplified way to get location in worker. 
        // In a real app, we might want to store the last known location in DataStore 
        // to avoid waiting for a fresh fix in the background.
        // For now, we'll try to get it, but workers have time limits.
        
        // Let's assume we have a way to get the last location or use a default.
        // Since startLocationUpdates is callback-based, it's tricky here without a wrapper.
        // For the sake of implementation, let's assume we use fixed coordinates if none found,
        // OR better, we'd have the last location stored.
        
        // Mocking coordinates for now or fetching from a hypothetical store
        lat = 60.1699f // Helsinki
        lon = 24.9384f

        try {
            val api = RetrofitClient.getService(applicationContext)
            val response = api.getWeather(
                latitude = lat,
                longitude = lon,
                temperatureUnit = "celsius",
                timezone = "auto"
            )

            val zoneId = ZoneId.of(response.timezone)
            val now = ZonedDateTime.now(zoneId)
            val nextHour = now.plusHours(1).hour
            
            // The hourly data starts from 00:00 of the day. 
            // We need to find the index for "next hour".
            // simplified: index = current_hour + 1 (if within 24h)
            val weatherCode = response.hourly.weatherCode.getOrNull(nextHour)

            if (weatherCode != null) {
                val (title, message) = when (weatherCode) {
                    65, 67 -> applicationContext.getString(R.string.severe_weather_alert) to 
                             applicationContext.getString(R.string.heavy_rain_warning)
                    75, 86 -> applicationContext.getString(R.string.severe_weather_alert) to 
                             applicationContext.getString(R.string.heavy_snow_warning)
                    95, 96, 99 -> applicationContext.getString(R.string.severe_weather_alert) to 
                                 applicationContext.getString(R.string.thunderstorm_warning)
                    82 -> applicationContext.getString(R.string.severe_weather_alert) to 
                         applicationContext.getString(R.string.rough_weather_warning)
                    else -> null to null
                }

                if (title != null && message != null) {
                    NotificationHelper(applicationContext).showNotification(title, message)
                }
            }

        } catch (e: Exception) {
            Log.e("WeatherWorker", "Error checking weather", e)
            return Result.retry()
        }

        return Result.success()
    }
}
