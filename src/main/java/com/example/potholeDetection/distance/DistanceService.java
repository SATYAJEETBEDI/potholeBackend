package com.example.potholeDetection.distance;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.example.potholeDetection.geodata.Location;
import com.example.potholeDetection.geodata.LocationRepository;

@Service
public class DistanceService {

    DistanceCalculatorService distanceCalculatorService;
    LocationRepository locationRepository;


    public DistanceService(DistanceCalculatorService distanceCalculatorService,LocationRepository locationRepository) {
        this.distanceCalculatorService = distanceCalculatorService;
        this.locationRepository = locationRepository;
    }

    


    
    public ResponseEntity<Map<String, Object>> calculate(Location source) {
        List<Location> allLocations = locationRepository.findAll();
        final int BATCH_SIZE = 25;
        Map<String, Object> response = new HashMap<>();
        response.put("message", "No Pothole Ahead");
        response.put("distance", null); // Default value for no pothole
    
        for (int i = 0; i < allLocations.size(); i += BATCH_SIZE) {
            int end = Math.min(i + BATCH_SIZE, allLocations.size());
            List<Location> batch = allLocations.subList(i, end);
            try {
                Map<String, Object> batchResponse = distanceCalculatorService.getData(source, batch);
                if ("Pothole Ahead".equals(batchResponse.get("message"))) {
                    response.put("message", "Pothole Ahead");
                    response.put("distance", batchResponse.get("distance"));
                    return new ResponseEntity<>(response, HttpStatus.OK);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
    
    
}
