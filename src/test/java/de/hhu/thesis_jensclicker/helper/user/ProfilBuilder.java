package de.hhu.thesis_jensclicker.helper.user;

import de.propra.profil.domain.model.profil.Fachgebiet;
import de.propra.profil.domain.model.profil.File;
import de.propra.profil.domain.model.profil.Link;
import de.propra.profil.domain.model.profil.Profil;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class ProfilBuilder {
    private UUID id = UUID.randomUUID();
    private String name = "Dieter Wurst";
    private String email = "dieter.wurst@hhu.de";
    private String githubLogin = " githublogiefeiuq9832ur";
    private Set<Link> links = new HashSet<>();
    private Set<Fachgebiet> fachgebiete = new HashSet<>();
    private Set<File> files = new HashSet<>();

    public static Profil builder() {
        return new ProfilBuilder().build();
    }

    public static Profil aProfil() {
        return new ProfilBuilder()
                .id(UUID.randomUUID())
                .name("Test User")
                .email("test@example.com")
                .githubLogin("testuser")
                .links(new HashSet<>())
                .fachgebiete(new HashSet<>())
                .profileFiles(new HashSet<>())
                .build();
    }

    public static Profil bProfil() {
        Profil profil = aProfil();
        Set<File> files = new HashSet<>();
        // using a file that exists to avoid io error
        files.add(ProfileFileBuilder.profileFile1());
        profil.setProfileFiles(files);
        return profil;
    }

    public ProfilBuilder id(UUID id) {
        this.id = id;
        return this;
    }

    public ProfilBuilder name(String name) {
        this.name = name;
        return this;
    }

    public ProfilBuilder email(String email) {
        this.email = email;
        return this;
    }

    public ProfilBuilder githubLogin(String githubLogin) {
        this.githubLogin = githubLogin;
        return this;
    }

    public ProfilBuilder links(Set<Link> links) {
        this.links = links;
        return this;
    }

    public ProfilBuilder fachgebiete(Set<Fachgebiet> fachgebiete) {
        this.fachgebiete = fachgebiete;
        return this;
    }

    private ProfilBuilder profileFiles(HashSet<File> files) {
        this.files = files;
        return this;
    }

    public ProfilBuilder addLink(Link link) {
        this.links.add(link);
        return this;
    }

    public ProfilBuilder addFachgebiete(Fachgebiet fachgebiet) {
        this.fachgebiete.add(fachgebiet);
        return this;
    }

    public ProfilBuilder addProfileFile(File file) {
        this.files.add(file);
        return this;
    }

    public Profil build() {
        return new Profil(id, name, email, githubLogin, links, fachgebiete, files);
    }
}
