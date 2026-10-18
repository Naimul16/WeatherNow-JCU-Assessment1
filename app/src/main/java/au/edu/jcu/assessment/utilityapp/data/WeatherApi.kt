package au.edu.jcu.assessment.utilityapp.data

import retrofit2.http.GET
import retrofit2.http.Query

/** A small subset of the Open-Meteo current-weather response. */
data class WeatherResponse(val current: CurrentWeather)

data class CurrentWeather(
    val temperature_2m: Double,
    val relative_humidity_2m: Int,
    val apparent_temperature: Double,
    val precipitation: Double,
    val weather_code: Int,
    val wind_speed_10m: Double
)

interface WeatherApi {
    @GET("v1/forecast")
    suspend fun getCurrentWeather(
        @Query("latitude") latitude: Double,
        @Query("longitude") longitude: Double,
        @Query("current") fields: String = "temperature_2m,relative_humidity_2m,apparent_temperature,precipitation,weather_code,wind_speed_10m",
        @Query("timezone") timezone: String = "auto"
    ): WeatherResponse
}
