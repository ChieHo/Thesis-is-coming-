package de.hhu.thesis_jensclicker.test.service;


import de.hhu.thesis_jensclicker.helper.TestcontainersConfiguration;
import de.hhu.thesis_jensclicker.helper.user.ProfilBuilder;
import de.propra.profil.application.exception.NichtVorhandenException;
import de.propra.profil.application.repository.ProfilRepository;
import de.propra.profil.application.service.ProfilService;
import de.propra.profil.domain.model.profil.Fachgebiet;
import de.propra.profil.domain.model.profil.Link;
import de.propra.profil.domain.model.profil.Profil;
import de.propra.profil.domain.model.profil.File;
import de.propra.profil.persistence.ProfilRepositoryImpl;
import de.propra.profil.persistence.dao.ProfilDataRepo;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.jdbc.DataJdbcTest;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.context.annotation.Import;

import java.nio.file.Path;
import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

@DataJdbcTest
@Import({TestcontainersConfiguration.class})
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class ProfilServiceTest {
    @Autowired
    ProfilDataRepo profilDataRepo;


    ProfilService profilService;


    @BeforeEach
    void setup() {
        ProfilRepository profilRepository = new ProfilRepositoryImpl(profilDataRepo);
        profilService = new ProfilService(profilRepository);
    }

    @Test
    @DisplayName("Ein Profil kann gespeichert werden")
    void test_01() {
        Profil profil = ProfilBuilder.aProfil();
        profilService.profilHinzufuegen(profil);
        assertThat(profilService.alleProfil()).size().isEqualTo(1);
    }

    @Test
    @DisplayName("Der Profil Service kann mehrere Profile hinzfügen")
    void test_02() {
        Profil a = ProfilBuilder.aProfil();
        Profil b = ProfilBuilder.aProfil();
        profilService.profilHinzufuegen(a);
        profilService.profilHinzufuegen(b);
        assertThat(profilService.alleProfil()).size().isEqualTo(2);
    }

    @Test
    @DisplayName("Man kann einen Fachgebiet hinzufügen bei einem Profil")
    void test_03() {
        Profil a = ProfilBuilder.aProfil();
        profilService.profilHinzufuegen(a);
        profilService.fachgebietHinzufuegen(a.getId(), new Fachgebiet("Hi"));
        Profil result = profilService.getProfileByGithubLogin(a.getGithubLogin());
        assertThat(result.getFachgebiete().stream().map(Fachgebiet::getName)).size().isEqualTo(1);
    }

    @Test
    @DisplayName("Man kann eine URL hinzufügen bei einem Profil")
    void test_04() {
        Profil a = ProfilBuilder.aProfil();
        profilService.profilHinzufuegen(a);
        profilService.urlHinzufuegen(a.getId(), new Link("https://hhu.de"));
        Profil profil = profilService.getProfileByGithubLogin(a.getGithubLogin());
        assertThat(profil.getLinks().stream().map(Link::getUrl)).size().isEqualTo(1);
    }

    @Test
    @DisplayName("Wenn Profil nicht verfügbar gebe NichtVorhandenException")
    void test_05() throws Exception {
        Profil a = ProfilBuilder.aProfil();
        assertThrows(NichtVorhandenException.class, () -> profilService.getProfileByGithubLogin(a.getGithubLogin()));
    }

    @Test
    @DisplayName("Die DB ID wird zurückgegeben bei einem Profil")
    void test_06() {
        Profil a = ProfilBuilder.aProfil();
        profilService.profilHinzufuegen(a);
        assertThat(profilService.getProfilDbId(a.getId())).isEqualTo(6);
    }

    @Test
    @DisplayName("Die UUID kriegt man auch durch die DB ID")
    void test_07() {
        Profil a = ProfilBuilder.aProfil();
        profilService.profilHinzufuegen(a);
        assertThat(profilService.getProfilIdByDbId(profilService.getProfilDbId(a.getId()))).isNotNull();
    }

    @Test
    @DisplayName("existsProfileByGithubLogin gibt einen boolean zurück")
    void test_08() {
        Profil a = ProfilBuilder.aProfil();
        Profil b = new ProfilBuilder().githubLogin("jgorejgre").build();
        profilService.profilHinzufuegen(a);
        assertThat(profilService.existsProfileByGithubLogin(a.getGithubLogin())).isTrue();
        assertThat(profilService.existsProfileByGithubLogin(b.getGithubLogin())).isFalse();
    }

    @Test
    @DisplayName("existsProfileByUUID funktioniert")
    void test_09() {
        Profil a = ProfilBuilder.aProfil();
        Profil b = new ProfilBuilder().githubLogin("Friedrich Merz").build();
        profilService.profilHinzufuegen(a);
        assertThat(profilService.existsProfileByUUID(a.getId())).isTrue();
        assertThat(profilService.existsProfileByUUID(b.getId())).isFalse();
    }

    @Test
    @DisplayName("deleteProfileByUUIId funktioniert")
    void test_10() {
        Profil a = ProfilBuilder.aProfil();
        profilService.profilHinzufuegen(a);
        profilService.deleteProfileByUUID(a.getId());
        assertThat(profilService.alleProfil()).size().isEqualTo(0);
    }

    @Test
    @DisplayName("addProfileFile funktioniert")
    void test_11() {
        Profil a = ProfilBuilder.aProfil();
        profilService.profilHinzufuegen(a);
        File file = new File("gr", "re", Path.of("test.txt"), "test.txt", 1, LocalDateTime.now());
        profilService.addProfileFile(a.getId(), file);
        assertThat(profilService.alleProfil().stream().map(Profil::getProfileFiles)).size().isEqualTo(1);
    }

    @Test
    @DisplayName("Wenn neues Profil mit UUID null übergeben bekommt, dann wird eine neue Random UUID erstellt")
    void test_14() {
        Profil a = new ProfilBuilder().id(null).build();
        profilService.profilHinzufuegen(a);
        Profil result = profilService.getProfileByGithubLogin(a.getGithubLogin());
        assertThat(result.getId()).isNotNull();
    }

}
