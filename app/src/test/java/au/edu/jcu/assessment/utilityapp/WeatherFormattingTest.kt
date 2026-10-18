package au.edu.jcu.assessment.utilityapp

import org.junit.Assert.assertEquals
import org.junit.Test

class WeatherFormattingTest {
    @Test fun convertsCelsiusToFahrenheit() {
        assertEquals("32°F", formatTemperature(0.0, false))
        assertEquals("25°C", formatTemperature(25.0, true))
    }

    @Test fun interpretsWeatherCodes() {
        assertEquals("Clear sky", weatherDescription(0))
        assertEquals("Rainy", weatherDescription(61))
    }
}
