package com.glackfag.travelgentle.services.creating;

import com.glackfag.travelgentle.models.creatring.CreatingTravel;
import com.glackfag.travelgentle.repositories.creating.CreatingTravelRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class CreatingTravelService {
    private final CreatingTravelRepository repository;

    @Autowired
    public CreatingTravelService(CreatingTravelRepository repository) {
        this.repository = repository;
    }

    public CreatingTravel findByOrganizerId(Long id) {
        return repository.findByOrganizerId(id).orElseThrow();
    }

    @Transactional
    public void save(CreatingTravel creatingTravel) {
        repository.save(creatingTravel);
    }

    @Transactional
    public void deleteAllByOrganizerId(Long orgId){
        repository.deleteAllByOrganizerId(orgId);
    }
}
