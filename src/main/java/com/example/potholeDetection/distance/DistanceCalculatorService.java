package com.example.potholeDetection.distance;

import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import com.example.potholeDetection.geodata.Location;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class DistanceCalculatorService {

    @Autowired
    DistanceConfig distanceConfig;

    

public Map<String, Object> getData(Location source, List<Location> destinations) throws Exception {
    String API_KEY = distanceConfig.getAPI_KEY();
    double sourceLat = source.getLatitude();
    double sourceLng = source.getLongitude();

    StringBuilder destinationsPart = new StringBuilder();
    for (Location destination : destinations) {
        if (destinationsPart.length() > 0) {
            destinationsPart.append("|");
        }
        destinationsPart.append(destination.getLatitude()).append(",").append(destination.getLongitude());
    }

    String encodedDestinations = URLEncoder.encode(destinationsPart.toString(), StandardCharsets.UTF_8.toString());

    String url = "https://maps.googleapis.com/maps/api/distancematrix/json?origins=" +
                 sourceLat + "," + sourceLng + "&destinations=" + encodedDestinations +
                 "&key=" + API_KEY;
    
    var request = HttpRequest.newBuilder()
                             .GET()
                             .uri(URI.create(url))
                             .build();
    
    var client = HttpClient.newBuilder().build();
    var response = client.send(request, HttpResponse.BodyHandlers.ofString()).body(); 

    System.out.println("Source: " + sourceLat + "," + sourceLng);
    System.out.println("Destinations: " + destinationsPart);
    JSONParser jp = new JSONParser();
    JSONObject jo = (JSONObject) jp.parse(response);
    JSONArray ja = (JSONArray) jo.get("rows");
    jo = (JSONObject) ja.get(0);
    ja = (JSONArray) jo.get("elements");

    long nearestDistance = Long.MAX_VALUE; // Track the nearest distance
    boolean potholeAhead = false;

    for (int i = 0; i < ja.size(); i++) {
        JSONObject element = (JSONObject) ja.get(i);
        JSONObject distanceElement = (JSONObject) element.get("distance");
        long distance = (long) distanceElement.get("value");
        System.out.println("API Response: " + distance );

        if (distance < 20) {
            potholeAhead = true;
        }
        nearestDistance = Math.min(nearestDistance, distance);
    }

    Map<String, Object> result = new HashMap<>();
    result.put("message", potholeAhead ? "Pothole Ahead" : "No Pothole Ahead");
    result.put("distance", nearestDistance == Long.MAX_VALUE ? null : nearestDistance);
    return result;
}


    public String closePothole(Location source, List<Location> destinations) throws Exception {

        String API_KEY = distanceConfig.getAPI_KEY();
        double sourceLat = source.getLatitude();
        double sourceLng = source.getLongitude();

        StringBuilder destinationsPart = new StringBuilder();
        for (Location destination : destinations) {
            if (destinationsPart.length() > 0) {
                destinationsPart.append("|");
            }
            destinationsPart.append(destination.getLatitude()).append(",").append(destination.getLongitude());
        }

        String encodedDestinations = URLEncoder.encode(destinationsPart.toString(), StandardCharsets.UTF_8.toString());

        String url = "https://maps.googleapis.com/maps/api/distancematrix/json?origins=" +
                     sourceLat + "," + sourceLng + "&destinations=" + encodedDestinations +
                     "&key=" + API_KEY;
        
        var request = HttpRequest.newBuilder()
                                 .GET()
                                 .uri(URI.create(url))
                                 .build();
        
        var client = HttpClient.newBuilder().build();
        var response = client.send(request, HttpResponse.BodyHandlers.ofString()).body(); 

        // System.out.println(response);
        JSONParser jp = new JSONParser();
        JSONObject jo = (JSONObject) jp.parse(response);
        JSONArray ja = (JSONArray) jo.get("rows");
        jo = (JSONObject) ja.get(0);
        ja = (JSONArray) jo.get("elements");

        for (int i = 0; i < ja.size(); i++) {
            JSONObject element = (JSONObject) ja.get(i);
            JSONObject distanceElement = (JSONObject) element.get("distance");
            long distance = (long) distanceElement.get("value");
            if (distance < 2) {
                return "Pothole already exists";
            } 
        }

        return "No similar entry found";
    }


}