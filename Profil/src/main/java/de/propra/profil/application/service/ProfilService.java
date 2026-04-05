package de.propra.profil.application.service;

import de.propra.profil.application.exception.NichtVorhandenException;
import de.propra.profil.application.repository.ProfilRepository;
import de.propra.profil.domain.model.profil.Fachgebiet;
import de.propra.profil.domain.model.profil.File;
import de.propra.profil.domain.model.profil.Link;
import de.propra.profil.domain.model.profil.Profil;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class ProfilService {
    private final ProfilRepository repository;

    @SuppressFBWarnings(value = "EI_EXPOSE_REP2", justification = "ArchUnit tests fail when autowired")
    public ProfilService(ProfilRepository repository) {
        this.repository = repository;
    }

    public Set<Profil> alleProfil() {
        return repository.findAll().stream().collect(Collectors.toSet());
    }

    public Profil profil(UUID id) {
        return repository.findById(id).orElseThrow(NichtVorhandenException::new);
    }

    public void profilHinzufuegen(Profil profil) {
        repository.save(profil);
    }

    public void urlHinzufuegen(UUID id, Link url) {
        Profil profil = profil(id);
        profil.addLink(url);
        repository.save(profil);
    }

    public void fachgebietHinzufuegen(UUID id, Fachgebiet fachgebiet) {
        Profil profil = profil(id);
        profil.addFachgebiet(new Fachgebiet(fachgebiet.getName()));
        repository.save(profil);
    }

    public Integer getProfilDbId(UUID profilId) {
        return repository.getDbId(profilId);
    }

    public UUID getProfilIdByDbId(Integer profilDbId) {
        return repository.findByDbId(profilDbId).getId();
    }

    public boolean existsProfileByGithubLogin(String githubLogin) {
        return repository.findAll().stream().anyMatch(profil -> profil.getGithubLogin().equals(githubLogin));
    }

    public Profil getProfileByGithubLogin(String githubLogin) {
        // FIXME: This only works when github_login is unique, otherwise the first match is returned
        Optional<Profil> profilOpt = repository.findAll().stream().filter(profil -> profil.getGithubLogin().equals(githubLogin)).findFirst();
        if (profilOpt.isPresent()) return profilOpt.get();
        else throw new NichtVorhandenException();
    }

    public boolean existsProfileByUUID(UUID id) {
        try {
            profil(id);
            return true;
        } catch (NichtVorhandenException e) {
            return false;
        }
    }

    /*public void deleteProfileById(Integer dbId) {
        repository.deleteById(dbId);
    }*/

    public void deleteProfileByUUID(UUID id) {
        repository.deleteById(repository.getDbId(id));
    }

    public void addProfileFile(UUID id, File file) {
        Profil profil = profil(id);
        profil.addProfileFile(file);
        repository.save(profil);
    }
}
