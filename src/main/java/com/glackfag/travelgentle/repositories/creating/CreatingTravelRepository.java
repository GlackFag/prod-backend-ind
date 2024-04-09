package com.glackfag.travelgentle.repositories.creating;

import com.glackfag.travelgentle.models.creatring.CreatingTravel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CreatingTravelRepository extends JpaRepository<CreatingTravel, Integer> {
    Optional<CreatingTravel> findByOrganizerId(Long orgId);
    void deleteAllByOrganizerId(Long orgId);
}
