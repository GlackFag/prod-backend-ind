package com.glackfag.travelgentle.services;

import com.glackfag.travelgentle.models.Person;
import com.glackfag.travelgentle.models.Travel;
import com.glackfag.travelgentle.models.creatring.CreatingTravel;
import com.glackfag.travelgentle.repositories.TravelRepository;
import jakarta.persistence.EntityManager;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Timestamp;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Optional;

@Service
@Transactional(readOnly = true)
public class TravelService {
    private final TravelRepository repository;
    private final PersonService personService;
    private final ModelMapper modelMapper;
    private final ZoneId zoneId;
    private final EntityManager entityManager;

    @Autowired
    public TravelService(TravelRepository repository, PersonService personService, ModelMapper modelMapper, ZoneId zoneId, EntityManager entityManager) {
        this.repository = repository;
        this.personService = personService;
        this.modelMapper = modelMapper;
        this.zoneId = zoneId;
        this.entityManager = entityManager;
    }

    public boolean existsByTitle(String title) {
        return repository.existsByTitle(title);
    }

    @Transactional
    public void save(Travel travel) {
        repository.save(travel);
    }

    @Transactional
    public void saveFromCreating(CreatingTravel creatingTravel) {
        Travel travel = modelMapper.map(creatingTravel, Travel.class);
        Timestamp createdAt = Timestamp.from(ZonedDateTime.now(zoneId).toInstant());

        travel.setCreatedAt(createdAt);

        repository.save(travel);
    }

    public Page<Travel> findAllByPersonId(Long organizerId, int pageNumber) {
        Person org = new Person();
        org.setId(organizerId);

        Pageable pageable = PageRequest.of(pageNumber, 6, Sort.by("createdAt").descending());

        return repository.findAllByOrganizerOrParticipantsContains(org, org, pageable);
    }

    public Optional<Travel> findById(int id) {
        return repository.findById(id);
    }

    public boolean isAccessed(int travelId, Long userId) {
        return repository.existsById(travelId) &&
                (repository.existsByIdAndOrganizerId(travelId, userId) || isParticipant(travelId, userId));
    }

    public boolean isParticipant(int travelId, Long userId) {
        return personService.findById(userId).orElseThrow().getTookPartInTravels()
                .stream().anyMatch(x -> x.getId() == travelId);
    }

    @Transactional
    public void addParticipant(Travel travel, Long newParticipantId) {
        Person person = personService.findById(newParticipantId).orElseThrow();
        entityManager.createNativeQuery("INSERT INTO travel_participant (travel_id, person_id) VALUES (:travelId, :personId)")
                .setParameter("travelId", travel.getId())
                .setParameter("personId", person.getId())
                .executeUpdate();
    }

    @Transactional
    public void deleteById(int id) {
        repository.deleteById(id);
    }
}
