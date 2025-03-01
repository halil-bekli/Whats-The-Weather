package com.example.whatstheweather.services

import com.example.whatstheweather.data.WeatherInfo
import okhttp3.OkHttpClient
import okhttp3.Request
import org.json.JSONObject
import java.io.IOException

class OpenWeatherService : WeatherService {
    companion object {

        private const val apiKey = "" // Replace with your API key
        // Fill it from local.properties and .gitignore it


    }

    override suspend fun getWeatherData(location: String): WeatherInfo {
        val client = OkHttpClient()
        val request = Request.Builder()
            .url("https://api.openweathermap.org/data/2.5/weather?q=$location&appid=$apiKey&units=metric")
            .build()

        client.newCall(request).execute().use { response ->
            if (!response.isSuccessful) throw IOException("Unexpected code $response")

            val jsonData = response.body?.string()
            val jsonObject = JSONObject(jsonData)

            // Parse the JSON data
            val main = jsonObject.getJSONObject("main")
            val wind = jsonObject.getJSONObject("wind")
            val weatherArray = jsonObject.getJSONArray("weather")
            val weather = weatherArray.getJSONObject(0)

            return WeatherInfo(
                date = "2024-07-27", // Dummy value
                maxTemp = main.getDouble("temp_max"),
                minTemp = main.getDouble("temp_min"),
                maxFeelsLike = main.getDouble("feels_like"),
                minFeelsLike = main.getDouble("feels_like"),
                precipitationProbability = 0, // Dummy value
                windSpeed = wind.getDouble("speed"),
                windDirection = wind.getInt("deg"),
                weatherCode = 0 // Dummy value
            )
        }
    }
}
