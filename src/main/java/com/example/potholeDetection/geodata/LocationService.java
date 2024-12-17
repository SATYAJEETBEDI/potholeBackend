package com.example.potholeDetection.geodata;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.example.potholeDetection.clustering.Centroid;
import com.example.potholeDetection.clustering.CentroidRepository;
import com.example.potholeDetection.clustering.KMeansClustering;
import com.example.potholeDetection.distance.DistanceCalculatorService;

import weka.classifiers.evaluation.output.prediction.Null;

@Service
public class LocationService {

    private final LocationRepository locationRepository;
    private final KMeansClustering kMeansClustering;
    private final CentroidRepository centroidRepository;
    private final DistanceCalculatorService distanceCalculatorService;
    
    private final int k = 2;


    public LocationService(LocationRepository locationRepository, KMeansClustering kMeansClustering, CentroidRepository centroidRepository, DistanceCalculatorService distanceCalculatorService) {
        this.locationRepository = locationRepository;
        this.kMeansClustering = kMeansClustering;
        this.centroidRepository = centroidRepository;
        this.distanceCalculatorService = distanceCalculatorService;
    }


    
    public List<Location> getAllLocations() {
        return locationRepository.findAll();
    }
    public String remover(Location location){

        Optional<Location> optionalLocation=locationRepository.findByLatitudeAndLongitude(location.getLatitude(), location.getLongitude());
        if(optionalLocation==null){
            return "nothing";
        }
        Location toRemoved=optionalLocation.get();
        Integer newval=toRemoved.getVal()-1;
        if(newval<=0){
            locationRepository.deleteByLatitudeAndLongitude(toRemoved.getLatitude(),toRemoved.getLongitude())  ;
            return "pothole removed";
        }
        toRemoved.setVal(newval);
        locationRepository.save(toRemoved);
        return "val updated";
    }

    public String create(Location location) {

        List<Location> allLocations = locationRepository.findAll();
        final int BATCH_SIZE = 25;    
        for (int i = 0; i < allLocations.size(); i += BATCH_SIZE) {
            int end = Math.min(i + BATCH_SIZE, allLocations.size());
            List<Location> batch = allLocations.subList(i, end);
            try {
                String batchResponse = distanceCalculatorService.closePothole(location, batch);
                if (batchResponse=="Pothole already exists") {
                    return batchResponse; 
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    
        locationRepository.save(location);  
        // locationRepository.deleteAll();
        return "Pothole added";

    }

    public void centroidAdd() {

        try {
            List<Centroid> centroids=kMeansClustering.findCentroids(getAllLocations(), k);
            centroidRepository.deleteAll();
            for(Centroid centroid:centroids){
                centroidRepository.save(centroid);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

}
