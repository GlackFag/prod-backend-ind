package com.glackfag.travelgentle.services;

import com.glackfag.travelgentle.models.Address;
import com.glackfag.travelgentle.repositories.AddressRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Types;
import java.util.Optional;
import java.util.Random;

@Service
@Transactional(readOnly = true)
public class AddressService {
    private final AddressRepository repository;
    private final JdbcTemplate jdbcTemplate;
    private final Random random = new Random();

    @Autowired
    public AddressService(AddressRepository repository, JdbcTemplate jdbcTemplate) {
        this.repository = repository;
        this.jdbcTemplate = jdbcTemplate;
    }

    @Transactional
    public int save(Address address) {
        String sql = "INSERT INTO address (country, city, street, building) VALUES (?, ?, ?, ?) RETURNING id";
        Object[] args = new Object[]{address.getCountry(), address.getCity(), address.getStreet(), address.getBuilding()};
        int[] argTypes = new int[]{Types.VARCHAR, Types.VARCHAR, Types.VARCHAR, Types.VARCHAR};
        return jdbcTemplate.queryForObject(sql, args, argTypes, Integer.class);
    }

    public Optional<Address> findById(int addressId){
        return repository.findById(addressId);
    }
}

