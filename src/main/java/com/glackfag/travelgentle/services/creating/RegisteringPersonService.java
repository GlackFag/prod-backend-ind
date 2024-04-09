package com.glackfag.travelgentle.services.creating;

import com.glackfag.travelgentle.action.Action;
import com.glackfag.travelgentle.models.creatring.RegisteringPerson;
import com.glackfag.travelgentle.repositories.creating.RegisteringPersonRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@Transactional(readOnly = true)
public class RegisteringPersonService {
    private final RegisteringPersonRepository repository;
    @Autowired
    public RegisteringPersonService(RegisteringPersonRepository repository) {
        this.repository = repository;
    }

    @Transactional
    public void save(RegisteringPerson person) {
        repository.save(person);
    }

    @Transactional
    public void updateLastActionById(Long id, Action action){
        repository.updateLastActionById(id, action);
    }
    @Transactional
    public void updateAddressById(Long id, int addressId){
        repository.updateAddressById(id, addressId);
    }

    @Transactional
    public void deleteById(Long id){
        repository.deleteById(id);
    }

    public Optional<RegisteringPerson> findById(Long id){
        return repository.findById(id);
    }

    public boolean existsById(Long id) {
        return repository.existsById(id);
    }

    public Action findLastActionById(Long id) {
        return repository.findLastActionById(id);
    }
}
