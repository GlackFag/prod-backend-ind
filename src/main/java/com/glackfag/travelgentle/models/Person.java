package com.glackfag.travelgentle.models;

import com.glackfag.travelgentle.action.Action;
import jakarta.persistence.*;
import lombok.Data;

import java.util.List;
import java.util.Objects;
import java.util.Set;

@Data
@Entity
@Table(name = "person")
public class Person {
    @Id
    private long id;
    @Column(name = "name")
    private String name;
    @Column(name = "age")
    private int age;
    @Column(name = "address_id")
    private int addressId;
    @Column(name = "bio")
    private String bio;
    @Column(name = "last_action")
    @Enumerated(value = EnumType.STRING)
    private Action lastAction;

    @OneToMany(mappedBy = "id")
    private List<Travel> organizedTravels;

    @ManyToMany(mappedBy = "participants", fetch = FetchType.EAGER)
    private Set<Travel> tookPartInTravels;

    @Override
    public String toString() {
        return "Person{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", age=" + age +
                ", addressId=" + addressId +
                ", bio='" + bio + '\'' +
                ", lastAction=" + lastAction +
                '}';
    }

    @Override
    public boolean equals(Object object) {
        if (this == object) return true;
        if (object == null || getClass() != object.getClass()) return false;
        Person person = (Person) object;
        return id == person.id && age == person.age && addressId == person.addressId && Objects.equals(name, person.name) && Objects.equals(bio, person.bio) && lastAction == person.lastAction;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, age, addressId, bio, lastAction);
    }
}
