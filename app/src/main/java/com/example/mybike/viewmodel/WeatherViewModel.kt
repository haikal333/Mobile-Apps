package com.example.mybike.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mybike.data.WeatherForecast
import com.example.mybike.data.DailySummary
import com.example.mybike.network.WeatherService
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class WeatherViewModel : ViewModel() {
    private val API_KEY = "b0712f00cdec530f2c50ce238b6850ab" // Replace with your new API key
    
    private val weatherService = Retrofit.Builder()
        .baseUrl("https://api.openweathermap.org/")
        .addConverterFactory(GsonConverterFactory.create())
        .client(OkHttpClient.Builder()
            .addInterceptor(HttpLoggingInterceptor().apply {
                level = HttpLoggingInterceptor.Level.BODY
            })
            .build())
        .build()
        .create(WeatherService::class.java)

    private val _weatherForecast = MutableStateFlow<WeatherForecast?>(null)
    val weatherForecast: StateFlow<WeatherForecast?> = _weatherForecast

    private val _selectedDay = MutableStateFlow<DailySummary?>(null)
    val selectedDay: StateFlow<DailySummary?> = _selectedDay

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error

    fun fetchWeatherForecast(lat: Double, lon: Double) {
        viewModelScope.launch {
            try {
                Log.d("WeatherViewModel", "Fetching weather for lat: $lat, lon: $lon")
                _error.value = null
                val forecast = weatherService.getWeatherForecast(
                    lat = 1.731800,
                    lon = 103.899178,
                    apiKey = API_KEY  // Use the API key constant
                )
                Log.d("WeatherViewModel", "Weather fetch successful: $forecast")
                _weatherForecast.value = forecast
            } catch (e: Exception) {
                Log.e("WeatherViewModel", "Error fetching weather", e)
                _error.value = e.message ?: "An error occurred"
            }
        }
    }

    fun selectDay(day: DailySummary?) {
        _selectedDay.value = day
    }
} 