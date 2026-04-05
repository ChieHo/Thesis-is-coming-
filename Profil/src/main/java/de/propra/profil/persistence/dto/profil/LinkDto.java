package de.propra.profil.persistence.dto.profil;

import org.springframework.data.relational.core.mapping.Table;

@Table("link")
public record LinkDto(String url, String anzeigetext) {}
