package fi.lab.markus.weatherapp.composables

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

class SettingsManager(private val context: Context) {

    companion object {
        val TEMP_UNIT = stringPreferencesKey("temp_unit")
        val WIND_UNIT = stringPreferencesKey("wind_unit")
        val PRECIP_UNIT = stringPreferencesKey("precip_unit")
        val FORECAST_DAYS = intPreferencesKey("forecast_days")
        val LANGUAGE = stringPreferencesKey("language")
        val NOTIFICATIONS_ENABLED = androidx.datastore.preferences.core.booleanPreferencesKey("notifications_enabled")
    }

    val tempUnitFlow: Flow<String> = context.dataStore.data.map { preferences ->
        preferences[TEMP_UNIT] ?: "celsius"
    }

    val windUnitFlow: Flow<String> = context.dataStore.data.map { preferences ->
        preferences[WIND_UNIT] ?: "kmh"
    }

    val precipUnitFlow: Flow<String> = context.dataStore.data.map { preferences ->
        preferences[PRECIP_UNIT] ?: "mm"
    }

    val forecastDaysFlow: Flow<Int> = context.dataStore.data.map { preferences ->
        preferences[FORECAST_DAYS] ?: 7
    }

    val languageFlow: Flow<String> = context.dataStore.data.map { preferences ->
        preferences[LANGUAGE] ?: "en"
    }

    val notificationsEnabledFlow: Flow<Boolean> = context.dataStore.data.map { preferences ->
        preferences[NOTIFICATIONS_ENABLED] ?: true
    }

    suspend fun saveTempUnit(unit: String) {
        context.dataStore.edit { preferences ->
            preferences[TEMP_UNIT] = unit
        }
    }

    suspend fun saveWindUnit(unit: String) {
        context.dataStore.edit { preferences ->
            preferences[WIND_UNIT] = unit
        }
    }

    suspend fun savePrecipUnit(unit: String) {
        context.dataStore.edit { preferences ->
            preferences[PRECIP_UNIT] = unit
        }
    }

    suspend fun saveForecastDays(days: Int) {
        context.dataStore.edit { preferences ->
            preferences[FORECAST_DAYS] = days
        }
    }

    suspend fun saveLanguage(lang: String) {
        context.dataStore.edit { preferences ->
            preferences[LANGUAGE] = lang
        }
    }

    suspend fun saveNotificationsEnabled(enabled: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[NOTIFICATIONS_ENABLED] = enabled
        }
    }
}
