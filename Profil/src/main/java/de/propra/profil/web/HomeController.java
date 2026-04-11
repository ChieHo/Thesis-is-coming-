package de.propra.profil.web;

import de.hhu.thesis_jensclicker.utility.NavMapper;
import de.hhu.thesis_jensclicker.utility.Roles.StudiAccess;
import de.hhu.thesis_jensclicker.utility.Roles.TeacherAccess;
import de.propra.profil.application.service.ProfilService;
import de.propra.profil.application.service.ThemaService;
import de.propra.profil.domain.model.profil.Profil;
import de.propra.profil.domain.model.thema.Thema;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@StudiAccess
@Controller
public class HomeController {
    private final ProfilService profilService;
    private final ThemaService themaService;

    @SuppressFBWarnings(value = "EI_EXPOSE_REP2", justification = "ArchUnit tests fail when autowired")
    public HomeController(ProfilService profilService, ThemaService themaService) {
        this.profilService = profilService;
        this.themaService = themaService;
    }

    @GetMapping("/")
    public String index(Model model, @AuthenticationPrincipal OAuth2User principal) {
        model.addAttribute("nav", NavMapper.mapRole(principal));
        return "homepage/home";
    }

    @TeacherAccess
    @GetMapping("/teacher")
    public String homepage(Model model, @AuthenticationPrincipal OAuth2User principal) {
        Profil profil = profilService.getProfileByGithubLogin(principal.getAttribute("login"));
        List<Thema> themen = themaService.themenVonProfil(profil.getId());
        model.addAttribute("profil", profil);
        model.addAttribute("themen", themen);
        model.addAttribute("nav", NavMapper.mapRole(principal));
        return "homepage/teacher_dashboard";
    }


    @GetMapping("/about")
    public String about(Model model, @AuthenticationPrincipal OAuth2User principal) {
        model.addAttribute("nav", NavMapper.mapRole(principal));
        return "homepage/about";
    }

    @GetMapping("/gh-login")
    public String github() {
        return "redirect:/oauth2/authorization/github";
    }
}