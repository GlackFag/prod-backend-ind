package com.glackfag.travelgentle.repositories;

import com.glackfag.travelgentle.action.Action;
import com.glackfag.travelgentle.models.Person;
import com.glackfag.travelgentle.models.Travel;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface PersonRepository extends JpaRepository<Person, Long> {
    @Query("SELECT p.lastAction FROM Person p WHERE p.id=:id")
    Action findLastActionById(@Param("id") Long id);

    @Modifying
    @Query("UPDATE Person p SET p.lastAction=:act WHERE p.id=:id")
    void updateLastActionById(@Param("id") Long id, @Param("act") Action action);

    @Modifying
    @Query("UPDATE Person p SET p.name=:name WHERE p.id=:id")
    void updateNameById(@Param("id") Long id, @Param("name") String name);

    @Modifying
    @Query("UPDATE Person p SET p.age=:age WHERE p.id=:id")
    void updateAgeIdById(@Param("id") Long id, @Param("age") int age);

    @Modifying
    @Query("UPDATE Person p SET p.bio=:bio WHERE p.id=:id")
    void updateBioById(@Param("id") Long id, @Param("bio") String bio);

    @Modifying
    @Query("UPDATE Person p SET p.addressId=:addrId WHERE p.id=:id")
    void updateAddressIdById(@Param("id") Long id, @Param("addrId") int addrId);

    Page<Person> findByTookPartInTravelsContaining(Travel travel, Pageable pageable);
}
