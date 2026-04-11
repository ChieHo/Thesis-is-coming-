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
import java.util.stream.Collectors;

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

    public List<Thema> sucheWithBetreuer(String query, List<String> fachgebiete,
                                         List<String> voraussetzungen, List<String> betreuer) {
        return themaRepository.findAll().stream()
                .filter(thema -> matchesTitel(thema, query))
                .filter(thema -> matchesFachgebiete(thema, fachgebiete))
                .filter(thema -> matchesVoraussetzungen(thema, voraussetzungen))
                .filter(thema -> matchesBetreuer(thema, betreuer))
                .collect(Collectors.toList());
    }

    private boolean matchesTitel(Thema thema, String query) {
        if (query == null || query.isBlank()) return true;
        return thema.getTitel().toLowerCase().contains(query.toLowerCase());
    }

    private boolean matchesFachgebiete(Thema thema, List<String> fachgebiete) {
        if (fachgebiete == null || fachgebiete.isEmpty()) return true;
        return fachgebiete.stream().allMatch(fg ->
                thema.getFachgebiete().stream()
                        .anyMatch(f -> f.getName().toLowerCase().contains(fg.toLowerCase())));
    }

    private boolean matchesVoraussetzungen(Thema thema, List<String> voraussetzungen) {
        if (voraussetzungen == null || voraussetzungen.isEmpty()) return true;
        return voraussetzungen.stream().allMatch(v ->
                thema.getVoraussetzungen().stream()
                        .anyMatch(vv -> vv.getVoraussetzung().toLowerCase().contains(v.toLowerCase())));
    }

    private boolean matchesBetreuer(Thema thema, List<String> betreuer) {
        if (betreuer == null || betreuer.isEmpty()) return true;
        Profil profil = profilRepository.findById(
                themaRepository.getProfilFachId(thema.getFachId())).orElse(null);
        if (profil == null) return false;
        return betreuer.stream().allMatch(b ->
                profil.getName().toLowerCase().contains(b.toLowerCase()));
    }


}
