package net.engineeringdigest.journalApp.service;

import net.engineeringdigest.journalApp.api.response.WeatherResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class WeatherService {

/*   @Value is used in Spring Boot to inject values from external configuration
  (like application.properties) into fields, method parameters, or constructor arguments."
  Uses ${} → for properties
Uses #{} → for expressions (SpEL)
 */
    @Value("${weather.api.key}")
    private  String apiKey;

    private static final String API =
            "https://api.weatherbit.io/v2.0/current?city=CITY&country=IN&key=API_KEY";

    @Autowired
    private RestTemplate restTemplate;

    public WeatherResponse getWeather(String city) {
        String finalAPI = API.replace("CITY", city)
                .replace("API_KEY", apiKey);

        ResponseEntity<WeatherResponse> response =
                restTemplate.exchange(finalAPI, HttpMethod.GET, null, WeatherResponse.class);

        return response.getBody();
    }
}