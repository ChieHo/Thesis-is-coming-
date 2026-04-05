package de.propra.profil.application.repository;

import de.propra.profil.domain.model.thema.Thema;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ThemaRepository {
    List<Thema> findAll();

    Optional<Thema> findById(UUID fachId);

    List<Thema> findByProfilId(UUID profilFachId);

    void save(Thema thema, UUID profilFachId);

    Integer getDbId(UUID fachId);

    void deleteById(Integer dbId);

    UUID getProfilFachId(UUID themaFachId);
    Optional<Thema> findByTitelAndProfilId(String titel, UUID profilFachId);
}