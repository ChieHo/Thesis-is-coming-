package de.propra.profil.web.ErrorPages.CustomErrors;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.util.UUID;

@ResponseStatus(value = HttpStatus.NOT_FOUND, reason = "Dieses Profil konnte nicht gefunden werden.")
public class ProfileNotFoundException extends RuntimeException {
    private final UUID profileId;

    public ProfileNotFoundException(UUID profileId) {
        this.profileId = profileId;
    }

    public UUID getProfileId() {
        return profileId;
    }
}
