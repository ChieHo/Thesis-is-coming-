package de.propra.homepage;


import de.hhu.thesis_jensclicker.utility.NavMapper;
import de.hhu.thesis_jensclicker.utility.Roles.StudiAccess;
import de.hhu.thesis_jensclicker.utility.Roles.TeacherAccess;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@StudiAccess
@Controller()
public class HomeController {

    @GetMapping("/")
    public String index(Model model, @AuthenticationPrincipal OAuth2User principal) {
        // FIXME: if-statement probably not needed with proper security
        /*if (principal != null) {
            model.addAttribute("nav", NavMapper.mapRole(principal));
        } else {
            model.addAttribute("nav", NavMapper.LOGGED_IN);
        }*/
        model.addAttribute("nav", NavMapper.mapRole(principal));
        return "home";
    }

    @TeacherAccess
    @GetMapping("/teacher")
    public String homepage(Model model, @AuthenticationPrincipal OAuth2User principal) {
        model.addAttribute("nav", NavMapper.mapRole(principal));
        return "homeTeacher";
    }


    @GetMapping("/about")
    public String about(Model model, @AuthenticationPrincipal OAuth2User principal) {
        model.addAttribute("nav", NavMapper.mapRole(principal));
        return "about";
    }

    @GetMapping("/gh-login")
    public String github() {
        return "redirect:/oauth2/authorization/github";
    }
}
