package de.propra.profil.domain.model.profil;

import de.propra.profil.application.exception.FileNotFoundException;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import org.springframework.data.annotation.PersistenceCreator;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;


public class Profil implements Comparable<Profil> {

    private UUID id;
    @NotBlank(message = "Geben Sie bitte einen Namen ein")
    private String name;
    @Email(message = "Geben Sie eine gültige Email ein")
    private String email;
    private String githubLogin;

    private Set<Link> links;
    private Set<Fachgebiet> fachgebiete;
    private Set<File> files;

    @PersistenceCreator
    public Profil(UUID id, String name, String email, String githubLogin, Set<Link> links, Set<Fachgebiet> fachgebiete, Set<File> files) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.githubLogin = githubLogin;
        this.links = new HashSet<>(links);
        this.fachgebiete = new HashSet<>(fachgebiete);
        this.files = new HashSet<>(files);
    }

    public Profil(String name, String email, String githubLogin) {
        this(UUID.randomUUID(), name, email, githubLogin, new HashSet<>(), new HashSet<>(), new HashSet<>());
    }

    public Profil() {
        this.id = UUID.randomUUID();
        this.links = new HashSet<>();
        this.fachgebiete = new HashSet<>();
        this.files = new HashSet<>();
    }

    @Override
    public int compareTo(Profil other) {
        return name.toLowerCase().compareTo(other.name.toLowerCase());
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Set<Link> getLinks() {
        return Set.copyOf(links);
    }

    public void setLinks(Set<Link> links) {
        this.links = new HashSet<>(links);
    }

    public Set<Fachgebiet> getFachgebiete() {
        return Set.copyOf(fachgebiete);
    }

    public void setFachgebiete(Set<Fachgebiet> fachgebiete) {
        this.fachgebiete = new HashSet<>(fachgebiete);
    }

    public void addLink(Link url) {
        links.add(url);
    }

    public void addFachgebiet(Fachgebiet fachgebiet) {
        fachgebiete.add(fachgebiet);
    }

    public String getGithubLogin() {
        return githubLogin;
    }

    public void setGithubLogin(String githubLogin) {
        this.githubLogin = githubLogin;
    }

    public Set<File> getProfileFiles() {
        return Set.copyOf(files);
    }

    public void setProfileFiles(Set<File> files) {
        this.files = new HashSet<>(files);
    }

    public void addProfileFile(File file) {
        files.add(file);
    }

    public File findProfileFileByName(String filename) {
        return files.stream().filter(file -> file.getName().equals(filename)).findFirst().orElseThrow(FileNotFoundException::new);
    }
}
