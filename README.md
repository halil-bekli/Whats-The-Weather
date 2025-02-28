# What's The Weather

A mobile Weather App to check if it's good to go outside with Kotlin and Jetpack Compose.

## API Integration

This application uses the OpenWeather API and Open-Meteo API to fetch weather data.

### OpenWeather API

The `OpenWeatherService` class is responsible for fetching weather data from the OpenWeather API. You need to provide your own OpenWeather API key for the application to work correctly.

### Open-Meteo API

The `OpenMeteoWeatherService` class is responsible for fetching weather data from the Open-Meteo API. This service uses the `WeatherApiClient` and `WeatherApi` to retrieve weather forecasts.

### How to Configure API

#### OpenWeather API

1.  Open the `OpenWeatherService.kt` file.
2.  Locate the `apiKey` constant within the `companion object`.
3.  Replace the placeholder value with your actual OpenWeather API key.

    ```kotlin
    class OpenWeatherService : WeatherService {
        companion object {
            private const val apiKey = "YOUR_OPENWEATHER_API_KEY" // Replace with your API key
        }
        // ...
    }
    ```

## Retry Location Fetching

If the application fails to fetch the location, it will retry every 10 seconds. This is implemented in the `fetchLocationAndWeatherData` method in `MainActivity.kt`.
