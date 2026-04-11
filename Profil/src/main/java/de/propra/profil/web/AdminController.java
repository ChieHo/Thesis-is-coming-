package de.propra.profil.web;

import de.hhu.thesis_jensclicker.utility.Roles.AdminAccess;
import de.propra.profil.application.service.ProfilService;
import de.propra.profil.domain.model.profil.Profil;
import de.propra.profil.web.ErrorPages.CustomErrors.ProfileNotFoundException;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.UUID;

@Controller
@AdminAccess
public class AdminController {

    private final ProfilService profilService;

    public AdminController(ProfilService profilService) {
        this.profilService = profilService;
    }
    @ModelAttribute
    public void addNav(Model model) {
        model.addAttribute("nav", "admin");
    }

    @GetMapping("/admin")
    public String adminDashboard(Model model) {
        model.addAttribute("profiles", profilService.alleProfil());
        return "homepage/admin_dashboard";
    }

    @GetMapping("/admin/roles")
    public String roles() {
        return "admin/role_assign";
    }

    @GetMapping("/admin/roles/assign")
    public String roleAssignView() {
        return "admin/role_assign";
    }

    @PostMapping("/admin/roles/assign")
    public String roleAssign(Model model, @Param("login") String login, RedirectAttributes redir) {
        if (login == null || login.isBlank()) {
            model.addAttribute("err", true);
            model.addAttribute("msg", "Leere Eingabe ist nicht erlaubt");
            return "admin/role_assign";
        }

        if (profilService.existsProfileByGithubLogin(login)) {
            model.addAttribute("err", true);
            model.addAttribute("msg", login + " hat bereits ein Profil.");
            return "admin/role_assign";
        }

        profilService.profilHinzufuegen(new Profil("placeholder_" + login, "", login));
        Profil profil = profilService.getProfileByGithubLogin(login);
        redir.addFlashAttribute("profile", profil);
        return "redirect:/admin/roles/assign/success";
    }

    @GetMapping("/admin/roles/assign/success")
    public String roleAssignSuccess(Model model) {
        if (!model.containsAttribute("profile")) {
            return "redirect:/admin/roles/assign";
        }
        return "admin/role_assign_success";
    }

    @GetMapping("/admin/roles/revoke")
    public String roleRevokeList(Model model) {
        model.addAttribute("profiles", profilService.alleProfil());
        return "admin/role_revoke_list";
    }

    @GetMapping("/admin/roles/revoke/{id}")
    public String roleRevokeConfirm(Model model, @PathVariable("id") UUID id) {
        if (!profilService.existsProfileByUUID(id)) {
            throw new ProfileNotFoundException(id);
        }
        model.addAttribute("profile", profilService.profil(id));
        return "admin/role_revoke_confirm";
    }

    @PostMapping("/admin/roles/revoke/{id}")
    public String roleRevoke(@PathVariable("id") UUID id, RedirectAttributes redir) {
        if (!profilService.existsProfileByUUID(id)) {
            throw new ProfileNotFoundException(id);
        }
        Profil profil = profilService.profil(id);
        redir.addFlashAttribute("name", profil.getName());
        redir.addFlashAttribute("login", profil.getGithubLogin());
        redir.addFlashAttribute("id", id);
        profilService.deleteProfileByUUID(id);
        return "redirect:/admin/roles/revoke/success";
    }

    @GetMapping("/admin/roles/revoke/success")
    public String roleRevokeSuccess(Model model) {
        if (!model.containsAttribute("id")) {
            return "redirect:/admin/roles/revoke";
        }
        return "admin/role_revoke_success";
    }

}
