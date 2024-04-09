package com.glackfag.travelgentle.services;

import com.glackfag.travelgentle.action.Action;
import com.glackfag.travelgentle.models.Person;
import com.glackfag.travelgentle.models.Travel;
import com.glackfag.travelgentle.models.creatring.RegisteringPerson;
import com.glackfag.travelgentle.repositories.PersonRepository;
import org.modelmapper.ModelMapper;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@Transactional(readOnly = true)
public class PersonService {
    private final PersonRepository repository;
    private final ModelMapper modelMapper;
    private final TravelService travelService;

    public PersonService(PersonRepository repository, ModelMapper modelMapper, @Lazy TravelService travelService) {
        this.repository = repository;
        this.modelMapper = modelMapper;
        this.travelService = travelService;
    }

    @Transactional
    public void save(Person person) {
        repository.save(person);
    }

    @Transactional
    public void updateLastActionById(Long id, Action action) {
        repository.updateLastActionById(id, action);
    }

    @Transactional
    public void saveFromRegistrationPerson(RegisteringPerson registeringPerson) {
        save(modelMapper.map(registeringPerson, Person.class));
    }

    public boolean existsById(Long id) {
        return repository.existsById(id);
    }

    public Action findLastActionById(Long id) {
        return repository.findLastActionById(id);
    }

    public Optional<Person> findById(Long id) {
        return repository.findById(id);
    }

    @Transactional
    public void updateNameById(Long id, String name) {
        repository.updateNameById(id, name);
    }

    @Transactional
    public void updateBioById(Long id, String bio) {
        repository.updateBioById(id, bio);
    }

    @Transactional
    public void updateAgeById(Long id, int age){
        repository.updateAgeIdById(id, age);
    }

    @Transactional
    public void updateAddressIdById(Long id, int addressId) {
        repository.updateAddressIdById(id, addressId);
    }

    public Page<Person> findByTravelId(int travelId, int pageNumber) {
        Optional<Travel> optional = travelService.findById(travelId);

        if(optional.isEmpty())
            return Page.empty();

        Pageable pageable = PageRequest.of(pageNumber, 6);

        return repository.findByTookPartInTravelsContaining(optional.get(), pageable);
    }
}
