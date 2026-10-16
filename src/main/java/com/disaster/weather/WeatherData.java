package com.disaster.weather;

public class WeatherData {

    private final String location;
    private final double temperature;
    private final double humidity;
    private final double precipitation;
    private final double windSpeed;
    private final int weatherCode;
    private final String time;

    public WeatherData(
            String location,
            double temperature,
            double humidity,
            double precipitation,
            double windSpeed,
            int weatherCode,
            String time
    ) {
        this.location = location;
        this.temperature = temperature;
        this.humidity = humidity;
        this.precipitation = precipitation;
        this.windSpeed = windSpeed;
        this.weatherCode = weatherCode;
        this.time = time;
    }

    public String getLocation() {
        return location;
    }

    public double getTemperature() {
        return temperature;
    }

    public double getHumidity() {
        return humidity;
    }

    public double getPrecipitation() {
        return precipitation;
    }

    public double getWindSpeed() {
        return windSpeed;
    }

    public int getWeatherCode() {
        return weatherCode;
    }

    public String getTime() {
        return time;
    }

    @Override
    public String toString() {

        return "WeatherData{" +
                "location='" + location + '\'' +
                ", temperature=" + temperature +
                ", humidity=" + humidity +
                ", precipitation=" + precipitation +
                ", windSpeed=" + windSpeed +
                ", weatherCode=" + weatherCode +
                ", time='" + time + '\'' +
                '}';
    }
}