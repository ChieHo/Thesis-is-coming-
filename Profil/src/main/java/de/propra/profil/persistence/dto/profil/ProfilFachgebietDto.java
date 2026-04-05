package de.propra.profil.persistence.dto.profil;


import org.springframework.data.relational.core.mapping.Table;


@Table("profil_fachgebiet")
public record
ProfilFachgebietDto(String name) {

}
