package com.example.whatstheweather.data

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query

interface WeatherApiService {
    @GET("v1/forecast?daily=weathercode,temperature_2m_max,temperature_2m_min,apparent_temperature_max,apparent_temperature_min,precipitation_probability_max,windspeed_10m_max,winddirection_10m_dominant&timezone=auto")
    suspend fun getWeatherForecast(
        @Query("latitude") latitude: Double,
        @Query("longitude") longitude: Double
    ): WeatherResponse
}

object WeatherApiClient {
    private const val BASE_URL = "https://api.open-meteo.com/"
    
    val service: WeatherApiService by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(WeatherApiService::class.java)
    }
}

data class WeatherResponse(
    val latitude: Double,
    val longitude: Double,
    val timezone: String,
    val daily: DailyWeather
)

data class DailyWeather(
    val time: List<String>,
    val weathercode: List<Int>,
    val temperature_2m_max: List<Double>,
    val temperature_2m_min: List<Double>,
    val apparent_temperature_max: List<Double>,
    val apparent_temperature_min: List<Double>,
    val precipitation_probability_max: List<Int>,
    val windspeed_10m_max: List<Double>,
    val winddirection_10m_dominant: List<Int>
)
