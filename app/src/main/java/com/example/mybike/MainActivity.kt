package com.example.mybike

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.mybike.data.DailyForecast
import com.example.mybike.data.BikeRideScore
import com.example.mybike.ui.theme.MyBikeTheme
import com.example.mybike.viewmodel.WeatherViewModel
import java.text.SimpleDateFormat
import java.util.*
import androidx.compose.foundation.clickable
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.TopAppBarDefaults
import com.example.mybike.data.DailySummary
import com.example.mybike.components.CircularScoreIndicator
import kotlin.math.roundToInt
import androidx.compose.ui.text.style.TextAlign

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MyBikeTheme {
                WeatherScreen()
            }
        }
    }
}

@Composable
fun ErrorMessage(error: String) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = "Error: $error",
            color = MaterialTheme.colorScheme.error,
            modifier = Modifier.padding(16.dp)
        )
    }
}

@Composable
fun LoadingIndicator() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        CircularProgressIndicator()
    }
}

@Composable
fun HourlyForecastCard(forecast: DailyForecast) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth()
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "${formatTime(forecast.dt)} (${getTimeOfDay(forecast.dt)})",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = forecast.weather.firstOrNull()?.description ?: "",
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
                
                Text(
                    text = formatTemperature(forecast.temp.day),
                    style = MaterialTheme.typography.headlineSmall
                )
            }
            
            Spacer(modifier = Modifier.height(8.dp))
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                WeatherInfoItem(
                    label = "Wind",
                    value = "${forecast.wind.speed.roundToInt()} m/s"
                )
                WeatherInfoItem(
                    label = "Humidity",
                    value = "${forecast.humidity}%"
                )
                CircularScoreIndicator(
                    score = BikeRideScore.calculateBikeRideScore(forecast),
                    size = 48f
                )
            }
        }
    }
}

@Composable
fun DailySummaryCard(
    dailySummary: DailySummary,
    onClick: () -> Unit
) {
    val bikeScore = BikeRideScore.calculateBikeRideScore(dailySummary.hourlyForecasts.first())
    
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
            .clickable(onClick = onClick),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = formatDate(dailySummary.date),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = formatTemperature(dailySummary.temp.day),
                    style = MaterialTheme.typography.bodyLarge
                )
                Text(
                    text = "H:${formatTemperature(dailySummary.temp.max)} L:${formatTemperature(dailySummary.temp.min)}",
                    style = MaterialTheme.typography.bodyMedium
                )
            }
            
            CircularScoreIndicator(score = bikeScore)
        }
    }
}

@Composable
fun WeatherInfoItem(label: String, value: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodyMedium
        )
    }
}

@Composable
fun DayDetailScreen(
    dailySummary: DailySummary,
    onBackClick: () -> Unit
) {
    Column(modifier = Modifier.fillMaxSize()) {
        // Custom top bar
        Surface(
            modifier = Modifier.fillMaxWidth(),
            color = MaterialTheme.colorScheme.primaryContainer,
            shadowElevation = 4.dp
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp),
                horizontalArrangement = Arrangement.Start,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = onBackClick) {
                    Icon(
                        Icons.Default.ArrowBack,
                        contentDescription = "Back",
                        tint = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                }
                
                Text(
                    text = formatDate(dailySummary.date),
                    style = MaterialTheme.typography.titleLarge,
                    color = MaterialTheme.colorScheme.onPrimaryContainer,
                    modifier = Modifier
                        .weight(1f)
                        .padding(horizontal = 16.dp),
                    textAlign = TextAlign.Center
                )
                
                // Empty spacer to balance the layout
                Spacer(modifier = Modifier.width(48.dp))
            }
        }

        LazyColumn(
            modifier = Modifier.fillMaxWidth(),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(dailySummary.hourlyForecasts.sortedBy { it.dt }) { hourly ->
                HourlyForecastCard(forecast = hourly)
            }
        }
    }
}

@Composable
fun WeatherScreen(viewModel: WeatherViewModel = viewModel()) {
    val weatherForecast by viewModel.weatherForecast.collectAsState()
    val selectedDay by viewModel.selectedDay.collectAsState()
    val error by viewModel.error.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.fetchWeatherForecast(1.731800, 103.899178)
    }

    Scaffold(modifier = Modifier.fillMaxSize()) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
        ) {
            if (selectedDay != null) {
                // Show hourly details for selected day
                DayDetailScreen(
                    dailySummary = selectedDay!!,
                    onBackClick = { viewModel.selectDay(null) }
                )
            } else {
                // Show 7-day summary
                Text(
                    text = "7-Day Weather Forecast",
                    style = MaterialTheme.typography.headlineMedium,
                    modifier = Modifier.padding(16.dp)
                )

                when {
                    error != null -> {
                        ErrorMessage(error!!)
                    }
                    weatherForecast == null -> {
                        LoadingIndicator()
                    }
                    else -> {
                        LazyColumn(
                            modifier = Modifier.fillMaxWidth(),
                            contentPadding = PaddingValues(16.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            items(weatherForecast!!.dailySummaries) { daily ->
                                DailySummaryCard(
                                    dailySummary = daily,
                                    onClick = { viewModel.selectDay(daily) }
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

private fun formatDate(timestamp: Long): String {
    val sdf = SimpleDateFormat("EEE, MMM d", Locale.getDefault())
    return sdf.format(Date(timestamp * 1000))
}

private fun formatTime(timestamp: Long): String {
    val sdf = SimpleDateFormat("HH:mm", Locale.getDefault())
    return sdf.format(Date(timestamp * 1000))
}

private fun formatTemperature(temp: Double): String {
    return "%.1f°C".format(temp)
}

private fun getTimeOfDay(timestamp: Long): String {
    val hour = SimpleDateFormat("HH", Locale.getDefault())
        .format(Date(timestamp * 1000)).toInt()
    return when {
        hour < 6 -> "Before 6AM"
        hour < 9 -> "Early Morning"
        hour < 12 -> "Morning"
        hour < 15 -> "Afternoon"
        hour < 18 -> "Late Afternoon"
        hour < 24 -> "Evening"
        else -> "Night"
    }
}