package com.glackfag.travelgentle.repositories;

import com.glackfag.travelgentle.models.Person;
import com.glackfag.travelgentle.models.Travel;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TravelRepository extends JpaRepository<Travel, Integer> {
    boolean existsByTitle(String title);

    Page<Travel> findAllByOrganizerOrParticipantsContains(Person org, Person part, Pageable pageable);
    boolean existsByIdAndOrganizerId(int travelId, Long organizerId);
}
