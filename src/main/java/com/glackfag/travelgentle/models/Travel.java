package com.glackfag.travelgentle.models;

import jakarta.persistence.*;
import lombok.Data;

import java.sql.Timestamp;
import java.util.List;
import java.util.Set;

@Data
@Entity
@Table(name = "travel")
public class Travel {
    @Id
    @Column(name = "id")
    private int id;
    @Column(name = "title")
    private String title;
    @Column(name = "description")
    private String description;
    @Column(name = "created_at")
    private Timestamp createdAt;
    @ManyToOne
    @JoinColumn(name = "organizer_id")
    private Person organizer;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "travel_participant",
            joinColumns = @JoinColumn(name = "travel_id"),
            inverseJoinColumns = @JoinColumn(name = "person_id"))
    private Set<Person> participants;

    @OneToMany(mappedBy = "travel")
    private List<IntermediatePoint> points;

    @Override
    public String toString() {
        return "Travel{" +
                "id=" + id +
                ", title='" + title + '\'' +
                ", description='" + description + '\'' +
                ", createdAt=" + createdAt +
                ", organizer=" + organizer +
                ", participants=" + participants +
                '}';
    }
}
