package com.chatClientTool.chatClientTool.tool;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
public class Weathertool {

    private static final Logger logger = LoggerFactory.getLogger(Weathertool.class);

    @Value("${weather.api.key}")
    private String weatherapikey;

    @Autowired
    private RestTemplate restTemplate;

    @Tool(description = "Get the current weather for a given location")
    @Cacheable(value = "weatherCache", key = "#city", unless = "#result.contains('Error')")
    public String getCurrentWeather(String city) {
        logger.info("🌍 getCurrentWeather tool called for city: {} - Making API call (not from cache)", city);

        if (weatherapikey == null || weatherapikey.isEmpty()) {
            logger.error("Weather API key is not configured");
            return "Error: Weather API key is not configured.";
        }

        try {
            String url = "http://api.weatherapi.com/v1/current.json?key=" + weatherapikey + "&q=" + city;
            logger.info("Making API request to: {}", url.replace(weatherapikey, "***"));

            String response = restTemplate.getForObject(url, String.class);
            logger.info("Received API response for city: {}", city);

            // Parse JSON response
            ObjectMapper mapper = new ObjectMapper();
            JsonNode root = mapper.readTree(response);

            // Check for API errors
            if (root.has("error")) {
                String errorMessage = root.path("error").path("message").asText();
                logger.error("Weather API error for city {}: {}", city, errorMessage);
                return "Error: " + errorMessage;
            }

            // Extract weather data
            JsonNode current = root.path("current");
            double tempC = current.path("temp_c").asDouble();
            String condition = current.path("condition").path("text").asText();
            int humidity = current.path("humidity").asInt();
            double windKph = current.path("wind_kph").asDouble();

            String weatherInfo = String.format(
                "The current weather in %s is %s with a temperature of %.1f°C, humidity %d%%, and wind speed %.1f km/h.",
                city, condition, tempC, humidity, windKph
            );

            logger.info("✅ Weather data retrieved successfully for {}: {} (Cached for future requests)", city, weatherInfo);
            return weatherInfo;

        } catch (Exception e) {
            logger.error("Error fetching weather for city {}: {}", city, e.getMessage(), e);
            return "Error: Unable to fetch weather data. Please check the city name and try again.";
        }
    }
}
