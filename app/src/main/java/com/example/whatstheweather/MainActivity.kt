package com.example.whatstheweather

import android.Manifest
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.example.whatstheweather.ui.WeatherScreen
import com.example.whatstheweather.ui.theme.WhatsTheWeatherTheme

class MainActivity : ComponentActivity() {
    private var hasLocationPermission by mutableStateOf(false)
    
    private val locationPermissionRequest = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        hasLocationPermission = isGranted
    }
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        locationPermissionRequest.launch(Manifest.permission.ACCESS_COARSE_LOCATION)
        
        setContent {
            WhatsTheWeatherTheme {
                if (hasLocationPermission) {
                    WeatherScreen()
                } else {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("Location permission is required to show weather information")
                    }
                }
            }
        }
    }
}