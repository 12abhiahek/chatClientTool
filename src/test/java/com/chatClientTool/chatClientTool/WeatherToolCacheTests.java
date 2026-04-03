package com.chatClientTool.chatClientTool;

import com.chatClientTool.chatClientTool.tool.Weathertool;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.TestPropertySource;
import org.springframework.web.client.RestTemplate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@SpringBootTest
@TestPropertySource(properties = {
    "weather.api.key=test_key"
})
class WeatherToolCacheTests {

	@Autowired
	private Weathertool weathertool;

	@MockBean
	private RestTemplate restTemplate;

	@Test
	void testWeatherCaching_FirstCallMakesApiRequest() {
		// Mock successful API response
		String mockResponse = """
			{
				"current": {
					"temp_c": 25.5,
					"condition": {
						"text": "Partly cloudy"
					},
					"humidity": 65,
					"wind_kph": 12.5
				}
			}
			""";

		when(restTemplate.getForObject(anyString(), eq(String.class))).thenReturn(mockResponse);

		// First call - should make API request
		String result1 = weathertool.getCurrentWeather("London");

		assertThat(result1).contains("London");
		assertThat(result1).contains("Partly cloudy");
		assertThat(result1).contains("25.5°C");

		// Verify API was called once
		verify(restTemplate, times(1)).getForObject(anyString(), eq(String.class));
	}

	@Test
	void testWeatherCaching_SecondCallUsesCachedData() {
		// Mock successful API response
		String mockResponse = """
			{
				"current": {
					"temp_c": 25.5,
					"condition": {
						"text": "Partly cloudy"
					},
					"humidity": 65,
					"wind_kph": 12.5
				}
			}
			""";

		when(restTemplate.getForObject(anyString(), eq(String.class))).thenReturn(mockResponse);

		// First call - makes API request
		String result1 = weathertool.getCurrentWeather("Paris");

		// Second call - should use cached data
		String result2 = weathertool.getCurrentWeather("Paris");

		// Results should be identical
		assertThat(result1).isEqualTo(result2);

		// Verify API was called only once (not twice)
		verify(restTemplate, times(1)).getForObject(anyString(), eq(String.class));
	}

	@Test
	void testWeatherCaching_DifferentCitiesCallApiMultipleTimes() {
		String mockResponseLondon = """
			{
				"current": {
					"temp_c": 25.5,
					"condition": {"text": "Partly cloudy"},
					"humidity": 65,
					"wind_kph": 12.5
				}
			}
			""";

		String mockResponseTokyo = """
			{
				"current": {
					"temp_c": 28.0,
					"condition": {"text": "Clear"},
					"humidity": 55,
					"wind_kph": 8.5
				}
			}
			""";

		when(restTemplate.getForObject(contains("London"), eq(String.class))).thenReturn(mockResponseLondon);
		when(restTemplate.getForObject(contains("Tokyo"), eq(String.class))).thenReturn(mockResponseTokyo);

		// Call for different cities
		String resultLondon = weathertool.getCurrentWeather("London");
		String resultTokyo = weathertool.getCurrentWeather("Tokyo");

		// Results should be different
		assertThat(resultLondon).contains("Partly cloudy");
		assertThat(resultTokyo).contains("Clear");

		// Verify API was called twice (once per city)
		verify(restTemplate, times(2)).getForObject(anyString(), eq(String.class));
	}

	@Test
	void testWeatherCaching_ErrorResponsesNotCached() {
		String errorResponse = """
			{
				"error": {
					"message": "Invalid city"
				}
			}
			""";

		when(restTemplate.getForObject(anyString(), eq(String.class))).thenReturn(errorResponse);

		// First call - returns error
		String result1 = weathertool.getCurrentWeather("InvalidCity");

		// Second call - should make another API request (not cached because it's an error)
		String result2 = weathertool.getCurrentWeather("InvalidCity");

		assertThat(result1).contains("Error:");
		assertThat(result2).contains("Error:");

		// Verify API was called twice (errors are not cached)
		verify(restTemplate, times(2)).getForObject(anyString(), eq(String.class));
	}
}

