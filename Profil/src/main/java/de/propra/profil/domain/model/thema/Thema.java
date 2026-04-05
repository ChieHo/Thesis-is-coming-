package de.propra.profil.domain.model.thema;


import de.propra.profil.application.exception.FileNotFoundException;
import de.propra.profil.domain.model.profil.Fachgebiet;
import de.propra.profil.domain.model.profil.File;
import de.propra.profil.domain.model.profil.Link;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class Thema {
    private UUID fachId;
    private String titel;
    private String beschreibung;
    private Set<Fachgebiet> fachgebiete;
    private Set<Link> links;
    private Set<File> themaFiles;
    private Set<Voraussetzung> voraussetzungen;

    public void setFachId(UUID fachId) {
        this.fachId = fachId;
    }

    public void setTitel(String titel) {
        this.titel = titel;
    }

    public void setBeschreibung(String beschreibung) {
        this.beschreibung = beschreibung;
    }

    public Thema(UUID fachId, String titel, String beschreibung, Set<Fachgebiet> fachgebiete, Set<Link> links, Set<File> themaFiles, Set<Voraussetzung> voraussetzungen) {
        this.fachId = fachId;
        this.titel = titel;
        this.beschreibung = beschreibung;
        this.fachgebiete = new HashSet<>(fachgebiete);
        this.links = new HashSet<>(links);
        this.themaFiles = new HashSet<>(themaFiles);
        this.voraussetzungen = new HashSet<>(voraussetzungen);
    }

    public Thema() {
        this.fachgebiete = new HashSet<>();
        this.links = new HashSet<>();
        this.themaFiles = new HashSet<>();
        this.voraussetzungen = new HashSet<>();
    }

    public UUID getFachId() {
        return fachId;
    }

    public String getTitel() {
        return titel;
    }

    public String getBeschreibung() {
        return beschreibung;
    }

    public Set<Fachgebiet> getFachgebiete() {
        return fachgebiete;
    }

    public Set<Link> getLinks() {
        return links;
    }

    public Set<File> getThemaFiles() {
        return themaFiles;
    }

    public Set<Voraussetzung> getVoraussetzungen() {
        return voraussetzungen;
    }

    public void addLink(Link link) {
        links.add(link);
    }

    public void addFachgebiet(Fachgebiet fachgebiet) {
        fachgebiete.add(fachgebiet);
    }

    public void addVoraussetzung(Voraussetzung voraussetzung) {
        voraussetzungen.add(voraussetzung);
    }

    public void addThemaFile(File file) {
        themaFiles.add(file);
    }

    public void setId(UUID uuid) {
        this.fachId = uuid;
    }

    public void setLinks(Set<Link> links) {
        this.links = links;
    }

    public void setFachgebiete(Set<Fachgebiet> fachgebiete) {
        this.fachgebiete = fachgebiete;
    }

    public void setVoraussetzungen(Set<Voraussetzung> voraussetzungen) {
        this.voraussetzungen = voraussetzungen;
    }

    public void setThemaFiles(Set<File> themaFiles) {
        this.themaFiles = themaFiles;
    }

    public File findThemaFileByName(String filename) {
        return themaFiles.stream()
                .filter(file -> file.getName().equals(filename))
                .findFirst()
                .orElseThrow(FileNotFoundException::new);
    }
}
