package com.glackfag.travelgentle.services.creating;

import com.glackfag.travelgentle.models.creatring.CreatingPoint;
import com.glackfag.travelgentle.repositories.creating.CreatingPointRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class CreatingPointService {
    private final CreatingPointRepository repository;

    @Autowired
    public CreatingPointService(CreatingPointRepository repository) {
        this.repository = repository;
    }

    @Transactional
    public void save(CreatingPoint point){
        repository.save(point);
    }

    public CreatingPoint findByTravelId(int travelId){
        return repository.findByTravelId(travelId).orElseThrow();
    }
    @Transactional
    public void deleteAllByTravelId(int travelId){
        repository.deleteAllByTravelId(travelId);
    }
}
