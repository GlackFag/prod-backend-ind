package com.glackfag.travelgentle.models;

import jakarta.annotation.Nullable;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Entity
@AllArgsConstructor
@NoArgsConstructor
public class Address {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    private String country;
    private String city;
    @Nullable
    private String street;
    @Nullable
    private String building;

    public Address(String country, String city, @Nullable String street, @Nullable String building) {
        this.country = country;
        this.city = city;
        this.street = street;
        this.building = building;
    }

    public Address(String country, String city) {
        this.country = country;
        this.city = city;
    }
}
