package com.glackfag.travelgentle.repositories.creating;

import com.glackfag.travelgentle.action.Action;
import com.glackfag.travelgentle.models.creatring.RegisteringPerson;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface RegisteringPersonRepository extends JpaRepository<RegisteringPerson, Long> {
    @Query("SELECT r.lastAction FROM RegisteringPerson r WHERE r.id=:id")
    Action findLastActionById(@Param("id") Long id);

    @Modifying
    @Query("UPDATE RegisteringPerson r SET r.lastAction=:act WHERE r.id=:id")
    void updateLastActionById(@Param("id") Long id, @Param("act") Action action);

    @Modifying
    @Query("UPDATE RegisteringPerson r SET r.addressId=:adrId WHERE r.id=:id")
    void updateAddressById(@Param("id") Long id, @Param("adrId") int addressId);
}
