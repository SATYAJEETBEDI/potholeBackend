package com.example.potholeDetection.geodata;

// import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;


@Repository
public interface LocationRepository extends JpaRepository<Location, Long>{

    Optional<Location> findByLatitudeAndLongitude(double latitude, double longitude);
    void deleteByLatitudeAndLongitude(double latitude, double longitude);
}