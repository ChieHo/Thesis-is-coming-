package de.hhu.thesis_jensclicker.test.domain.model;

import de.hhu.thesis_jensclicker.helper.user.ProfileFileBuilder;
import de.propra.profil.domain.model.profil.File;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

public class FileTest {
    @Test
    @DisplayName("ProfileFiles liefert die korrekte displaySize für Dateigrößen < 1 kB")
    void test_01() throws Exception {
        File file = new ProfileFileBuilder()
                .size(15)
                .build();

        String result = file.displaySize();
        assertThat(result).isEqualTo("15 B");
    }

    @Test
    @DisplayName("ProfileFiles liefert die korrekte displaySize für Dateigrößen >= 1 kB und < 1 MB")
    void test_02() throws Exception {
        File file = new ProfileFileBuilder()
                .size(1_000)
                .build();

        String result = file.displaySize();
        assertThat(result).isEqualTo("1 kB");
    }

    @Test
    @DisplayName("ProfileFiles liefert die korrekte displaySize für Dateigrößen >= 1 MB")
    void test_03() throws Exception {
        File file = new ProfileFileBuilder()
                .size(1_330_000)
                .build();

        String result = file.displaySize();
        assertThat(result).isEqualTo("1.33 MB");
    }

    @Test
    @DisplayName("ProfileFiles liefert den korrekten Wert für displayUploadDate")
    void test_04() throws Exception {
        File file = new ProfileFileBuilder()
                .uploadDate(LocalDateTime.of(2026, 1, 2, 4, 5, 6))
                .build();

        String result = file.displayUploadDate();

        assertThat(result).isEqualTo("2026-01-02 04:05:06");
    }

}
