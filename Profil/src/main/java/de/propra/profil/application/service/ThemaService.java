package de.propra.profil.application.service;

import de.propra.profil.application.exception.NichtVorhandenException;
import de.propra.profil.application.repository.ProfilRepository;
import de.propra.profil.application.repository.ThemaRepository;
import de.propra.profil.domain.model.profil.Fachgebiet;
import de.propra.profil.domain.model.profil.File;
import de.propra.profil.domain.model.profil.Link;
import de.propra.profil.domain.model.profil.Profil;
import de.propra.profil.domain.model.thema.Thema;
import de.propra.profil.domain.model.thema.Voraussetzung;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class ThemaService {
    private final ThemaRepository themaRepository;
    private final ProfilRepository profilRepository;

    @SuppressFBWarnings(value = "EI_EXPOSE_REP2", justification = "ArchUnit tests fail when autowired")
    public ThemaService(ThemaRepository themaRepository, ProfilRepository profilRepository) {
        this.themaRepository = themaRepository;
        this.profilRepository = profilRepository;
    }

    public List<Thema> alleThemen() {
        return themaRepository.findAll();
    }

    public List<Thema> themenVonProfil(UUID profilId) {
        return themaRepository.findByProfilId(profilId);
    }

    public Thema thema(UUID themaId) {
        return themaRepository.findById(themaId).orElseThrow(NichtVorhandenException::new);
    }

    public void themaHinzufuegen(Thema thema, UUID profilId) {
        Optional<Thema> existing = themaRepository.findByTitelAndProfilId(thema.getTitel(), profilId);
        if (existing.isPresent()) {
            thema.setId(existing.get().getFachId());
        }
        themaRepository.save(thema, profilId);
    }

    public void linkHinzufuegen(UUID themaId, UUID profilId, Link link) {
        Thema thema = thema(themaId);
        thema.addLink(link);
        themaRepository.save(thema, profilId);
    }

    public void fachgebietHinzufuegen(UUID themaId, UUID profilId, Fachgebiet fachgebiet) {
        Thema thema = thema(themaId);
        thema.addFachgebiet(fachgebiet);
        themaRepository.save(thema, profilId);
    }

    public void voraussetzungHinzufuegen(UUID themaId, UUID profilId, Voraussetzung voraussetzung) {
        Thema thema = thema(themaId);
        thema.addVoraussetzung(voraussetzung);
        themaRepository.save(thema, profilId);
    }

    public void addThemaFile(UUID themaId, UUID profilId, File file) {
        Thema thema = thema(themaId);
        thema.addThemaFile(file);
        themaRepository.save(thema, profilId);
    }

    public void deleteThemaByUUID(UUID themaId) {
        themaRepository.deleteById(themaRepository.getDbId(themaId));
    }

    public Profil profilVonThema(UUID themaId) {
        UUID profilFachId = themaRepository.getProfilFachId(themaId);
        return profilRepository.findById(profilFachId)
                .orElseThrow(NichtVorhandenException::new);
    }
}
