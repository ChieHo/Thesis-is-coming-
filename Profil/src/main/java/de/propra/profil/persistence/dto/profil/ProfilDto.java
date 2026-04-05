package de.propra.profil.persistence.dto.profil;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

import java.util.Set;
import java.util.UUID;

@Table("profil")
@SuppressFBWarnings(value = "EI_EXPOSE_REP", justification = "Not an issue as this class is only used for database storage and retrieval")
public record ProfilDto(@Id Integer id,
                        UUID fachId,
                        String name,
                        String email,
                        String githubLogin,
                        Set<LinkDto> links,
                        Set<ProfilFachgebietDto> profilFachGebiet,
                        Set<ProfileFileDto> profileFiles) {
}
