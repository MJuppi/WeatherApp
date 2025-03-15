package fi.lab.markus.weatherapp.composables

import com.google.gson.annotations.SerializedName
import retrofit2.http.GET
import retrofit2.http.Query

data class Hourly(
    val time: List<String>,
    @SerializedName("temperature_2m") val temp: List<Double>,
    @SerializedName("weather_code") val weatherCode: List<Int>,
    @SerializedName("is_day") val isDay: List<Int>
)

data class Daily(
    val time: List<String>,
    @SerializedName("temperature_2m_max") val tempMax: List<Double>,
    @SerializedName("temperature_2m_min") val tempMin: List<Double>,
    val sunrise: List<String>,
    val sunset: List<String>
)

data class WeatherResponse(
    val latitude: Float,
    val longitude: Float,
    val timezone: String,
    val hourly: Hourly,
    val daily: Daily
)

interface WeatherService {
    /**
     * Retrieves weather data for the specified latitude and longitude.
     *
     * @param latitude The latitude coordinate of the location.
     * @param longitude The longitude coordinate of the location.
     * @param hourly String specifying the hourly weather data to retrieve. Defaults to
     * "temperature_2m,weather_code,is_day".
     * @param daily String specifying the daily weather data to retrieve. Defaults to
     * "temperature_2m_max,temperature_2m_min,sunrise,sunset".
     * @param forecastDays The number of days to forecast. Defaults to 14.
     * @param temperatureUnit The unit of temperature to use.
     * @param timezone The timezone of the location.
     * @return A WeatherResponse object containing the weather data.
     */
    @GET("forecast")
    suspend fun getWeather(
        @Query("latitude") latitude: Float,
        @Query("longitude") longitude: Float,
        @Query("hourly") hourly: String = "temperature_2m,weather_code,is_day",
        @Query("daily") daily: String = "temperature_2m_max,temperature_2m_min,sunrise,sunset",
        @Query("forecast_days") forecastDays: Int = 14,
        @Query("temperature_unit") temperatureUnit: String,
        @Query("timezone") timezone: String
    ): WeatherResponse
}
