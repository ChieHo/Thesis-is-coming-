package de.propra.profil.persistence.dto.profil;

import org.springframework.data.relational.core.mapping.Table;

import java.time.LocalDateTime;

@Table("profile_files")
public record ProfileFileDto(String name, String description, String path, String type, long size,
                             LocalDateTime uploadDate) {
}
