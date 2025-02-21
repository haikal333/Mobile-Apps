package com.example.mybike.data

object BikeRideScore {
    // Temperature ranges (in Celsius)
    private const val IDEAL_TEMP_MIN = 15.0
    private const val IDEAL_TEMP_MAX = 25.0
    private const val ACCEPTABLE_TEMP_MIN = 10.0
    private const val ACCEPTABLE_TEMP_MAX = 30.0

    // Wind speed ranges (in m/s)
    private const val IDEAL_WIND_MAX = 5.0
    private const val ACCEPTABLE_WIND_MAX = 10.0

    // Weather condition scores
    private val weatherScores = mapOf(
        "Clear" to 100,
        "Clouds" to 80,
        "Drizzle" to 40,
        "Rain" to 20,
        "Thunderstorm" to 0,
        "Snow" to 10,
        "Mist" to 50,
        "Fog" to 30
    )

    fun calculateBikeRideScore(forecast: DailyForecast): Int {
        val tempScore = calculateTemperatureScore(forecast.temp.day)
        val windScore = calculateWindScore(forecast.wind.speed)
        val weatherScore = calculateWeatherScore(forecast.weather.firstOrNull()?.main ?: "")
        
        // Weight the different factors
        val weightedScore = (tempScore * 0.4 + // Temperature is very important
                           windScore * 0.3 +   // Wind is quite important
                           weatherScore * 0.3)  // Weather condition is equally important
        
        return weightedScore.toInt().coerceIn(0, 100)
    }

    private fun calculateTemperatureScore(temp: Double): Double {
        return when {
            // Ideal temperature range (100%)
            temp in IDEAL_TEMP_MIN..IDEAL_TEMP_MAX -> 100.0
            
            // Too cold
            temp < ACCEPTABLE_TEMP_MIN -> 0.0
            temp < IDEAL_TEMP_MIN -> {
                val range = IDEAL_TEMP_MIN - ACCEPTABLE_TEMP_MIN
                val diff = temp - ACCEPTABLE_TEMP_MIN
                (diff / range) * 100
            }
            
            // Too hot
            temp > ACCEPTABLE_TEMP_MAX -> 0.0
            temp > IDEAL_TEMP_MAX -> {
                val range = ACCEPTABLE_TEMP_MAX - IDEAL_TEMP_MAX
                val diff = ACCEPTABLE_TEMP_MAX - temp
                (diff / range) * 100
            }
            
            else -> 100.0
        }
    }

    private fun calculateWindScore(windSpeed: Double): Double {
        return when {
            windSpeed <= IDEAL_WIND_MAX -> 100.0
            windSpeed <= ACCEPTABLE_WIND_MAX -> {
                val range = ACCEPTABLE_WIND_MAX - IDEAL_WIND_MAX
                val diff = ACCEPTABLE_WIND_MAX - windSpeed
                (diff / range) * 100
            }
            else -> 0.0
        }
    }

    private fun calculateWeatherScore(weatherMain: String): Double {
        return weatherScores[weatherMain]?.toDouble() ?: 50.0
    }
} 