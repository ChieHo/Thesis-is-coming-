package de.hhu.thesis_jensclicker.test.web;



import de.hhu.thesis_jensclicker.helper.user.ProfilBuilder;
import de.hhu.thesis_jensclicker.helper.user.WithMockOAuth2User;
import de.hhu.thesis_jensclicker.security.SecurityConfig;

import de.propra.profil.application.repository.ProfilRepository;
import de.propra.profil.application.service.ProfilService;
import de.propra.profil.domain.model.profil.Profil;
import de.propra.profil.web.HomeController;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

@WebMvcTest(controllers = {HomeController.class})
@Import({SecurityConfig.class})
public class HomePageTest {

    @Autowired
    MockMvc mvc;
    @MockitoBean
    ProfilService profilService;
    @MockitoBean
    ProfilRepository profilRepository;


    @Test
    @DisplayName("angemeldete Github User haben einen Zugriff auf die homepage seite")
    @WithMockOAuth2User(login = "snt", roles = {"STUDI"})
    void test_01() throws Exception {
        mvc.perform(get("/"))
                .andExpect(status().isOk())
                .andExpect(view().name("home"));
    }

    @Test
    @DisplayName("angemeldete Github User haben zugriff auf die about Seite")
    @WithMockOAuth2User(login = "snt", roles = {"STUDI"})
    void test_02() throws Exception {
        mvc.perform(get("/about"))
                .andExpect(status().isOk())
                .andExpect(view().name("about"));
    }

    @Test
    @DisplayName("/teacher für studi nicht erreichbar")
    @WithMockOAuth2User(login = "snt", roles = {"STUDI"})
    void test_03() throws Exception {
        // 403 is never returned for security reasons
        mvc.perform(get("/teacher"))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("Teacher hat zugriff auf die /teacher Seite")
    @WithMockOAuth2User(login = "teacher_Conrad", roles = {"TEACHER"})
    void test_04() throws Exception {
        mvc.perform(get("/teacher"))
                .andExpect(status().isOk())
                .andExpect(view().name("homeTeacher"));
    }

    @Test
    @DisplayName("/view/profile/all gibt alle Profile an")
    @WithMockOAuth2User(login = "snt", roles = {"STUDI"})
    void test_05() throws Exception {
        Profil profil = ProfilBuilder.aProfil();
        profilService.profilHinzufuegen(profil);
        mvc.perform(get("/view/profile/all"))
                .andExpect(status().isOk())
                .andExpect(view().name("view-profile/list"));
    }

    @Test
    @DisplayName("Jeder hat zugriff auf /gh-login")
    @WithMockOAuth2User(login = "snt", roles = {"STUDI"})
    void test_6() throws Exception {
        mvc.perform(get("/gh-login"))
                .andExpect(status().is3xxRedirection());
    }

    @Test
    @DisplayName("Unbekannter hat keinen Zugriff auf die Seite")
    void test_7() throws Exception {
        mvc.perform(get("/")).andExpect(status().is3xxRedirection());
    }

}
