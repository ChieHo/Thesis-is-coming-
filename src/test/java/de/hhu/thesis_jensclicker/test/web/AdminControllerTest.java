package de.hhu.thesis_jensclicker.test.web;


import de.hhu.thesis_jensclicker.helper.user.ProfilBuilder;
import de.hhu.thesis_jensclicker.helper.user.WithMockOAuth2User;
import de.hhu.thesis_jensclicker.security.SecurityConfig;
import de.propra.profil.application.repository.ProfilRepository;
import de.propra.profil.application.service.ProfilService;
import de.propra.profil.domain.model.profil.Profil;
import de.propra.profil.web.AdminController;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


@WebMvcTest(AdminController.class)
@Import({SecurityConfig.class})
public class AdminControllerTest {
    @Autowired
    MockMvc mvc;
    @MockitoBean
    ProfilService profilService;
    @MockitoBean
    ProfilRepository profilRepository;


    @Test
    @DisplayName("Person mit Admin Rolle hat Zugriff auf Admin Endpunkte")
    @WithMockOAuth2User(roles = {"ADMIN"})
    void test_01() throws Exception {
        mvc.perform(get("/admin/roles"))
                .andExpect(status().isOk())
                .andExpect(view().name("admin/roles"));
    }

    @Test
    @DisplayName("Person ohne Admin Rolle hat keinen Zugriff auf Admin Endpunkte")
    @WithMockOAuth2User()
    void test_02() throws Exception {
        mvc.perform(get("/admin/roles"))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("Person mit Admin Rolle kann GitHub User als Teacher hinzufügen")
    @WithMockOAuth2User(roles = {"ADMIN"})
    void test_03() throws Exception {
        mvc.perform(post("/admin/roles/assign")
                        .with(csrf())
                        .param("login", "uname"))
                .andExpect(redirectedUrl("/admin/roles/assign/success"));
    }

    @Test
    @DisplayName("Person mit Admin Rolle kann die Teacher Rolle entziehen")
    @WithMockOAuth2User(roles = {"ADMIN"})
    void test_04() throws Exception {
        Profil mockProfil = ProfilBuilder.aProfil();
        when(profilService.existsProfileByUUID(any())).thenReturn(true);
        when(profilService.profil(any())).thenReturn(mockProfil);

        mvc.perform(post("/admin/roles/revoke/00000000-0000-0000-0000-000000000000")
                        .with(csrf()))
                .andExpect(redirectedUrl("/admin/roles/revoke/success"));
    }

    @Test
    @DisplayName("Der Versuch, einem nicht existierendem Profil, die Teacher Rolle zu entziehen gibt 404 zurück")
    @WithMockOAuth2User(roles = {"ADMIN"})
    void test_05() throws Exception {
        mvc.perform(post("/admin/roles/revoke/00000000-0000-0000-0000-000000000000")
                        .with(csrf()))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("Die Route /admin/roles/assign liefert die korrekte Seite")
    @WithMockOAuth2User(roles = {"ADMIN"})
    void test_06() throws Exception {
        mvc.perform(get("/admin/roles/assign"))
                .andExpect(view().name("admin/role_assign"));
    }

}
