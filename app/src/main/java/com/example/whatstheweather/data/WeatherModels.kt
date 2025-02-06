package com.example.whatstheweather.data

import java.util.*

data class WeatherResponse(
    val daily: DailyUnits,
    val daily_units: DailyForecast,
    val latitude: Double,
    val longitude: Double,
    val timezone: String,
    val timezone_abbreviation: String,
    val elevation: Double
)

data class DailyUnits(
    val time: List<String>,
    val temperature_2m_max: List<Double>,
    val temperature_2m_min: List<Double>,
    val apparent_temperature_max: List<Double>,
    val apparent_temperature_min: List<Double>,
    val precipitation_probability_max: List<Int>,
    val windspeed_10m_max: List<Double>,
    val winddirection_10m_dominant: List<Int>,
    val weathercode: List<Int>
)

data class DailyForecast(
    val time: String,
    val temperature_2m_max: String,
    val temperature_2m_min: String,
    val apparent_temperature_max: String,
    val apparent_temperature_min: String,
    val precipitation_probability_max: String,
    val windspeed_10m_max: String,
    val winddirection_10m_dominant: String
)

data class WeatherInfo(
    val date: String,
    val maxTemp: Double,
    val minTemp: Double,
    val maxFeelsLike: Double,
    val minFeelsLike: Double,
    val precipitationProbability: Int,
    val windSpeed: Double,
    val windDirection: Int,
    val weatherCode: Int
) {
    val weatherDescription: String
        get() = when (weatherCode) {
            0 -> "Açık"
            1, 2, 3 -> "Parçalı Bulutlu"
            45, 48 -> "Sisli"
            51, 53, 55 -> "Çisenti"
            61, 63, 65 -> "Yağmurlu"
            71, 73, 75 -> "Karlı"
            77 -> "Kar Taneleri"
            80, 81, 82 -> "Sağanak Yağışlı"
            85, 86 -> "Kar Yağışlı"
            95 -> "Gök Gürültülü"
            96, 99 -> "Dolu"
            else -> "Bilinmeyen"
        }
    
    val weatherIcon: String
        get() = when (weatherCode) {
            0 -> "01d" // Clear
            1, 2, 3 -> "02d" // Partly cloudy
            45, 48 -> "50d" // Foggy
            51, 53, 55 -> "09d" // Drizzle
            61, 63, 65 -> "10d" // Rain
            71, 73, 75, 77 -> "13d" // Snow
            80, 81, 82 -> "09d" // Rain showers
            85, 86 -> "13d" // Snow showers
            95, 96, 99 -> "11d" // Thunderstorm
            else -> "50d"
        }

    fun getOutdoorActivityScore(): OutdoorActivityScore {
        val season = getSeason()
        var score = 100

        // Temperature factor based on season
        val avgTemp = (maxTemp + minTemp) / 2
        score -= when (season) {
            Season.WINTER -> {
                when {
                    avgTemp < -10 -> 40  // Too cold
                    avgTemp < 0 -> 20
                    avgTemp in 0.0..10.0 -> 0  // Ideal winter temp
                    avgTemp > 15 -> 10  // Unusually warm for winter, slightly positive
                    else -> 5
                }
            }
            Season.SUMMER -> {
                when {
                    avgTemp > 35 -> 40  // Too hot
                    avgTemp > 30 -> 20
                    avgTemp in 20.0..28.0 -> 0  // Ideal summer temp
                    avgTemp < 15 -> 15  // Too cool for summer
                    else -> 5
                }
            }
            Season.SPRING, Season.FALL -> {
                when {
                    avgTemp > 30 -> 30  // Too hot
                    avgTemp < 5 -> 30   // Too cold
                    avgTemp in 15.0..25.0 -> 0  // Ideal spring/fall temp
                    else -> 10
                }
            }
        }

        // Precipitation factor
        score -= (precipitationProbability * 0.5).toInt()

        // Wind factor
        score -= when {
            windSpeed > 50 -> 30  // Very strong wind
            windSpeed > 30 -> 20  // Strong wind
            windSpeed > 20 -> 10  // Moderate wind
            else -> 0
        }

        // Weather code factor
        score -= when (weatherCode) {
            0 -> 0  // Clear sky
            1, 2, 3 -> 5  // Partly cloudy
            45, 48 -> 15  // Foggy
            51, 53, 55 -> 20  // Drizzle
            61, 63, 65 -> 30  // Rain
            71, 73, 75, 77 -> 40  // Snow
            80, 81, 82 -> 35  // Rain showers
            85, 86 -> 40  // Snow showers
            95, 96, 99 -> 50  // Thunderstorm
            else -> 25
        }

        // Ensure score stays within 0-100 range
        val finalScore = score.coerceIn(0, 100)

        val recommendation = when {
            finalScore >= 80 -> when (Locale.getDefault().language) {
                "tr" -> "Hava harika! Dışarıda vakit geçirmek için mükemmel bir gün."
                else -> "Perfect weather! An excellent day to spend time outside."
            }
            finalScore >= 60 -> when (Locale.getDefault().language) {
                "tr" -> "Güzel bir hava var. Dışarıda aktivite yapmak için uygun."
                else -> "Nice weather. Good conditions for outdoor activities."
            }
            finalScore >= 40 -> when (Locale.getDefault().language) {
                "tr" -> "Hava ortalama. Dışarı çıkacaksanız hazırlıklı olun."
                else -> "Average weather. Be prepared if you're going out."
            }
            finalScore >= 20 -> when (Locale.getDefault().language) {
                "tr" -> "Hava pek iyi değil. Mecbur değilseniz dışarı çıkmayın."
                else -> "Weather isn't great. Stay inside unless necessary."
            }
            else -> when (Locale.getDefault().language) {
                "tr" -> "Kötü hava koşulları! Mümkünse evde kalın."
                else -> "Bad weather conditions! Stay home if possible."
            }
        }

        return OutdoorActivityScore(finalScore, recommendation)
    }

    private fun getSeason(): Season {
        val month = date.substring(5, 7).toInt()
        return when (month) {
            12, 1, 2 -> Season.WINTER
            3, 4, 5 -> Season.SPRING
            6, 7, 8 -> Season.SUMMER
            9, 10, 11 -> Season.FALL
            else -> Season.WINTER
        }
    }
}

data class OutdoorActivityScore(
    val score: Int,
    val recommendation: String
)

enum class Season {
    WINTER, SPRING, SUMMER, FALL
}

data class City(
    val id: Int,
    val name: String,
    val coord: Coordinates,
    val country: String,
    val population: Int,
    val timezone: Int,
    val sunrise: Long,
    val sunset: Long
)

data class Coordinates(
    val lat: Double,
    val lon: Double
)

data class MainWeather(
    val temp: Double,
    val feels_like: Double,
    val temp_min: Double,
    val temp_max: Double,
    val pressure: Int,
    val sea_level: Int,
    val grnd_level: Int,
    val humidity: Int,
    val temp_kf: Double
)

data class Wind(
    val speed: Double,
    val deg: Int,
    val gust: Double
)

data class Weather(
    val id: Int,
    val main: String,
    val description: String,
    val icon: String
)

data class Clouds(
    val all: Int
)

data class Rain(
    val `3h`: Double
)

data class Snow(
    val `3h`: Double
)

data class Sys(
    val pod: String
) 