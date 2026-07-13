package fi.lab.markus.weatherapp.composables

import com.google.gson.annotations.SerializedName
import retrofit2.http.GET
import retrofit2.http.Query

data class Hourly(
    val time: List<String>,
    @SerializedName("temperature_2m") val temp: List<Double>,
    @SerializedName("relative_humidity_2m") val humidity: List<Int>,
    @SerializedName("apparent_temperature") val apparentTemp: List<Double>,
    @SerializedName("precipitation_probability") val precipProb: List<Int>,
    @SerializedName("weather_code") val weatherCode: List<Int>,
    @SerializedName("is_day") val isDay: List<Int>,
    @SerializedName("wind_speed_10m") val windSpeed: List<Double>,
    val precipitation: List<Double>
)

data class Daily(
    val time: List<String>,
    @SerializedName("temperature_2m_max") val tempMax: List<Double>,
    @SerializedName("temperature_2m_min") val tempMin: List<Double>,
    val sunrise: List<String>,
    val sunset: List<String>,
    @SerializedName("uv_index_max") val uvIndexMax: List<Double>,
    @SerializedName("precipitation_sum") val precipSum: List<Double>
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
        @Query("hourly") hourly: String = "temperature_2m,relative_humidity_2m,apparent_temperature,precipitation_probability,precipitation,weather_code,is_day,wind_speed_10m",
        @Query("daily") daily: String = "temperature_2m_max,temperature_2m_min,sunrise,sunset,uv_index_max,precipitation_sum",
        @Query("forecast_days") forecastDays: Int = 14,
        @Query("temperature_unit") temperatureUnit: String,
        @Query("wind_speed_unit") windSpeedUnit: String = "kmh",
        @Query("precipitation_unit") precipitationUnit: String = "mm",
        @Query("timezone") timezone: String
    ): WeatherResponse
}
