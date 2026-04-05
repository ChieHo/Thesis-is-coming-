package de.propra.profil.domain.model.profil;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;


public class Link {
    @Pattern(
            regexp = "^(https://([a-zA-Z0-9.-]+\\.[a-zA-Z]{2,6}(/.*)?)?)?$",
            message = "Nur sichere HTTPS-Links sind erlaubt (z.B. https://example.com)"
    )
    @Size(min = 8, max = 250, message = "Geben Sie keine leere Zeichen ein")
    private String url;

    @NotBlank(message = "Geben Sie bitte einen Anzeigetext ein")
    private String anzeigetext;

    public Link() {}

    public Link(String url, String anzeigetext) {
        this.url = url;
        this.anzeigetext = anzeigetext;
    }

    public String getUrl() { return url; }
    public void setUrl(String url) { this.url = url; }

    public String getAnzeigetext() { return anzeigetext; }
    public void setAnzeigetext(String anzeigetext) { this.anzeigetext = anzeigetext; }
}
