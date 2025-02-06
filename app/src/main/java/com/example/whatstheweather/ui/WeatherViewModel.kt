package com.example.whatstheweather.ui

import android.annotation.SuppressLint
import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.whatstheweather.data.WeatherApiClient
import com.example.whatstheweather.data.WeatherInfo
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class WeatherViewModel(application: Application) : AndroidViewModel(application) {
    private val fusedLocationClient: FusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(application)
    
    private val _weatherState = MutableStateFlow<WeatherState>(WeatherState.Loading)
    val weatherState: StateFlow<WeatherState> = _weatherState.asStateFlow()
    
    init {
        fetchWeatherData()
    }
    
    @SuppressLint("MissingPermission")
    private fun fetchWeatherData() {
        viewModelScope.launch {
            try {
                val location = fusedLocationClient.getCurrentLocation(Priority.PRIORITY_HIGH_ACCURACY, null).await()
                if (location != null) {
                    val weatherResponse = WeatherApiClient.service.getWeatherForecast(
                        lat = location.latitude,
                        lon = location.longitude
                    )
                    
                    val weatherInfoList = weatherResponse.daily.time.mapIndexed { index, date ->
                        WeatherInfo(
                            date = date,
                            maxTemp = weatherResponse.daily.temperature_2m_max[index],
                            minTemp = weatherResponse.daily.temperature_2m_min[index],
                            maxFeelsLike = weatherResponse.daily.apparent_temperature_max[index],
                            minFeelsLike = weatherResponse.daily.apparent_temperature_min[index],
                            precipitationProbability = weatherResponse.daily.precipitation_probability_max[index],
                            windSpeed = weatherResponse.daily.windspeed_10m_max[index],
                            windDirection = weatherResponse.daily.winddirection_10m_dominant[index],
                            weatherCode = weatherResponse.daily.weathercode[index]
                        )
                    }
                    
                    _weatherState.value = WeatherState.Success(weatherInfoList)
                } else {
                    _weatherState.value = WeatherState.Error("Konum alınamadı")
                }
            } catch (e: Exception) {
                _weatherState.value = WeatherState.Error(e.message ?: "Bilinmeyen bir hata oluştu")
            }
        }
    }
}

sealed class WeatherState {
    object Loading : WeatherState()
    data class Success(val forecasts: List<WeatherInfo>) : WeatherState()
    data class Error(val message: String) : WeatherState()
}