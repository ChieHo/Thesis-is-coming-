package de.propra.profil.domain.model.profil;


import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;


public class Fachgebiet {

    @NotBlank(message = "Geben Sie einen Fachgebiet ein")
    @Size(min = 1, max = 250, message = "Fachgebiet darf nicht länger als 250 Zeichen sein und mindestens 1 Zeichen lang sein")
    private String name;

    public Fachgebiet() {}

    public Fachgebiet(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

}
