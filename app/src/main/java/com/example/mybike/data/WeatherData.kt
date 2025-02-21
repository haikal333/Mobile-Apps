package com.example.mybike.data

import com.google.gson.annotations.SerializedName

data class WeatherForecast(
    @SerializedName("list")
    val hourlyForecasts: List<DailyForecast>
) {
    // Group forecasts by day and calculate daily averages
    val dailySummaries: List<DailySummary> get() {
        return hourlyForecasts
            .groupBy { formatDateKey(it.dt) }
            .map { (_, forecasts) ->
                DailySummary(
                    date = forecasts.first().dt,
                    temp = Temperature(
                        day = forecasts.map { it.temp.day }.average(),
                        min = forecasts.minOf { it.temp.min },
                        max = forecasts.maxOf { it.temp.max }
                    ),
                    humidity = forecasts.map { it.humidity }.average().toInt(),
                    wind = Wind(
                        speed = forecasts.map { it.wind.speed }.average()
                    ),
                    weather = forecasts.first().weather,
                    hourlyForecasts = forecasts
                )
            }
            .take(7) // Ensure we only show 7 days
    }
}

data class DailySummary(
    val date: Long,
    val temp: Temperature,
    val humidity: Int,
    val wind: Wind,
    val weather: List<Weather>,
    val hourlyForecasts: List<DailyForecast>
)

data class DailyForecast(
    @SerializedName("dt")
    val dt: Long,
    @SerializedName("main")
    val temp: Temperature,
    @SerializedName("humidity")
    val humidity: Int,
    @SerializedName("wind")
    val wind: Wind,
    @SerializedName("weather")
    val weather: List<Weather>
)

data class Temperature(
    @SerializedName("temp")
    val day: Double,
    @SerializedName("temp_min")
    val min: Double,
    @SerializedName("temp_max")
    val max: Double
)

data class Wind(
    @SerializedName("speed")
    val speed: Double
)

data class Weather(
    @SerializedName("main")
    val main: String,
    @SerializedName("description")
    val description: String,
    @SerializedName("icon")
    val icon: String
)

private fun formatDateKey(timestamp: Long): String {
    val sdf = java.text.SimpleDateFormat("yyyy-MM-dd", java.util.Locale.getDefault())
    return sdf.format(java.util.Date(timestamp * 1000))
} 