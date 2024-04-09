package com.glackfag.travelgentle.repositories;

import com.glackfag.travelgentle.models.IntermediatePoint;
import com.glackfag.travelgentle.models.Travel;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface IntermediatePointRepository extends JpaRepository<IntermediatePoint, Integer> {
    List<IntermediatePoint> findByTravelId(int travelId);
    Page<IntermediatePoint> findByTravelId(int travelId, Pageable pageable);
}
