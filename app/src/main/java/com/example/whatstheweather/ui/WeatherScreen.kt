package com.example.whatstheweather.ui

import android.location.Location
import androidx.compose.animation.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.drawscope.Fill
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.example.whatstheweather.data.WeatherInfo
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun WeatherScreen(location: Location) {
    Column {
       Text("Weather Screen")
    }
}

@Composable
fun CircularProgressIndicatorWithLabel(
    percentage: Int,
    color: Color,
    modifier: Modifier = Modifier,
    size: Dp = 48.dp,
    strokeWidth: Dp = 4.dp
) {
    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier
            .background(Color.White.copy(alpha = 0.1f), CircleShape)
            .padding(8.dp)
    ) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier.size(size)
        ) {
            Canvas(
                modifier = Modifier
                    .size(size)
                    .padding(strokeWidth / 2)
            ) {
                drawArc(
                    color = color,
                    startAngle = -90f,
                    sweepAngle = (percentage / 100f) * 360f,
                    useCenter = false,
                    style = Stroke(width = strokeWidth.toPx(), cap = StrokeCap.Round)
                )
            }
        }
        
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier.padding(4.dp)
        ) {
            Text(
                text = getLocalizedString("Percentage", "$percentage%"),
                color = Color.Black,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                maxLines = 1
            )
            Text(
                text = getLocalizedString("ForWalking"),
                color = Color.Black,
                style = MaterialTheme.typography.bodySmall,
                fontWeight = FontWeight.Medium,
                textAlign = TextAlign.Center,
                maxLines = 1
            )
        }
    }
}

@Composable
fun LoadingScreen() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            CircularProgressIndicator(
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(48.dp)
            )
            Text(
                text = getLocalizedString("Loading"),
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.primary
            )
        }
    }
}

@Composable
fun ErrorScreen(message: String) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = getLocalizedString("Error") + ": $message",
            style = MaterialTheme.typography.bodyLarge,
            textAlign = TextAlign.Center,
            color = MaterialTheme.colorScheme.error,
            modifier = Modifier.padding(16.dp)
        )
    }
}

@Composable
fun WeatherList(forecasts: List<WeatherInfo>) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 12.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        items(forecasts) { forecast ->
            WeatherCard(forecast = forecast)
        }
    }
}

@Composable
fun WeatherCard(forecast: WeatherInfo) {
    val activityScore = forecast.getOutdoorActivityScore()
    
    val cardColor = when {
        activityScore.score >= 80 -> Color(0xFF4CAF50)
        activityScore.score >= 60 -> Color(0xFF7CB342)
        activityScore.score >= 40 -> Color(0xFFFFB300)
        activityScore.score >= 20 -> Color(0xFFF57C00)
        else -> Color(0xFFE64A19)
    }
    
    val gradient = Brush.verticalGradient(
        colors = listOf(
            cardColor,
            cardColor.copy(alpha = 0.85f)
        )
    )

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(170.dp)
            .clip(RoundedCornerShape(16.dp)),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .background(gradient)
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Left section: Date and basic info
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Text(
                    text = formatDate(forecast.date),
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    AsyncImage(
                        model = "https://openweathermap.org/img/wn/${forecast.weatherIcon}@2x.png",
                        contentDescription = forecast.weatherDescription,
                        modifier = Modifier
                            .size(56.dp)
                            .background(Color.White.copy(alpha = 0.15f), CircleShape)
                            .padding(4.dp)
                    )
                    Column(
                        verticalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Text(
                            text = "${forecast.maxTemp}°C",
                            style = MaterialTheme.typography.headlineMedium,
                            color = Color.Black,
                            fontWeight = FontWeight.Bold,
                            maxLines = 1
                        )
                        Text(
                            text = forecast.weatherDescription,
                            style = MaterialTheme.typography.titleMedium,
                            color = Color.Black,
                            fontWeight = FontWeight.Medium,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }
            }

            // Right section: Weather details and score
            Column(
                horizontalAlignment = Alignment.End,
                verticalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier
                    .padding(start = 12.dp)
                    .width(110.dp)
            ) {
                CircularProgressIndicatorWithLabel(
                    percentage = activityScore.score,
                    color = cardColor,
                    size = 72.dp,
                    strokeWidth = 4.dp
                )
                
                Column(
                    horizontalAlignment = Alignment.End,
                    verticalArrangement = Arrangement.spacedBy(4.dp),
                    modifier = Modifier.padding(top = 8.dp)
                ) {
                    if (forecast.precipitationProbability > 0) {
                        Text(
                            text = getLocalizedString("RainShort", ": ${forecast.precipitationProbability}%"),
                            style = MaterialTheme.typography.titleSmall,
                            color = Color.Black,
                            fontWeight = FontWeight.Medium,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                    Text(
                        text = getLocalizedString("WindShort", ": ${forecast.windSpeed}"),
                        style = MaterialTheme.typography.titleSmall,
                        color = Color.Black,
                        fontWeight = FontWeight.Medium,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }
        }
    }
}

private fun formatDate(dateStr: String): String {
    val locale = Locale.getDefault()
    val inputFormat = SimpleDateFormat("yyyy-MM-dd", locale)
    val outputFormat = SimpleDateFormat("EEEE, d MMMM", locale)
    val date = inputFormat.parse(dateStr) ?: return dateStr
    return outputFormat.format(date).replaceFirstChar { it.uppercase() }
}

private fun getLocalizedString(key: String, value: String = ""): String {
    val language = Locale.getDefault().language
    return when (language) {
        "tr" -> when (key) {
            "AppTitle" -> "Hava Durumu Tahmini"
            "Min" -> "En düşük"
            "Max" -> "En yüksek"
            "Feels" -> "Hissedilen"
            "Rain" -> "Yağış"
            "Wind" -> "Rüzgar"
            "RainShort" -> "Yağış"
            "WindShort" -> "Rüzgar"
            "Loading" -> "Yükleniyor..."
            "Error" -> "Bir hata oluştu"
            "Today" -> "Bugün"
            "Tomorrow" -> "Yarın"
            "ForWalking" -> "yürümek için"
            "Percentage" -> ""  // Yüzde işareti value ile gelecek
            "Monday" -> "Pazartesi"
            "Tuesday" -> "Salı"
            "Wednesday" -> "Çarşamba"
            "Thursday" -> "Perşembe"
            "Friday" -> "Cuma"
            "Saturday" -> "Cumartesi"
            "Sunday" -> "Pazar"
            "January" -> "Ocak"
            "February" -> "Şubat"
            "March" -> "Mart"
            "April" -> "Nisan"
            "May" -> "Mayıs"
            "June" -> "Haziran"
            "July" -> "Temmuz"
            "August" -> "Ağustos"
            "September" -> "Eylül"
            "October" -> "Ekim"
            "November" -> "Kasım"
            "December" -> "Aralık"
            else -> key
        }
        else -> when (key) {
            "AppTitle" -> "Weather Forecast"
            "Loading" -> "Loading..."
            "Error" -> "An error occurred"
            "RainShort" -> "Rain"
            "WindShort" -> "Wind"
            "ForWalking" -> "for walking"
            "Percentage" -> ""  // Percentage sign will come with value
            else -> key
        }
    } + value
}