package com.example.whatstheweather

import android.Manifest
import android.content.pm.PackageManager
import android.location.Location
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.core.app.ActivityCompat
import com.example.whatstheweather.data.WeatherInfo
import com.example.whatstheweather.ui.theme.WhatsTheWeatherTheme
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.example.whatstheweather.services.WeatherService
import com.example.whatstheweather.services.OpenMeteoWeatherService
import com.example.whatstheweather.services.OpenWeatherService

class MainActivity : ComponentActivity() {
    private var hasLocationPermission by mutableStateOf(false)
    private val locationPermissionRequest = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        hasLocationPermission = isGranted
        if (isGranted) {
            fetchLocationAndWeatherData()
        }
    }

    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private val handler = Handler(Looper.getMainLooper())
    private val retryInterval: Long = 10000 // 10 seconds

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        locationPermissionRequest.launch(Manifest.permission.ACCESS_COARSE_LOCATION)

        setContent {
            WhatsTheWeatherTheme {
                MainContent()
            }
        }
    }

    @Composable
    fun MainContent() {
        var location by remember { mutableStateOf<Location?>(null) }
        var weatherInfo by remember { mutableStateOf<WeatherInfo?>(null) }
        val useOpenMeteo = true // Set to false to use OpenWeatherService
        val weatherService: WeatherService = if (useOpenMeteo) {
            OpenMeteoWeatherService()
        } else {
            OpenWeatherService()
        }

        LaunchedEffect(hasLocationPermission, location, weatherService) {
            if (hasLocationPermission) {
                val currentLocation = location
                if (currentLocation != null) {
                    try {
                        weatherInfo = weatherService.getWeatherData("${currentLocation.latitude},${currentLocation.longitude}")
                    } catch (e: Exception) {
                        Log.e("Weather", "Error fetching weather data", e)
                        weatherInfo = null
                    }
                }
            }
        }

        LaunchedEffect(hasLocationPermission) {
            if (hasLocationPermission) {
                fetchLocationAndWeatherData()
            }
        }

        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            if (hasLocationPermission) {
                val currentLocation = location
                if (currentLocation != null) {
                    if (weatherInfo != null) {
                        Text("Temperature: ${weatherInfo?.maxTemp}")
                        Text("Description: ${weatherInfo?.weatherDescription}")
                    } else {
                        Text("Loading Weather Data")
                    }
                } else {
                    Text("Fetching location...")
                }
            } else {
                Text("Location permission is required to show weather information")
            }
        }
    }

    private fun fetchLocationAndWeatherData() {
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            return
        }

        fusedLocationClient.lastLocation
            .addOnSuccessListener { newLocation: Location? ->
                if (newLocation != null) {
                    Log.d("Location", "Location found: ${newLocation.latitude}, ${newLocation.longitude}")
                    // Update the location in MainContent
                    setContent {
                        WhatsTheWeatherTheme {
                            MainContent()
                        }
                    }
                } else {
                    Log.d("Location", "Last known location is null. Retrying in 10 seconds")
                    retryFetchLocation()
                }
            }
            .addOnFailureListener { e ->
                Log.e("Location", "Failed to get location: ${e.message}. Retrying in 10 seconds")
                retryFetchLocation()
            }
    }

    private fun retryFetchLocation() {
        handler.postDelayed({
            fetchLocationAndWeatherData()
        }, retryInterval)
    }
}