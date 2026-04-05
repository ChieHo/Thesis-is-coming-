package de.hhu.thesis_jensclicker;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan(basePackages = {
        "de.hhu.thesis_jensclicker",
        "de.propra.homepage",
        "de.propra.profil",
})
public class ThesisJensclickerApplication {

    public static void main(String[] args) {
        SpringApplication.run(ThesisJensclickerApplication.class, args);
    }

}
