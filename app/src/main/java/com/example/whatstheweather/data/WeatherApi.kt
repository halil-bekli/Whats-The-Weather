package com.example.whatstheweather.data

import retrofit2.http.GET
import retrofit2.http.Query

interface WeatherApi {
    @GET("v1/forecast")
    suspend fun getWeatherForecast(
        @Query("latitude") lat: Double,
        @Query("longitude") lon: Double,
        @Query("daily") daily: String = "weathercode,temperature_2m_max,temperature_2m_min,apparent_temperature_max,apparent_temperature_min,precipitation_probability_max,windspeed_10m_max,winddirection_10m_dominant",
        @Query("timezone") timezone: String = "auto"
    ): WeatherResponse
}

object WeatherApiClient {
    private const val BASE_URL = "https://api.open-meteo.com/"
    
    private val retrofit = retrofit2.Retrofit.Builder()
        .baseUrl(BASE_URL)
        .addConverterFactory(retrofit2.converter.gson.GsonConverterFactory.create())
        .build()
        
    val service: WeatherApi = retrofit.create(WeatherApi::class.java)
}