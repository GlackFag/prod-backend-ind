package com.glackfag.travelgentle.repositories.creating;

import com.glackfag.travelgentle.models.creatring.CreatingPoint;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CreatingPointRepository extends JpaRepository<CreatingPoint, Integer> {
    Optional<CreatingPoint> findByTravelId(int id);
    void deleteAllByTravelId(int travelId);
}
