package au.edu.jcu.assessment.utilityapp.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import au.edu.jcu.assessment.utilityapp.data.City
import au.edu.jcu.assessment.utilityapp.data.CurrentWeather
import au.edu.jcu.assessment.utilityapp.data.WeatherRepository
import au.edu.jcu.assessment.utilityapp.data.availableCities
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class WeatherUiState(
    val city: City = availableCities.first(),
    val isCelsius: Boolean = true,
    val weather: CurrentWeather? = null,
    val loading: Boolean = false,
    val error: String? = null
)

class WeatherViewModel(private val repository: WeatherRepository) : ViewModel() {
    private val _uiState = MutableStateFlow(WeatherUiState())
    val uiState: StateFlow<WeatherUiState> = _uiState.asStateFlow()

    init { refresh() }

    fun selectCity(city: City) {
        if (city == _uiState.value.city) return
        _uiState.update { it.copy(city = city) }
        refresh()
    }

    fun setCelsius(enabled: Boolean) {
        _uiState.update { it.copy(isCelsius = enabled) }
    }

    fun refresh() {
        val requestedCity = _uiState.value.city
        viewModelScope.launch {
            _uiState.update { it.copy(loading = true, error = null, weather = null) }
            try {
                val result = repository.loadWeather(requestedCity)
                if (_uiState.value.city == requestedCity) {
                    _uiState.update { it.copy(weather = result, loading = false) }
                }
            } catch (error: Exception) {
                if (_uiState.value.city == requestedCity) {
                    _uiState.update {
                        it.copy(loading = false, error = "Couldn't load the weather. Check your internet connection and try again.")
                    }
                }
            }
        }
    }
}

class WeatherViewModelFactory(private val repository: WeatherRepository) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        require(modelClass.isAssignableFrom(WeatherViewModel::class.java))
        return WeatherViewModel(repository) as T
    }
}
