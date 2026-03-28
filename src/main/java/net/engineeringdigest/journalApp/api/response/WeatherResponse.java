package net.engineeringdigest.journalApp.api.response;

import lombok.Getter;
import lombok.Setter;
import java.util.List;

// @JsonProperty: to map the json property to java field if they are different
@Getter
@Setter
public class WeatherResponse {

    private List<Data> data;

    @Getter
    @Setter
    public static class Data {
        private double temp;
        private double app_temp;
        private String city_name;
        private Weather weather;
    }

    @Getter
    @Setter
    public static class Weather {
        private String description;
    }
}