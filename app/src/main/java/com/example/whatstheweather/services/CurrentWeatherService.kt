package com.example.whatstheweather.services

import com.example.whatstheweather.data.WeatherApiClient
import com.example.whatstheweather.data.WeatherInfo

class CurrentWeatherService : WeatherService {
    override suspend fun getWeatherData(location: String): WeatherInfo {
        // Extract latitude and longitude from the location string
        val (latitude, longitude) = location.split(",").map { it.toDouble() }

        try {
            val response = WeatherApiClient.service.getWeatherForecast(latitude, longitude)

            // Extract the data for the current day
            val daily = response.daily
            return WeatherInfo(
                date = daily.time[0],
                maxTemp = daily.temperature_2m_max[0],
                minTemp = daily.temperature_2m_min[0],
                maxFeelsLike = daily.apparent_temperature_max[0],
                minFeelsLike = daily.apparent_temperature_min[0],
                precipitationProbability = daily.precipitation_probability_max[0],
                windSpeed = daily.windspeed_10m_max[0],
                windDirection = daily.winddirection_10m_dominant[0],
                weatherCode = daily.weathercode[0]
            )
        } catch (e: Exception) {
            // Handle errors (e.g., network issues, API errors)
            e.printStackTrace()
            // Return a default WeatherInfo object
            return WeatherInfo(
                date = "2024-07-27",
                maxTemp = 25.0,
                minTemp = 20.0,
                maxFeelsLike = 25.0,
                minFeelsLike = 20.0,
                precipitationProbability = 0,
                windSpeed = 5.0,
                windDirection = 180,
                weatherCode = 0
            )
        }
    }
}
