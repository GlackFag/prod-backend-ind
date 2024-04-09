package com.glackfag.travelgentle.models.creatring;

import jakarta.persistence.*;
import lombok.Data;

import java.sql.Date;

@Entity
@Data
@Table(name = "creating_intermediate_point")
public class CreatingPoint {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    @Column(name = "start_date")
    private Date startDate;
    @Column(name = "end_date")
    private Date endDate;
    @Column(name = "travel_id")
    private int travelId;
    @Column(name = "address_id")
    private int addressId;
}
