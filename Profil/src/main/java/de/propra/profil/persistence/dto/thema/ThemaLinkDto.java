package de.propra.profil.persistence.dto.thema;

import org.springframework.data.relational.core.mapping.Table;

@Table("thema_link")
public record ThemaLinkDto(
        String url,
        String anzeigetext
) {}
