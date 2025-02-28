package com.example.whatstheweather.services

import com.example.whatstheweather.services.WeatherService
import com.example.whatstheweather.data.WeatherInfo

class WeatherRepository(private var weatherService: WeatherService) {

    fun setWeatherService(service: WeatherService) {
        this.weatherService = service
    }

    suspend fun getWeatherData(location: String): WeatherInfo {
        return weatherService.getWeatherData(location)
    }
}
