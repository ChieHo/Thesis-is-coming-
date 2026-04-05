package de.propra.profil.web;

import de.hhu.thesis_jensclicker.utility.Roles.TeacherAccess;
import de.propra.profil.application.exception.UnsupportedFileTypeException;
import de.propra.profil.application.service.ProfilService;
import de.propra.profil.application.service.ThemaService;
import de.propra.profil.domain.model.profil.Fachgebiet;
import de.propra.profil.domain.model.profil.File;
import de.propra.profil.domain.model.profil.Link;
import de.propra.profil.domain.model.profil.Profil;
import de.propra.profil.domain.model.thema.Thema;
import de.propra.profil.domain.model.thema.Voraussetzung;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import jakarta.validation.Valid;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@TeacherAccess
@Controller
public class ThemaController {
    private final ThemaService themaService;
    private final ProfilService profilService;
    private final Set<String> SUPPORTED_TYPES = Set.of("text/markdown", "application/pdf", "application/zip");

    @SuppressFBWarnings(value = "EI_EXPOSE_REP2", justification = "ArchUnit tests fail when autowired")
    public ThemaController(ThemaService themaService, ProfilService profilService) {
        this.themaService = themaService;
        this.profilService = profilService;
    }

    @ModelAttribute
    public void addNav(Model model) {
        model.addAttribute("nav", "logged-in-teacher");
    }

    @GetMapping("/teacher/thema/neu")
    public String themaErstellenView(Model model, @AuthenticationPrincipal OAuth2User principal) {
        model.addAttribute("thema", new Thema(null, "", "", new HashSet<>(), new HashSet<>(), new HashSet<>(), new HashSet<>()));
        model.addAttribute("link", new Link("", ""));
        model.addAttribute("fachgebiet", new Fachgebiet(""));
        model.addAttribute("voraussetzung", new Voraussetzung(""));
        model.addAttribute("themen", themaService.themenVonProfil(profilService.getProfileByGithubLogin(principal.getAttribute("login")).getId()));
        return "thema_edit";
    }

    @PostMapping("/teacher/thema/neu")
    public String themaErstellen(@Valid @ModelAttribute("thema") Thema thema,
                                 BindingResult bindingResult,
                                 @Valid @ModelAttribute("link") Link link,
                                 BindingResult bindingResult2,
                                 @Valid @ModelAttribute("fachgebiet") Fachgebiet fachgebiet,
                                 BindingResult bindingResult3,
                                 @Valid @ModelAttribute("voraussetzung") Voraussetzung voraussetzung,
                                 BindingResult bindingResult4,
                                 @AuthenticationPrincipal OAuth2User principal,
                                 Model model,
                                 @RequestParam(required = false) String action) {

        Profil dbProfil = profilService.getProfileByGithubLogin(principal.getAttribute("login"));
        UUID profilId = dbProfil.getId();

        if ("save_thema".equals(action)) {
            if (bindingResult.hasErrors()) {
                model.addAttribute("thema", thema);
                model.addAttribute("link", link);
                model.addAttribute("fachgebiet", fachgebiet);
                model.addAttribute("voraussetzung", voraussetzung);
                return "thema_edit";
            }
            if (thema.getFachId() == null) {
                thema.setId(UUID.randomUUID());
            }
            themaService.themaHinzufuegen(thema, profilId);
        }
        if ("addLink".equals(action)) {
            if (bindingResult2.hasErrors()) {
                model.addAttribute("thema", thema);
                model.addAttribute("link", link);
                model.addAttribute("fachgebiet", fachgebiet);
                model.addAttribute("voraussetzung", voraussetzung);
                return "thema_edit";
            }
            themaService.linkHinzufuegen(thema.getFachId(), profilId, link);
        }
        if ("addFachgebiet".equals(action)) {
            if (bindingResult3.hasErrors()) {
                model.addAttribute("thema", thema);
                model.addAttribute("link", link);
                model.addAttribute("fachgebiet", fachgebiet);
                model.addAttribute("voraussetzung", voraussetzung);
                return "thema_edit";
            }
            themaService.fachgebietHinzufuegen(thema.getFachId(), profilId, fachgebiet);
        }
        if ("addVoraussetzung".equals(action)) {
            if (bindingResult4.hasErrors()) {
                model.addAttribute("thema", thema);
                model.addAttribute("link", link);
                model.addAttribute("fachgebiet", fachgebiet);
                model.addAttribute("voraussetzung", voraussetzung);
                return "thema_edit";
            }
            themaService.voraussetzungHinzufuegen(thema.getFachId(), profilId, voraussetzung);
        }
        return "redirect:/teacher/thema/" + thema.getFachId() + "/edit";
    }

    @GetMapping("/teacher/thema/{themaId}/edit")
    public String themaBearbeitenView(@PathVariable UUID themaId, Model model, @AuthenticationPrincipal OAuth2User principal) {
        model.addAttribute("thema", themaService.thema(themaId));
        model.addAttribute("link", new Link("", ""));
        model.addAttribute("fachgebiet", new Fachgebiet(""));
        model.addAttribute("voraussetzung", new Voraussetzung(""));
        model.addAttribute("themen", themaService.themenVonProfil(profilService.getProfileByGithubLogin(principal.getAttribute("login")).getId()));
        return "thema_edit";
    }

    @PostMapping("/teacher/thema/{themaId}/edit")
    public String themaBearbeiten(@PathVariable UUID themaId,
                                  @Valid @ModelAttribute("thema") Thema thema,
                                  BindingResult bindingResult,
                                  @Valid @ModelAttribute("link") Link link,
                                  BindingResult bindingResult2,
                                  @Valid @ModelAttribute("fachgebiet") Fachgebiet fachgebiet,
                                  BindingResult bindingResult3,
                                  @Valid @ModelAttribute("voraussetzung") Voraussetzung voraussetzung,
                                  BindingResult bindingResult4,
                                  @AuthenticationPrincipal OAuth2User principal,
                                  Model model,
                                  @RequestParam(required = false) String action) {

        Profil dbProfil = profilService.getProfileByGithubLogin(principal.getAttribute("login"));
        UUID profilId = dbProfil.getId();
        Thema dbThema = themaService.thema(themaId);

        if ("save_thema".equals(action)) {
            if (bindingResult.hasErrors()) {
                model.addAttribute("thema", thema);
                model.addAttribute("link", link);
                model.addAttribute("fachgebiet", fachgebiet);
                model.addAttribute("voraussetzung", voraussetzung);
                return "thema_edit";
            }
            thema.setId(themaId);
            thema.setLinks(dbThema.getLinks());
            thema.setFachgebiete(dbThema.getFachgebiete());
            thema.setVoraussetzungen(dbThema.getVoraussetzungen());
            thema.setThemaFiles(dbThema.getThemaFiles());
            themaService.themaHinzufuegen(thema, profilId);
        }
        if ("addLink".equals(action)) {
            if (bindingResult2.hasErrors()) {
                model.addAttribute("thema", dbThema);
                model.addAttribute("link", link);
                model.addAttribute("fachgebiet", fachgebiet);
                model.addAttribute("voraussetzung", voraussetzung);
                return "thema_edit";
            }
            themaService.linkHinzufuegen(themaId, profilId, link);
        }
        if ("addFachgebiet".equals(action)) {
            if (bindingResult3.hasErrors()) {
                model.addAttribute("thema", dbThema);
                model.addAttribute("link", link);
                model.addAttribute("fachgebiet", fachgebiet);
                model.addAttribute("voraussetzung", voraussetzung);
                return "thema_edit";
            }
            themaService.fachgebietHinzufuegen(themaId, profilId, fachgebiet);
        }
        if ("addVoraussetzung".equals(action)) {
            if (bindingResult4.hasErrors()) {
                model.addAttribute("thema", dbThema);
                model.addAttribute("link", link);
                model.addAttribute("fachgebiet", fachgebiet);
                model.addAttribute("voraussetzung", voraussetzung);
                return "thema_edit";
            }
            themaService.voraussetzungHinzufuegen(themaId, profilId, voraussetzung);
        }
        return "redirect:/teacher/thema/" + themaId + "/edit";
    }

    @GetMapping("/teacher/thema/{themaId}/edit/files")
    public String uploadFileView(@PathVariable UUID themaId, Model model) {
        model.addAttribute("thema", themaService.thema(themaId));
        return "thema_upload_file";
    }

    @PostMapping("/teacher/thema/{themaId}/edit/files/upload")
    public String uploadFile(@PathVariable UUID themaId,
                             @RequestParam("file") MultipartFile file,
                             @RequestParam("description") String description,
                             @AuthenticationPrincipal OAuth2User principal) {
        Profil profil = profilService.getProfileByGithubLogin(principal.getAttribute("login"));
        if (!SUPPORTED_TYPES.contains(file.getContentType())) {
            throw new UnsupportedFileTypeException();
        }
        try {
            String name = file.getOriginalFilename();
            Path path = Path.of("./tf_" + UUID.randomUUID());
            file.transferTo(path);
            themaService.addThemaFile(themaId, profil.getId(),
                    new File(name, description, path, file.getContentType(), file.getSize(), LocalDateTime.now()));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return "redirect:/teacher/thema/" + themaId + "/edit/files";
    }
}