package de.propra.profil.persistence.dto.thema;

import org.springframework.data.relational.core.mapping.Table;

@Table("thema_fachgebiet")
public record ThemaFachgebietDto(
        String name
) {}
