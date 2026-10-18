package au.edu.jcu.assessment.utilityapp.data

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

data class City(val name: String, val latitude: Double, val longitude: Double)

val availableCities = listOf(
    City("Brisbane", -27.4698, 153.0251),
    City("Sydney", -33.8688, 151.2093),
    City("Melbourne", -37.8136, 144.9631),
    City("Dhaka", 23.8103, 90.4125)
)

/** Repository isolates network details from the UI and ViewModel. */
class WeatherRepository(private val api: WeatherApi) {
    suspend fun loadWeather(city: City): CurrentWeather =
        api.getCurrentWeather(city.latitude, city.longitude).current
}

/** Simple manual dependency injection: one shared Retrofit service and repository. */
object WeatherDependencies {
    private val api: WeatherApi by lazy {
        Retrofit.Builder()
            .baseUrl("https://api.open-meteo.com/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(WeatherApi::class.java)
    }
    val repository: WeatherRepository by lazy { WeatherRepository(api) }
}
