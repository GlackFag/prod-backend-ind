package com.glackfag.travelgentle.services;

import com.glackfag.travelgentle.models.IntermediatePoint;
import com.glackfag.travelgentle.models.creatring.CreatingPoint;
import com.glackfag.travelgentle.repositories.IntermediatePointRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional(readOnly = true)
public class IntermediatePointService {
    private final IntermediatePointRepository repository;
    private final TravelService travelService;
    private final ModelMapper modelMapper;

    @Autowired
    public IntermediatePointService(IntermediatePointRepository repository, TravelService travelService, ModelMapper modelMapper) {
        this.repository = repository;
        this.travelService = travelService;
        this.modelMapper = modelMapper;
    }


    @Transactional
    public void saveFromCreating(CreatingPoint creatingPoint){
        repository.save(modelMapper.map(creatingPoint, IntermediatePoint.class));
    }
    public Optional<IntermediatePoint> findById(int id){
        return repository.findById(id);
    }

    public List<IntermediatePoint> findByTravelId(int travelId){
        return repository.findByTravelId(travelId);
    }

    public Page<IntermediatePoint> findByTravelId(int travelId, int pageNumber){
        Pageable pageable = PageRequest.of(pageNumber, 6, Sort.by("startDate"));

        return repository.findByTravelId(travelId, pageable);
    }

    public boolean isAccessed(Long userId, int pointId){
        IntermediatePoint point = repository.findById(pointId).orElseThrow();

        return travelService.isAccessed(point.getTravel().getId(), userId);
    }
}
