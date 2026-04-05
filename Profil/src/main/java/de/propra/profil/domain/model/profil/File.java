package de.propra.profil.domain.model.profil;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.text.DecimalFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class File {
    private String name;
    private String description;
    private Path path;
    private String type;
    private long size;
    private LocalDateTime uploadDate;

    public File(String name, String description, Path path, String type, long size, LocalDateTime uploadDate) {
        this.name = name;
        this.description = description;
        this.path = path;
        this.type = type;
        this.size = size;
        this.uploadDate = uploadDate;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Path getPath() {
        return path;
    }

    public void setPath(Path path) {
        this.path = path;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public long getSize() {
        return size;
    }

    public void setSize(long size) {
        this.size = size;
    }

    public LocalDateTime getUploadDate() {
        return uploadDate;
    }

    public void setUploadDate(LocalDateTime uploadDate) {
        this.uploadDate = uploadDate;
    }
    /*
    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        ProfileFile that = (ProfileFile) o;
        return Objects.equals(name, that.name) && Objects.equals(path, that.path);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, path);
    }*/

    public java.io.File asFile() {
        return new java.io.File(path.toUri());
    }

    public String readText() throws IOException {
        String result;
        try (Stream<String> lines = Files.lines(path)) {
            result = lines.collect(Collectors.joining("\n"));
        }
        return result;
    }

    public String displaySize() {
        DecimalFormat df = new DecimalFormat("#.##");
        String suffix = "B";
        double dSize = size;

        if (size >= 1_000 && size < 1_000_000) {
            dSize = size / 1_000d;
            suffix = "kB";
        } else if (size >= 1_000_000) {
            dSize = size / 1_000_000d;
            suffix = "MB";
        }

        return df.format(dSize) + " " + suffix;
    }

    public String displayUploadDate() {
        return uploadDate.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
    }
}
