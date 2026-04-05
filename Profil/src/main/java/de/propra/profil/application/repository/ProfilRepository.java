package de.propra.profil.application.repository;

import de.propra.profil.domain.model.profil.Profil;

import java.util.Collection;
import java.util.Optional;
import java.util.UUID;

public interface ProfilRepository {
    Collection<Profil> findAll();

    Optional<Profil> findById(UUID id);

    void save(Profil profil);

    Integer getDbId(UUID id);

    Profil findByDbId(Integer dbId);

    void deleteById(Integer dbId);
}

