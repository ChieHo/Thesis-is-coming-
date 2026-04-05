package de.propra.profil.web;

import de.hhu.thesis_jensclicker.utility.Roles.TeacherAccess;
import de.propra.profil.application.exception.UnsupportedFileTypeException;
import de.propra.profil.application.service.ProfilService;
import de.propra.profil.domain.model.profil.Fachgebiet;
import de.propra.profil.domain.model.profil.File;
import de.propra.profil.domain.model.profil.Link;
import de.propra.profil.domain.model.profil.Profil;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import jakarta.validation.Valid;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.Set;
import java.util.UUID;

@TeacherAccess
@Controller
public class ProfileController {
    private final ProfilService profilService;
    private final Set<String> SUPPORTED_TYPES = Set.of("text/markdown", "application/pdf", "application/zip");


    @SuppressFBWarnings(value = "EI_EXPOSE_REP2", justification = "ArchUnit tests fail when autowired")
    public ProfileController(ProfilService profilService) {
        this.profilService = profilService;
    }

    @ModelAttribute
    public void addNav(Model model) {
        model.addAttribute("nav", "logged-in-teacher");
    }


    @GetMapping("/teacher/edit")
    public String profilErstellenView(Model model, @AuthenticationPrincipal OAuth2User principal) {
        model.addAttribute("link", new Link("",""));
        model.addAttribute("fachgebiet", new Fachgebiet(""));
        model.addAttribute("profil", profilService.getProfileByGithubLogin(principal.getAttribute("login")));
        return "profile_edit";
    }

    @PostMapping("/teacher/edit")
    public String profilErstellenView(@Valid @ModelAttribute("profil") Profil profil,
                                      BindingResult bindingResult,
                                      @Valid @ModelAttribute("link") Link link,
                                      BindingResult bindingResult2,
                                      @Valid @ModelAttribute("fachgebiet") Fachgebiet fachgebiet,
                                      BindingResult bindingResult3,
                                      @AuthenticationPrincipal OAuth2User principal,
                                      Model model,
                                      @RequestParam(required = false) String action
    ) {
        Profil dbProfil = profilService.getProfileByGithubLogin(principal.getAttribute("login"));
        UUID profilId = dbProfil.getId();


        if (action.equals("save_profile")) {
            if (bindingResult.hasErrors()) {
                model.addAttribute("profil", profil);
                model.addAttribute("link", link);
                model.addAttribute("fachgebiet", fachgebiet);
                return "profile_edit";
            }
            // weird behavior without setting these manually
            profil.setId(profilId);
            profil.setLinks(dbProfil.getLinks());
            profil.setFachgebiete(dbProfil.getFachgebiete());
            profil.setGithubLogin(dbProfil.getGithubLogin());
            profilService.profilHinzufuegen(profil);
        }
        if ("addLink".equals(action)) {
            if (bindingResult2.hasErrors()) {
                model.addAttribute("profil", dbProfil);
                model.addAttribute("link", link);
                model.addAttribute("fachgebiet", fachgebiet);
                return "profile_edit";
            }
            profilService.urlHinzufuegen(profilId, link);

        }
        if ("addFachgebiet".equals(action)) {
            if (bindingResult3.hasErrors()) {
                model.addAttribute("profil", dbProfil);
                model.addAttribute("link", link);
                model.addAttribute("fachgebiet", fachgebiet);
                return "profile_edit";
            }
            profilService.fachgebietHinzufuegen(profilId, fachgebiet);

        }
        return "redirect:/teacher/edit";
    }

    @GetMapping("/teacher/edit/files")
    public String uploadFileView(@AuthenticationPrincipal OAuth2User principal, Model model) {
        Profil profil = profilService.getProfileByGithubLogin(principal.getAttribute("login"));
        model.addAttribute("profile", profil);
        return "upload_file";
    }

    @PostMapping("/teacher/edit/files/upload")
    public String uploadFile(@RequestParam("file") MultipartFile file, @RequestParam("description") String description, @AuthenticationPrincipal OAuth2User principal) {
        Profil profil = profilService.getProfileByGithubLogin(principal.getAttribute("login"));
        /*if (file.getSize() > 10_000_000) {
            // FIXME: not actually needed; covered by max file size in properties file
            throw new FileTooLargeException();
        }*/
        if (!SUPPORTED_TYPES.contains(file.getContentType())) {
            throw new UnsupportedFileTypeException();
        }

        try {
            String name = file.getOriginalFilename();
            Path path = Path.of("./pf_" + UUID.randomUUID());
            file.transferTo(path);
            profilService.addProfileFile(profil.getId(), new File(name, description, path, file.getContentType(), file.getSize(), LocalDateTime.now()));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return "redirect:/teacher/edit/files";
    }
}
