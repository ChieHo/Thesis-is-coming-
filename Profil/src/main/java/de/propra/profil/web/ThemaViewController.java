package de.propra.profil.web;

import de.hhu.thesis_jensclicker.utility.MarkdownRenderer;
import de.hhu.thesis_jensclicker.utility.NavMapper;
import de.hhu.thesis_jensclicker.utility.Roles.StudiAccess;
import de.propra.profil.application.service.ProfilService;
import de.propra.profil.application.service.ThemaService;
import de.propra.profil.domain.model.profil.File;
import de.propra.profil.domain.model.profil.Profil;
import de.propra.profil.domain.model.thema.Thema;
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
import org.springframework.web.bind.annotation.RequestParam;


import java.io.IOException;
import java.util.List;
import java.util.UUID;


import static java.util.stream.Collectors.*;

@Controller
@StudiAccess
public class ThemaViewController {
    private final ThemaService themaService;
    private final ProfilService profilService;

    @SuppressFBWarnings(value = "EI_EXPOSE_REP2", justification = "ArchUnit tests fail when autowired")
    public ThemaViewController(ThemaService themaService, ProfilService profilService) {
        this.themaService = themaService;
        this.profilService = profilService;
    }

    @ModelAttribute
    public void addNav(Model model) {
        model.addAttribute("nav", "logged-in-teacher");
    }

    @GetMapping("/view/thema/all")
    public String listThemen(Model model, @AuthenticationPrincipal OAuth2User principal) {
        model.addAttribute("themen", themaService.alleThemen());
        model.addAttribute("nav", NavMapper.mapRole(principal));
        return "view-thema/list";
    }



    @GetMapping("/view/thema/{id}")
    public String viewThema(Model model, @PathVariable("id") UUID id, @AuthenticationPrincipal OAuth2User principal) {
        Thema thema = themaService.thema(id);
        Profil betreuer = themaService.profilVonThema(id);
        model.addAttribute("thema", thema);
        model.addAttribute("betreuer", betreuer);
        if (thema.getBeschreibung() != null && !thema.getBeschreibung().isBlank()) {
            model.addAttribute("renderedBeschreibung", MarkdownRenderer.render(thema.getBeschreibung()));
        }
        model.addAttribute("nav", NavMapper.mapRole(principal));
        return "view-thema/thema";
    }

    @GetMapping("/view/thema/{id}/{filename}")
    public String viewFileDetails(Model model, @PathVariable("id") UUID id,
                                  @PathVariable("filename") String filename,
                                  @AuthenticationPrincipal OAuth2User principal) throws IOException {
        Thema thema = themaService.thema(id);
        File file = thema.findThemaFileByName(filename);
        model.addAttribute("thema", thema);
        model.addAttribute("file", file);
        if (file.getType().equals("text/markdown")) {
            model.addAttribute("renderedMarkdown", MarkdownRenderer.render(file.readText()));
        }
        model.addAttribute("nav", NavMapper.mapRole(principal));
        return "view-thema/file_details";
    }

    @GetMapping("/view/thema/{id}/{filename}/download")
    public ResponseEntity<FileSystemResource> downloadFile(@PathVariable("id") UUID id,
                                                           @PathVariable("filename") String filename) {
        Thema thema = themaService.thema(id);
        File file = thema.findThemaFileByName(filename);
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.parseMediaType(file.getType()));
        headers.setContentDispositionFormData("attachment", file.getName());
        headers.setContentLength(file.getSize());
        return new ResponseEntity<>(new FileSystemResource(file.asFile()), headers, HttpStatus.OK);
    }

    @GetMapping("/view/thema/search")
    public String searchThemen(Model model,
                               @RequestParam(required = false) String query,
                               @RequestParam(required = false) List<String> fachgebiet,
                               @RequestParam(required = false) List<String> voraussetzung,
                               @RequestParam(required = false) List<String> betreuer,
                               @AuthenticationPrincipal OAuth2User principal) {
        if (query == null && fachgebiet == null && voraussetzung == null && betreuer == null) {
            return "redirect:/view/thema/all";
        }
        List<Thema> themen = themaService.sucheWithBetreuer(query, fachgebiet, voraussetzung, betreuer);
        model.addAttribute("themen", themen);
        model.addAttribute("found", !themen.isEmpty());
        model.addAttribute("nav", NavMapper.mapRole(principal));
        return "view-thema/list";
    }
}