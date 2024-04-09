package com.glackfag.travelgentle.models.creatring;

import com.glackfag.travelgentle.action.Action;
import jakarta.persistence.*;
import lombok.Data;

/***Класс, используемый для хроанения данных о пользователях, которые начали, но не завершили процесс регистрации*/
@Data
@Entity
@Table(name = "registering_person")
public class RegisteringPerson {
    @Id
    private long id;
    @Column(name = "name")
    private String name;
    @Column(name = "age")
    private int age;
    @Column(name = "address_id")
    private Integer addressId;
    @Column(name = "last_action")
    @Enumerated(value = EnumType.STRING)
    private Action lastAction;

    public RegisteringPerson setId(long id) {
        this.id = id;
        return this;
    }

    public RegisteringPerson setName(String name) {
        this.name = name;
        return this;
    }

    public RegisteringPerson setAge(int age) {
        this.age = age;
        return this;
    }

    public RegisteringPerson setAddressId(int addressId) {
        this.addressId = addressId;
        return this;
    }

    public RegisteringPerson setLastAction(Action lastAction) {
        this.lastAction = lastAction;
        return this;
    }
}
