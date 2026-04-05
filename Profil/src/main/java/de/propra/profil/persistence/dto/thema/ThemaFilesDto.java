package de.propra.profil.persistence.dto.thema;

import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

import java.time.LocalDateTime;

@Table("thema_files")
public record ThemaFilesDto(
        @Id Integer id,
        String name,
        String description,
        String path,
        String type,
        long size,
        LocalDateTime uploadDate
) {}
