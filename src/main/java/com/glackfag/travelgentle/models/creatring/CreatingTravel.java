package com.glackfag.travelgentle.models.creatring;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
@Table(name = "Creating_Travel")
public class CreatingTravel {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private int id;
    @Column(name = "title")
    private String title;
    @Column(name = "description")
    private String description;
    @Column(name = "organizer_id")
    private long organizerId;
}
