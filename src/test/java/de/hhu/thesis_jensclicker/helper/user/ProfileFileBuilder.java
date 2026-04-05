package de.hhu.thesis_jensclicker.helper.user;

import de.propra.profil.domain.model.profil.File;

import java.nio.file.Path;
import java.time.LocalDateTime;

public class ProfileFileBuilder {
    private String name;
    private String description;
    private Path path;
    private String type;
    private long size;
    private LocalDateTime uploadDate;

    public static File profileFile1() {
        return new ProfileFileBuilder()
                .name("application.properties")
                .description("description")
                .path(Path.of("src/main/resources/application.properties"))
                .type("text/plain")
                .size(1234L)
                .uploadDate(LocalDateTime.of(2026, 1, 1, 0, 0))
                .build();
    }

    public ProfileFileBuilder name(String name) {
        this.name = name;
        return this;
    }

    public ProfileFileBuilder description(String description) {
        this.description = description;
        return this;
    }

    public ProfileFileBuilder path(Path path) {
        this.path = path;
        return this;
    }

    public ProfileFileBuilder type(String type) {
        this.type = type;
        return this;
    }

    public ProfileFileBuilder size(long size) {
        this.size = size;
        return this;
    }

    public ProfileFileBuilder uploadDate(LocalDateTime uploadDate) {
        this.uploadDate = uploadDate;
        return this;
    }

    public File build() {
        return new File(name, description, path, type, size, uploadDate);
    }
}
