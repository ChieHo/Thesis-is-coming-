package de.propra.profil.persistence;

import de.propra.profil.application.exception.NichtVorhandenException;
import de.propra.profil.application.repository.ThemaRepository;
import de.propra.profil.domain.model.profil.Fachgebiet;
import de.propra.profil.domain.model.profil.File;
import de.propra.profil.domain.model.profil.Link;
import de.propra.profil.domain.model.thema.Thema;
import de.propra.profil.domain.model.thema.Voraussetzung;
import de.propra.profil.persistence.dao.ProfilDataRepo;
import de.propra.profil.persistence.dao.ThemaDataRepo;
import de.propra.profil.persistence.dto.profil.ProfilDto;
import de.propra.profil.persistence.dto.thema.*;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import org.springframework.data.jdbc.core.mapping.AggregateReference;
import org.springframework.stereotype.Repository;

import java.nio.file.Path;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Repository
public class ThemaRepositoryImpl implements ThemaRepository {
    private final ThemaDataRepo repo;
    private final ProfilDataRepo profilDataRepo;

    @SuppressFBWarnings(value = "EI_EXPOSE_REP2", justification = "ArchUnit tests fail when autowired")
    public ThemaRepositoryImpl(ThemaDataRepo repo, ProfilDataRepo profilDataRepo) {
        this.repo = repo;
        this.profilDataRepo = profilDataRepo;
    }

    @Override
    public List<Thema> findAll() {
        return repo.findAll().stream().map(this::toThema).collect(Collectors.toList());
    }

    @Override
    public Optional<Thema> findById(UUID fachId) {
        return repo.findByFachId(fachId).map(this::toThema);
    }

    @Override
    public List<Thema> findByProfilId(UUID profilFachId) {
        Integer dbId = profilDataRepo.findByFachId(profilFachId)
                .map(ProfilDto::id)
                .orElseThrow(NichtVorhandenException::new);
        return repo.findByProfil(AggregateReference.to(dbId))
                .stream()
                .map(this::toThema)
                .collect(Collectors.toList());
    }

    @Override
    public void save(Thema thema, UUID profilFachId) {
        Integer profilDbId = profilDataRepo.findByFachId(profilFachId)
                .map(ProfilDto::id)
                .orElseThrow(NichtVorhandenException::new);
        Integer existingDbKey = repo.findByFachId(thema.getFachId()).map(ThemaDto::id).orElse(null);
        ThemaDto dto = toThemaDto(existingDbKey, thema, profilDbId);
        repo.save(dto);
    }

    @Override
    public void deleteById(Integer dbId) {
        repo.deleteById(dbId);
    }

    @Override
    public Integer getDbId(UUID fachId) {
        return repo.findByFachId(fachId).map(ThemaDto::id).orElseThrow(NichtVorhandenException::new);
    }
    @Override
    public UUID getProfilFachId(UUID themaFachId) {
        ThemaDto dto = repo.findByFachId(themaFachId)
                .orElseThrow(NichtVorhandenException::new);
        Integer profilDbId = dto.profil().getId();
        return profilDataRepo.findById(profilDbId)
                .map(ProfilDto::fachId)
                .orElseThrow(NichtVorhandenException::new);
    }
    @Override
    public Optional<Thema> findByTitelAndProfilId(String titel, UUID profilFachId) {
        Integer profilDbId = profilDataRepo.findByFachId(profilFachId)
                .map(ProfilDto::id)
                .orElseThrow(NichtVorhandenException::new);
        return repo.findByTitelAndProfil(titel, AggregateReference.to(profilDbId))
                .map(this::toThema);
    }

    private Thema toThema(ThemaDto dto) {
        return new Thema(
                dto.fachId(),
                dto.titel(),
                dto.beschreibung(),
                toFachgebiete(dto.themaFachgebiet()),
                toLinks(dto.themaLink()),
                toThemaFiles(dto.themaFiles()),
                toVoraussetzungen(dto.themaVoraussetzung())
        );
    }

    private Set<Fachgebiet> toFachgebiete(Set<ThemaFachgebietDto> dtos) {
        return dtos.stream().map(d -> new Fachgebiet(d.name())).collect(Collectors.toSet());
    }

    private Set<Link> toLinks(Set<ThemaLinkDto> dtos) {
        return dtos.stream().map(d -> new Link(d.url(), d.anzeigetext())).collect(Collectors.toSet());
    }

    private Set<File> toThemaFiles(Set<ThemaFilesDto> dtos) {
        return dtos.stream()
                .map(d -> new File(d.name(), d.description(), Path.of(d.path()), d.type(), d.size(), d.uploadDate()))
                .collect(Collectors.toSet());
    }

    private Set<Voraussetzung> toVoraussetzungen(Set<ThemaVoraussetzungDto> dtos) {
        return dtos.stream().map(d -> new Voraussetzung(d.voraussetzung())).collect(Collectors.toSet());
    }

    private ThemaDto toThemaDto(Integer existingDbKey, Thema thema, Integer profilDbId) {
        UUID fachId = thema.getFachId() != null ? thema.getFachId() : UUID.randomUUID();
        return new ThemaDto(
                existingDbKey,
                fachId,
                thema.getTitel(),
                thema.getBeschreibung(),
                AggregateReference.to(profilDbId),
                toFachgebieteDto(thema.getFachgebiete()),
                toLinksDto(thema.getLinks()),
                toThemaFilesDto(thema.getThemaFiles()),
                toVoraussetzungenDto(thema.getVoraussetzungen())
        );
    }

    private Set<ThemaFachgebietDto> toFachgebieteDto(Set<Fachgebiet> fachgebiete) {
        return fachgebiete.stream().map(f -> new ThemaFachgebietDto(f.getName())).collect(Collectors.toSet());
    }

    private Set<ThemaLinkDto> toLinksDto(Set<Link> links) {
        return links.stream().map(l -> new ThemaLinkDto(l.getUrl(), l.getAnzeigetext())).collect(Collectors.toSet());
    }

    private ThemaFilesDto toThemaFileDto(File file) {
        return new ThemaFilesDto(null, file.getName(), file.getDescription(), file.getPath().toString(), file.getType(), file.getSize(), file.getUploadDate());
    }

    private Set<ThemaFilesDto> toThemaFilesDto(Set<File> files) {
        return files.stream().map(this::toThemaFileDto).collect(Collectors.toSet());
    }

    private Set<ThemaVoraussetzungDto> toVoraussetzungenDto(Set<Voraussetzung> voraussetzungen) {
        return voraussetzungen.stream().map(v -> new ThemaVoraussetzungDto(v.getVoraussetzung())).collect(Collectors.toSet());
    }
}