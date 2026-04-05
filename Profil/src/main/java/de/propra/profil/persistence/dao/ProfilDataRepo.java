package de.propra.profil.persistence.dao;

import de.propra.profil.persistence.dto.profil.ProfilDto;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;
import java.util.Set;
import java.util.UUID;


public interface ProfilDataRepo extends CrudRepository<ProfilDto, Integer> {
    Set<ProfilDto> findAll();

    ProfilDto save(ProfilDto dto);

    Optional<ProfilDto> findByFachId(UUID fachIdProfil);

    void deleteById(Integer dbId);
}
