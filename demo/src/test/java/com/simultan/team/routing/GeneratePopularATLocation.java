package com.simultan.team.routing;

import com.simultan.team.routing.GeneratePopularATLocation.AirportFavLocProperties;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.tuple.Pair;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.context.properties.NestedConfigurationProperty;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.ClassPathResource;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.web.client.RestTemplate;

@SpringBootTest
@ActiveProfiles("test")
@EnableConfigurationProperties(AirportFavLocProperties.class)
public class GeneratePopularATLocation {

  @ConfigurationProperties(prefix = "suggestion")
  public static class AirportFavLocProperties {

    private Map<String, List<FavoriteDestinationProperties>> favoriteDestinations = new HashMap<>();

    public Map<String, List<FavoriteDestinationProperties>> getFavoriteDestinations() {
      return favoriteDestinations;
    }

    public void setFavoriteDestinations(
        Map<String, List<FavoriteDestinationProperties>> favoriteDestinations) {
      this.favoriteDestinations = favoriteDestinations;
    }
  }

  @Getter
  @Setter
  public static class FavoriteDestinationProperties {

    private String id;
    private String name;
    private String address;
    private String locationId;
    private String countryCode;
    @NestedConfigurationProperty
    private Pin pin;
    private double distance;
    private String type;
  }

  @Getter
  @Setter
  public static class Pin {

    private double lat;
    private double lng;

    public Pin() {
    }

    public Pin(double latitude, double longitude) {
      this.lat = latitude;
      this.lng = longitude;
    }

    public String getLagLngString() {
      return lat + "," + lng;
    }
  }

//    @Value("${google.api.key}")
//    private String googleApiKey;

  private final RestTemplate restTemplate = new RestTemplate();

  @Autowired
  AirportFavLocProperties airportFavLocProperties;

  @Test
  void test_aja() {

    String airportsFile = "[AT X MOZIO] Init Master Data - [Properties] Popular Destination.csv";
    List<AirportInfo> airportInfos = readCsv(airportsFile);

    Map<String, List<FavoriteDestinationProperties>> newFavoriteDestinationsProps = new HashMap<>();
    airportFavLocProperties.getFavoriteDestinations()
        .forEach((key, value) -> {
          value.forEach(favoriteDestinationProperties -> {

            if(key.equals("JHB")) {
              System.out.println("abc JHB");
            }

            if(favoriteDestinationProperties.getName().equals("Universal Studios Singapore")) {
              System.out.println("abc");
            }

            if(favoriteDestinationProperties.getName().contains("Senso-ji")) {
              System.out.println("abc");
            }
            AtomicReference<String> name = new AtomicReference<>(
                favoriteDestinationProperties.getName().replace("|", ","));
            airportInfos.stream().filter(airportInfo ->
                airportInfo.getPopularDestination().replace("|", ",")
                    .equalsIgnoreCase(name.get()))
                .findFirst().ifPresent(airportInfo -> {
              if(!airportInfo.getAdjustmentName().isEmpty()) {
                name.set(airportInfo.getAdjustmentName().replace("|", ","));
              }
            });

            LocationData locationData = getLocation(name.get(),
                Objects.nonNull(favoriteDestinationProperties.getPin()) ?
                    favoriteDestinationProperties.getPin().getLagLngString()
                    : new Pin(0, 0).getLagLngString());

            if (Objects.isNull(locationData)) {
              System.out.println("not found -> " + name.get());
            }

            if (Objects.nonNull(locationData)) {
              List<FavoriteDestinationProperties> favList = new ArrayList<>();
              if (newFavoriteDestinationsProps.containsKey(key)) {
                Optional<FavoriteDestinationProperties> favoriteDestPropertiesOptional = value.stream()
                    .filter(newFavDestProp ->
                        newFavDestProp.getName().replace("|", ",")
                            .equalsIgnoreCase(favoriteDestinationProperties.getName().replace("|", ","))).findFirst();
                if (favoriteDestPropertiesOptional.isPresent()) {
                  FavoriteDestinationProperties fav = favoriteDestPropertiesOptional.get();
                  fav.setName(name.get());
                  fav.setAddress(locationData.getAddress());
                  fav.setLocationId(locationData.getPlace_id());
                  favList = new ArrayList<>(newFavoriteDestinationsProps.get(key));
                  favList.add(fav);
                }
              } else {
                Optional<FavoriteDestinationProperties> favoriteDestPropertiesOptional = value.stream()
                    .filter(newFavDestProp ->
                        newFavDestProp.getName().replace("|", ",")
                            .equalsIgnoreCase(favoriteDestinationProperties.getName().replace("|", ","))).findFirst();
                if (favoriteDestPropertiesOptional.isPresent()) {
                  FavoriteDestinationProperties fav = favoriteDestPropertiesOptional.get();
                  fav.setName(name.get());
                  fav.setAddress(locationData.getAddress());
                  fav.setLocationId(locationData.getPlace_id());
                  favList = List.of(fav);
                }
              }

              newFavoriteDestinationsProps.put(key, favList);
            }

          });
        });

    System.out.println("suggestion:\n"
        + "  favoriteDestinations:");
    newFavoriteDestinationsProps.forEach((key, value) -> {
      System.out.println("    " + key + ":");
      value.forEach(favoriteDestinationProperties -> {
        System.out.println("      - id: " + favoriteDestinationProperties.getId());
        System.out.println("        name: " + favoriteDestinationProperties.getName().replace("|", ","));
        System.out.println("        address: " + favoriteDestinationProperties.getAddress());
        System.out.println("        locationId: " + favoriteDestinationProperties.getLocationId());
        System.out.println("        pin:");
        System.out.println("          lat: " + favoriteDestinationProperties.getPin().getLat());
        System.out.println("          lng: " + favoriteDestinationProperties.getPin().getLng());
        System.out.println("        distance: " + favoriteDestinationProperties.getDistance());
        System.out.println("        type: " + favoriteDestinationProperties.getType());
      });
    });
  }

  public LocationData getLocation(String query, String location) {
    System.out.println("getLocation query: " + query);
    String url = String.format(
        "https://maps.googleapis.com/maps/api/place/textsearch/json?query=%s&language=en&components&radius=5000&location=%s&key=%s",
        query, location, "AIzaSyB5l31mhwlB1GQ54_zE3-SAUV0fl11KSxU");

    String response = restTemplate.getForObject(url, String.class);
    JSONObject jsonResponse = null;
    try {
      jsonResponse = new JSONObject(response);
    } catch (JSONException e) {
      throw new RuntimeException(e);
    }
    try {
      if (jsonResponse.getJSONArray("results").length() > 0) {
        return LocationData.builder()
            .name(jsonResponse.getJSONArray("results").getJSONObject(0).getString("name"))
            .place_id(jsonResponse.getJSONArray("results").getJSONObject(0).getString("place_id"))
            .address(jsonResponse.getJSONArray("results").getJSONObject(0)
                .getString("formatted_address"))
            .build();
      }
    } catch (JSONException e) {
      throw new RuntimeException(e);
    }
    return null;
  }


  public Pair<String, String> getPlaceDetail(String placeId) {
    String url = String.format(
        "https://maps.googleapis.com/maps/api/place/details/json?place_id=%s&fields=%s&key=%s",
        placeId, "name,rating,formatted_phone_number,formatted_address,place_id",
        "AIzaSyB5l31mhwlB1GQ54_zE3-SAUV0fl11KSxU");

    String response = restTemplate.getForObject(url, String.class);
    JSONObject jsonResponse = null;
    try {
      jsonResponse = new JSONObject(response);
    } catch (JSONException e) {
      throw new RuntimeException(e);
    }
    try {
      if (Objects.nonNull(jsonResponse.get("result"))) {
        JSONObject hashMap = (JSONObject) jsonResponse.get("result");
        return Pair.of(hashMap.get("name").toString(), hashMap.get("formatted_address").toString());
      }
    } catch (JSONException e) {
      throw new RuntimeException(e);
    }
    return null;
  }

  public List<AirportInfo> readCsv(String fileName) {
    List<AirportInfo> airports = new ArrayList<>();

    ClassPathResource resource = new ClassPathResource(fileName);
    InputStream inputStream = null;
    try {
      inputStream = resource.getInputStream();
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
    try (BufferedReader br = new BufferedReader(new InputStreamReader(inputStream));) {
      String line;
      boolean isFirstLine = true; // Untuk melewati header
      while ((line = br.readLine()) != null) {
        if (isFirstLine) {
          isFirstLine = false;
          continue;
        }

        String[] data = line.split(",");

        String adjustmentName = data.length > 8 ? data[8] : "";
        String devStatus = data.length > 9 ? data[9] : "";
        AirportInfo airport = new AirportInfo(
            data[0], data[1], data[2], data[3],
            Double.parseDouble(data[4]), Double.parseDouble(data[5]),
            Integer.parseInt(data[6]), data[7], adjustmentName, devStatus
        );
        airports.add(airport);
      }
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
    return airports;
  }

  @Setter
  @Getter
  @lombok.Builder(builderClassName = "Builder")
  private static class LocationData {

    String name;
    String place_id;
    String address;
  }

  @Getter
  @Setter
  public class AirportInfo {

    private String countryName;
    private String countryCode;
    private String airportCode;
    private String popularDestination;
    private double latitude;
    private double longitude;
    private int distance;
    private String domesticInternational;
    private String adjustmentName;
    private String devStatus;

    // Constructor
    public AirportInfo(String countryName, String countryCode, String airportCode,
        String popularDestination,
        double latitude, double longitude, int distance, String domesticInternational,
        String adjustmentName, String devStatus) {
      this.countryName = countryName;
      this.countryCode = countryCode;
      this.airportCode = airportCode;
      this.popularDestination = popularDestination;
      this.latitude = latitude;
      this.longitude = longitude;
      this.distance = distance;
      this.domesticInternational = domesticInternational;
      this.adjustmentName = adjustmentName;
      this.devStatus = devStatus;
    }

  }
}