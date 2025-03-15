package fi.lab.markus.weatherapp.composables

import retrofit2.http.GET
import retrofit2.http.Query

data class LocationData(
    val name: String,
    val latitude: Float,
    val longitude: Float,
    val timezone: String,
    val country: String
)

data class LocationResponse(val results: List<LocationData>)

interface LocationService {
    /**
     * Retrieves a list of locations based on the provided name.
     *
     * @param name The name of the location to search for.
     * @param count The maximum number of locations to retrieve. Defaults to 5. Currently not used.
     * @param language The language of the location names. Defaults to "en".
     * @param format The format of the response. Defaults to "json".
     * @return A [LocationResponse] object containing the list of locations that match the search
     * criteria.
     */
    @GET("search")
    suspend fun getLocation(
        @Query("name") name: String,
        @Query("count") count: Int = 5,
        @Query("language") language: String = "en",
        @Query("format") format: String = "json",
    ): LocationResponse
}
