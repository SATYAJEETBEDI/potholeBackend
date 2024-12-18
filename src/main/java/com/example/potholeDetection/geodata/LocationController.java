package com.example.potholeDetection.geodata;

import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;





@RestController
@RequestMapping("/api/location")
public class LocationController {

    // @Autowired
    private final LocationService locationService;

    public LocationController(LocationService locationService) {
        this.locationService = locationService;
    }

    @PostMapping("/live")
    public void alert() {
        
         locationService.temp();
    }
    
    @GetMapping("/test")
    public String getMethodName() {
        return "deployed successfully";
    }
    

    @PostMapping("/")
    String create(@RequestBody Location location) {
        if(location.getVal()==4)
        return locationService.create(location);
        else
        return locationService.remover(location);
    }

    @GetMapping("/centroid")
    public void centroidAdd() {
        locationService.centroidAdd();
    }
    
    
    
}
