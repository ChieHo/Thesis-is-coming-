package de.propra.profil.persistence;

import de.propra.profil.application.exception.NichtVorhandenException;
import de.propra.profil.application.repository.ProfilRepository;
import de.propra.profil.domain.model.profil.Fachgebiet;
import de.propra.profil.domain.model.profil.File;
import de.propra.profil.domain.model.profil.Link;
import de.propra.profil.domain.model.profil.Profil;
import de.propra.profil.persistence.dao.ProfilDataRepo;
import de.propra.profil.persistence.dto.profil.ProfilFachgebietDto;
import de.propra.profil.persistence.dto.profil.LinkDto;
import de.propra.profil.persistence.dto.profil.ProfilDto;
import de.propra.profil.persistence.dto.profil.ProfileFileDto;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import org.springframework.stereotype.Repository;

import java.nio.file.Path;
import java.util.Collection;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Repository
public class ProfilRepositoryImpl implements ProfilRepository {
    private final ProfilDataRepo repo;

    @SuppressFBWarnings(value = "EI_EXPOSE_REP2", justification = "ArchUnit tests fail when autowired")
    public ProfilRepositoryImpl(ProfilDataRepo repo) {
        this.repo = repo;
    }

    @Override
    public Collection<Profil> findAll() {
        return repo.findAll().stream().map(this::toProfil).collect(Collectors.toSet());
    }

    private Profil toProfil(ProfilDto dto) {
        return new Profil(dto.fachId(), dto.name(), dto.email(), dto.githubLogin(), toLinks(dto.links()), toFachgebiete(dto.profilFachGebiet()), toProfileFiles(dto.profileFiles()));
    }

    private Set<Fachgebiet> toFachgebiete(Set<ProfilFachgebietDto> fachgebiete) {
        return fachgebiete.stream().map(this::toFachgebiet).collect(Collectors.toSet());
    }

    private Fachgebiet toFachgebiet(ProfilFachgebietDto fachgebietDto) {
        return new Fachgebiet(fachgebietDto.name());
    }

    private Set<Link> toLinks(Set<LinkDto> links) {
        return links.stream().map(this::toLink).collect(Collectors.toSet());
    }

    private Link toLink(LinkDto linkDto) {
        return new Link(linkDto.url(), linkDto.anzeigetext());
    }

    private File toProfileFile(ProfileFileDto profileFileDto) {
        return new File(profileFileDto.name(), profileFileDto.description(), Path.of(profileFileDto.path()), profileFileDto.type(), profileFileDto.size(), profileFileDto.uploadDate());
    }

    private Set<File> toProfileFiles(Set<ProfileFileDto> profileFileDtos) {
        return profileFileDtos.stream().map(this::toProfileFile).collect(Collectors.toSet());
    }

    @Override
    public Optional<Profil> findById(UUID id) {
        return repo.findByFachId(id).map(this::toProfil);
    }

    @Override
    public void save(Profil profil) {
        Integer existingDbKey = repo.findByFachId(profil.getId()).map(ProfilDto::id).orElse(null);
        ProfilDto dto = toProfilDto(existingDbKey, profil);
        repo.save(dto);

    }

    @Override
    public Profil findByDbId(Integer dbId) {
        return repo.findById(dbId).map(this::toProfil).orElseThrow(NichtVorhandenException::new);
    }

    @Override
    public Integer getDbId(UUID id) {
        return repo.findByFachId(id).map(ProfilDto::id).orElseThrow(NichtVorhandenException::new);
    }

    private ProfilDto toProfilDto(Integer existingDbKey, Profil profil) {
        UUID fachId = profil.getId() != null ? profil.getId() : UUID.randomUUID();
        return new ProfilDto(existingDbKey, fachId, profil.getName(), profil.getEmail(), profil.getGithubLogin(), toLinksDto(profil.getLinks()), toFachgebieteDto(profil.getFachgebiete()), toProfileFilesDto(profil.getProfileFiles()));
    }

    private Set<ProfilFachgebietDto> toFachgebieteDto(Set<Fachgebiet> fachgebiete) {
        return fachgebiete.stream().map(this::toFachgebietDto).collect(Collectors.toSet());
    }

    private ProfilFachgebietDto toFachgebietDto(Fachgebiet fachgebiet) {
        return new ProfilFachgebietDto(fachgebiet.getName());
    }

    private Set<LinkDto> toLinksDto(Set<Link> links) {
        return links.stream().map(this::toLinkDto).collect(Collectors.toSet());
    }

    private LinkDto toLinkDto(Link link) {
        return new LinkDto(link.getUrl(), link.getAnzeigetext());
    }

    private ProfileFileDto toProfileFileDto(File file) {
        return new ProfileFileDto(file.getName(), file.getDescription(), file.getPath().toString(), file.getType(), file.getSize(), file.getUploadDate());
    }

    private Set<ProfileFileDto> toProfileFilesDto(Set<File> files) {
        return files.stream().map(this::toProfileFileDto).collect(Collectors.toSet());
    }

    @Override
    public void deleteById(Integer dbId) {
        repo.deleteById(dbId);
    }
}
