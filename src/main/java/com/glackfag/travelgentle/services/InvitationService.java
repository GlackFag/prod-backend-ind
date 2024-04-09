package com.glackfag.travelgentle.services;

import com.glackfag.travelgentle.models.Invitation;
import com.glackfag.travelgentle.models.Travel;
import com.glackfag.travelgentle.repositories.InvitationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.SecureRandom;
import java.sql.Timestamp;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Optional;

@Service
@Transactional(readOnly = true)
public class InvitationService {
    private final InvitationRepository repository;
    private final ZoneId zoneId;
    private final SecureRandom random = new SecureRandom();
    private static final String ALLOWED_CHARACTERS = "0123456789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ";

    @Autowired
    public InvitationService(ZoneId zoneId, InvitationRepository repository) {
        this.zoneId = zoneId;
        this.repository = repository;
    }

    @Transactional
    public String generate(Travel travel) {
        Timestamp expiresAt = Timestamp.from(ZonedDateTime.now(zoneId).plusHours(12).toInstant());
        String code = generateCode();

        Invitation invitation = new Invitation(code, 1, expiresAt, travel);

        repository.save(invitation);

        return code;
    }

    @Transactional
    public Optional<Travel> useInvitation(String code) {
        Optional<Invitation> optional = repository.findById(code);

        if (optional.isEmpty())
            return Optional.empty();

        Invitation invitation = optional.get();
        Timestamp now = Timestamp.from(ZonedDateTime.now(zoneId).toInstant());

        if (invitation.getExpiresAt().before(now)) {
            repository.delete(invitation);
            return Optional.empty();
        }

        int lastUses = invitation.getUsesLast();

        if (lastUses < 1) {
            repository.delete(invitation);
            return Optional.empty();
        }

        if (lastUses == 1)
            repository.delete(invitation);
        else
            invitation.setUsesLast(lastUses - 1);

        return Optional.of(invitation.getTravel());
    }


    private String generateCode() {
        int length = 25;
        String code;

        do {
            StringBuilder sb = new StringBuilder(length);
            for (int i = 0; i < length; i++) {
                int randomIndex = random.nextInt(ALLOWED_CHARACTERS.length());
                char randomChar = ALLOWED_CHARACTERS.charAt(randomIndex);
                sb.append(randomChar);
            }

            code = sb.toString();
        } while (repository.existsById(code));

        return code;
    }
}
