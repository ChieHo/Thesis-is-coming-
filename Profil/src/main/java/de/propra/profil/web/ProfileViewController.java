package de.propra.profil.web;

import de.hhu.thesis_jensclicker.utility.MarkdownRenderer;
import de.hhu.thesis_jensclicker.utility.NavMapper;
import de.hhu.thesis_jensclicker.utility.Roles.StudiAccess;
import de.propra.profil.application.service.ProfilService;
import de.propra.profil.domain.model.profil.File;
import de.propra.profil.domain.model.profil.Profil;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;

import java.io.IOException;
import java.util.Collection;
import java.util.UUID;
import java.util.stream.Collectors;

@Controller
@StudiAccess
public class ProfileViewController {
    private final ProfilService profilService;


    @SuppressFBWarnings(value = "EI_EXPOSE_REP2", justification = "ArchUnit tests fail when autowired")
    public ProfileViewController(ProfilService profilService) {
        this.profilService = profilService;

    }

    @ModelAttribute
    public void addNav(Model model) {
        model.addAttribute("nav", "logged-in-teacher");
    }


    @GetMapping("/view/profile/all")
    public String listProfiles(Model model, @AuthenticationPrincipal OAuth2User principal) {
        model.addAttribute("profiles", profilService.alleProfil());
        model.addAttribute("nav", NavMapper.mapRole(principal));
        return "view-profile/list";
    }

    @GetMapping("/view/profile/search")
    public String searchProfiles(Model model, String query, @AuthenticationPrincipal OAuth2User principal) {
        if (query == null || query.isBlank()) {
            return "redirect:/view/profile/all";
        }
        Collection<Profil> profiles = profilService.alleProfil()
                .stream()
                .filter(profil -> profil.getName().toLowerCase().contains(query.toLowerCase()))
                .collect(Collectors.toSet());
        model.addAttribute("query", query);
        model.addAttribute("profiles", profiles);
        model.addAttribute("found", !profiles.isEmpty());
        model.addAttribute("nav", NavMapper.mapRole(principal));
        return "view-profile/list";
    }

    @GetMapping("/view/profile/{id}")
    public String viewProfile(Model model, @PathVariable("id") UUID id, @AuthenticationPrincipal OAuth2User principal) {
        Profil profile = profilService.profil(id);
        model.addAttribute("profile", profile);
        model.addAttribute("noEmail", profile.getEmail().isEmpty());
        model.addAttribute("noFachgebiete", profile.getFachgebiete().isEmpty());
        model.addAttribute("noLinks", profile.getLinks().isEmpty());
        model.addAttribute("nav", NavMapper.mapRole(principal));
        return "view-profile/profile";
    }

    @GetMapping("/view/profile/{id}/{filename}")
    public String viewFileDetails(Model model, @PathVariable("id") UUID id, @AuthenticationPrincipal OAuth2User principal, @PathVariable("filename") String filename) throws IOException {
        Profil profile = profilService.profil(id);
        File file = profile.findProfileFileByName(filename);
        model.addAttribute("profile", profile);
        model.addAttribute("file", file);
        if (file.getType().equals("text/markdown")) {
            model.addAttribute("renderedMarkdown", MarkdownRenderer.render(file.readText()));
        }
        model.addAttribute("nav", NavMapper.mapRole(principal));
        return "view-profile/file_details";
    }

    @GetMapping("/view/profile/{id}/{filename}/download")
    public ResponseEntity<FileSystemResource> downloadFile(Model model, @PathVariable("id") UUID id, @AuthenticationPrincipal OAuth2User principal, @PathVariable("filename") String filename) {
        Profil profile = profilService.profil(id);
        File file = profile.findProfileFileByName(filename);
        // https://stackoverflow.com/questions/5673260/downloading-a-file-from-spring-controllers#26537519
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.parseMediaType(file.getType()));
        headers.setContentDispositionFormData("attachment", file.getName());
        headers.setContentLength(file.getSize());
        return new ResponseEntity<>(new FileSystemResource(file.asFile()), headers, HttpStatus.OK);
    }

}
