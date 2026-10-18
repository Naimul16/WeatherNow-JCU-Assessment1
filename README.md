# WeatherNow — JCU Mobile Computing Assessment 1

WeatherNow is a Kotlin and Jetpack Compose Android utility app that presents current weather **at a glance** for four selectable cities. Built from the supplied CP3406/CP5307 starter template.

## Features
- Live current-weather conditions using Open-Meteo (no API key required)
- Temperature, feels-like temperature, humidity, wind and precipitation
- Celsius/Fahrenheit switch
- Four city choices (Brisbane, Sydney, Melbourne, Dhaka)
- Refresh button, network loading feedback and retryable error message
- Two-tab Material Design 3 UI (Weather / Settings)

## Run the app
1. Open this project folder in Android Studio.
2. Allow Gradle to sync and install any SDKs Android Studio requests.
3. Select an emulator or connected Android device (Android 8.0/API 26 or newer).
4. Click **Run**. An internet connection is required for live data.

## Technologies and architecture
- **Week 1:** Kotlin, Android Studio and Android manifest
- **Week 2:** Compose `Scaffold`, `Column`, `Row`, `Card`, state-driven screens
- **Week 3:** Material 3 components, typography and theme
- **Week 4:** MVVM: `WeatherViewModel` exposes `StateFlow<WeatherUiState>`; repository handles data access; `WeatherDependencies` manually injects the repository
- **Week 5:** Retrofit uses Open-Meteo's `GET /v1/forecast` endpoint; Gson converts the JSON response

## Source organisation
- `MainActivity.kt`: existing template navigation plus Weather and Settings screens
- `data/WeatherApi.kt`: Retrofit interface and response models
- `data/WeatherRepository.kt`: repository, city model and manual dependency container
- `viewmodel/WeatherViewModel.kt`: view state, settings, refresh and error handling

## Testing checklist
- [ ] Gradle sync succeeds
- [ ] App launches with a network connection and loads Brisbane
- [ ] Refresh updates the weather
- [ ] Selecting Sydney/Dhaka/Melbourne retrieves the selected city's weather
- [ ] Celsius/Fahrenheit switch changes temperature and feels-like values
- [ ] Switching tabs keeps current selections
- [ ] With Wi-Fi/mobile data disabled, a clear error appears and retry works when reconnected
- [ ] Screen renders in portrait and landscape

## Limitations / future improvements
- City choices are predefined; no device-location permission is required.
- Preferences are deliberately held in memory (per the assessment instructions).
- Wind speed uses Open-Meteo's default km/h response, regardless of chosen temperature unit.
- No offline cache; live data needs internet.

## Attribution
Weather API: https://open-meteo.com/ — current observations / forecast data provided by Open-Meteo.
Initial scaffolding: JCU CP3406/CP5307 teaching template.
