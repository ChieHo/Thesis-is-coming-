package de.propra.profil.persistence.dto.thema;

import de.propra.profil.persistence.dto.profil.ProfilDto;
import org.springframework.data.annotation.Id;
import org.springframework.data.jdbc.core.mapping.AggregateReference;
import org.springframework.data.relational.core.mapping.Table;

import java.util.Set;
import java.util.UUID;

@Table("thema")
public record ThemaDto(
        @Id Integer id,
        UUID fachId,
        String titel,
        String beschreibung,
        AggregateReference<ProfilDto, Integer> profil,
        Set<ThemaFachgebietDto> themaFachgebiet,
        Set<ThemaLinkDto> themaLink,
        Set<ThemaFilesDto> themaFiles,
        Set<ThemaVoraussetzungDto> themaVoraussetzung
) {}

