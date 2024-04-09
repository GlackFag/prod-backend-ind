package com.glackfag.travelgentle.repositories;

import com.glackfag.travelgentle.models.Invitation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface InvitationRepository extends JpaRepository<Invitation, String> {
}
