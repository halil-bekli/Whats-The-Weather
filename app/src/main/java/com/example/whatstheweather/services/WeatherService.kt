package com.example.whatstheweather.services

import com.example.whatstheweather.data.WeatherInfo

interface WeatherService {
    suspend fun getWeatherData(location: String): WeatherInfo
}
