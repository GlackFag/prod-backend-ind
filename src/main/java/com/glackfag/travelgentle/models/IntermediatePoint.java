package com.glackfag.travelgentle.models;

import jakarta.persistence.*;
import lombok.Data;

import java.sql.Date;

@Data
@Entity
@Table(name = "intermediate_point")
public class IntermediatePoint implements Comparable<IntermediatePoint>{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    @Column(name = "start_date")
    private Date startDate;
    @Column(name = "end_date")
    private Date endDate;
    @ManyToOne
    @JoinColumn(name = "travel_id")
    private Travel travel;

    @OneToOne
    @JoinColumn(name = "address_id")
    private Address address;

    @Override
    public int compareTo(IntermediatePoint o) {
        return this.startDate.compareTo(o.getStartDate());
    }
}

