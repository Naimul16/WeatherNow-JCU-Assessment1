package au.edu.jcu.assessment.utilityapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import au.edu.jcu.assessment.utilityapp.data.CurrentWeather
import au.edu.jcu.assessment.utilityapp.data.WeatherDependencies
import au.edu.jcu.assessment.utilityapp.data.availableCities
import au.edu.jcu.assessment.utilityapp.ui.theme.MobileTech_UtilityAppTheme
import au.edu.jcu.assessment.utilityapp.viewmodel.WeatherUiState
import au.edu.jcu.assessment.utilityapp.viewmodel.WeatherViewModel
import au.edu.jcu.assessment.utilityapp.viewmodel.WeatherViewModelFactory
import java.util.Locale
import kotlin.math.roundToInt

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MobileTech_UtilityAppTheme { UtilityApp() }
        }
    }
}

@Composable
fun UtilityApp() {
    // The Activity's ViewModel survives configuration changes (e.g. rotation).
    val weatherViewModel: WeatherViewModel = viewModel(
        factory = WeatherViewModelFactory(WeatherDependencies.repository)
    )
    val state by weatherViewModel.uiState.collectAsState()
    var selectedTab by remember { mutableStateOf("Utility") }

    Scaffold(
        bottomBar = {
            NavigationBar {
                NavigationBarItem(
                    icon = { Icon(Icons.Default.Home, contentDescription = "Home") },
                    label = { Text("Weather") },
                    selected = selectedTab == "Utility",
                    onClick = { selectedTab = "Utility" }
                )
                NavigationBarItem(
                    icon = { Icon(Icons.Default.Settings, contentDescription = "Settings") },
                    label = { Text("Settings") },
                    selected = selectedTab == "Settings",
                    onClick = { selectedTab = "Settings" }
                )
            }
        }
    ) { innerPadding ->
        Box(Modifier.fillMaxSize().padding(innerPadding)) {
            when (selectedTab) {
                "Utility" -> UtilityScreen(state, onRefresh = weatherViewModel::refresh)
                "Settings" -> SettingsScreen(
                    state = state,
                    onCelsiusChange = weatherViewModel::setCelsius,
                    onCitySelected = weatherViewModel::selectCity
                )
            }
        }
    }
}

@Composable
fun UtilityScreen(state: WeatherUiState, onRefresh: () -> Unit) {
    Column(
        modifier = Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text("WeatherNow", style = MaterialTheme.typography.headlineLarge, fontWeight = FontWeight.Bold)
        Text("Your weather at a glance", style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant)
        Card(modifier = Modifier.fillMaxWidth()) {
            Column(
                modifier = Modifier.fillMaxWidth().padding(24.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(state.city.name, style = MaterialTheme.typography.titleLarge)
                when {
                    state.loading -> {
                        CircularProgressIndicator()
                        Text("Updating weather…")
                    }
                    state.error != null -> {
                        Text(state.error, color = MaterialTheme.colorScheme.error, textAlign = TextAlign.Center)
                    }
                    state.weather != null -> WeatherDetails(state.weather, state.isCelsius)
                    else -> Text("No weather data available")
                }
            }
        }
        Button(onClick = onRefresh, enabled = !state.loading, modifier = Modifier.fillMaxWidth()) {
            Text("Refresh weather")
        }
        Text("Weather data: Open-Meteo • Select a city or temperature unit in Settings.",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant)
    }
}

@Composable
private fun WeatherDetails(weather: CurrentWeather, isCelsius: Boolean) {
    val temperature = formatTemperature(weather.temperature_2m, isCelsius)
    Text(weatherEmoji(weather.weather_code), style = MaterialTheme.typography.displayMedium)
    Text(temperature, style = MaterialTheme.typography.displayLarge, fontWeight = FontWeight.Bold)
    Text(weatherDescription(weather.weather_code), style = MaterialTheme.typography.titleMedium)
    Spacer(Modifier.height(8.dp))
    InfoRow("Feels like", formatTemperature(weather.apparent_temperature, isCelsius))
    InfoRow("Humidity", "${weather.relative_humidity_2m}%")
    InfoRow("Wind speed", String.format(Locale.US, "%.1f km/h", weather.wind_speed_10m))
    InfoRow("Precipitation", String.format(Locale.US, "%.1f mm", weather.precipitation))
}

@Composable
private fun InfoRow(label: String, value: String) {
    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
        Text(label, color = MaterialTheme.colorScheme.onSurfaceVariant)
        Text(value, fontWeight = FontWeight.SemiBold)
    }
}

@Composable
fun SettingsScreen(
    state: WeatherUiState,
    onCelsiusChange: (Boolean) -> Unit,
    onCitySelected: (au.edu.jcu.assessment.utilityapp.data.City) -> Unit
) {
    Column(
        Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text("Settings", style = MaterialTheme.typography.headlineLarge, fontWeight = FontWeight.Bold)
        Text("Temperature unit", style = MaterialTheme.typography.titleMedium)
        Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween) {
            Text(if (state.isCelsius) "Celsius (°C)" else "Fahrenheit (°F)")
            Switch(checked = state.isCelsius, onCheckedChange = onCelsiusChange)
        }
        Text("Choose a city", style = MaterialTheme.typography.titleMedium)
        Text("Weather updates automatically when you select a city.",
            style = MaterialTheme.typography.bodySmall)
        availableCities.forEach { city ->
            FilterChip(
                selected = state.city == city,
                onClick = { onCitySelected(city) },
                label = { Text(city.name) },
                modifier = Modifier.fillMaxWidth()
            )
        }
        Text("Preferences last until the app process is closed.",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant)
    }
}

internal fun formatTemperature(celsius: Double, isCelsius: Boolean): String =
    if (isCelsius) "${celsius.roundToInt()}°C" else "${(celsius * 9 / 5 + 32).roundToInt()}°F"

internal fun weatherDescription(code: Int): String = when (code) {
    0 -> "Clear sky"
    1, 2 -> "Partly cloudy"
    3 -> "Overcast"
    45, 48 -> "Foggy"
    51, 53, 55, 56, 57 -> "Drizzle"
    61, 63, 65, 66, 67, 80, 81, 82 -> "Rainy"
    71, 73, 75, 77, 85, 86 -> "Snowy"
    95, 96, 99 -> "Thunderstorm"
    else -> "Weather conditions"
}

internal fun weatherEmoji(code: Int): String = when (code) {
    0 -> "☀️"
    1, 2 -> "🌤️"
    3 -> "☁️"
    45, 48 -> "🌫️"
    95, 96, 99 -> "⛈️"
    71, 73, 75, 77, 85, 86 -> "❄️"
    else -> "🌧️"
}
