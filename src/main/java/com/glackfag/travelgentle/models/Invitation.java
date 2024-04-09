package com.glackfag.travelgentle.models;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;

@Data
@Entity
@Table(name = "invitation")
@NoArgsConstructor
@AllArgsConstructor
public class Invitation {
    @Id
    private String code;
    @Column(name = "uses_last")
    private int usesLast;
    @Column(name = "expires_at")
    private Timestamp expiresAt;
    @ManyToOne
    @JoinColumn(name = "travel_id")
    private Travel travel;
}
