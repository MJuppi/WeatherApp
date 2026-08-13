package fi.lab.markus.weatherapp.composables

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import fi.lab.markus.weatherapp.R

class NotificationHelper(private val context: Context) {

    companion object {
        const val CHANNEL_ID = "weather_alerts"
    }

    fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = context.getString(R.string.weather_alert_channel_name)
            val descriptionText = context.getString(R.string.weather_alert_channel_description)
            val importance = NotificationManager.IMPORTANCE_HIGH
            val channel = NotificationChannel(CHANNEL_ID, name, importance).apply {
                description = descriptionText
            }
            val notificationManager: NotificationManager =
                context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }

    fun showNotification(title: String, message: String) {
        val builder = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(R.drawable.weather_app_icon) // Using the available drawable
            .setContentTitle(title)
            .setContentText(message)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)

        with(NotificationManagerCompat.from(context)) {
            // Check for permission is usually done by the caller or we can check here
            // but for simplicity we assume permissions are granted if the worker is running
            // and the user enabled them.
            try {
                notify(System.currentTimeMillis().toInt(), builder.build())
            } catch (e: SecurityException) {
                // Handle missing permission
            }
        }
    }
}
