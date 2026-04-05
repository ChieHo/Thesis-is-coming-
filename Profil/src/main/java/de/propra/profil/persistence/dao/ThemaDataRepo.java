package de.propra.profil.persistence.dao;

import com.tngtech.archunit.lang.syntax.PredicateAggregator;
import de.propra.profil.persistence.dto.profil.ProfilDto;
import de.propra.profil.persistence.dto.thema.ThemaDto;
import org.springframework.data.jdbc.core.mapping.AggregateReference;
import org.springframework.data.repository.CrudRepository;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

public interface ThemaDataRepo extends CrudRepository<ThemaDto, Integer> {
    List<ThemaDto> findAll();

    ThemaDto save(ThemaDto dto);

    Optional<ThemaDto> findById(Integer id);

    List<ThemaDto> findByProfil(AggregateReference<ProfilDto, Integer> profil);

    Optional<ThemaDto> findByFachId(UUID fachId);

    void deleteById(Integer id);



   Optional<ThemaDto> findByTitelAndProfil(String titel, AggregateReference<ProfilDto, Integer> profil);
}
