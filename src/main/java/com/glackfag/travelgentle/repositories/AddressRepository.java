package com.glackfag.travelgentle.repositories;

import com.glackfag.travelgentle.models.Address;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AddressRepository extends JpaRepository<Address, Integer> {
    boolean existsById(int id);
    Optional<Address> findByCountryAndCityAndStreetAndBuilding(String country, String city, String street, String building);

}
