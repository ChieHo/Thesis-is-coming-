package de.hhu.thesis_jensclicker.test.service;

import de.hhu.thesis_jensclicker.helper.user.ProfilBuilder;
import de.propra.profil.domain.model.profil.Profil;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class ProfilTest {
    //Profil Domain Modell Tests

    @Test
    @DisplayName("der vergleich zwischen Profil a und b ist sortiert nach Namen")
    void test_12() {
        Profil a = new ProfilBuilder().name("a").build();
        Profil b = new ProfilBuilder().name("b").build();
        assertThat(a.equals(b)).isFalse();
    }

    @Test
    @DisplayName("wenn Objekt null dann gebe null zurück in der equals Methode")
    void test_13() {
        Object o = null;
        Profil a = ProfilBuilder.aProfil();
        assertThat(a.equals(o)).isFalse();
    }


}
