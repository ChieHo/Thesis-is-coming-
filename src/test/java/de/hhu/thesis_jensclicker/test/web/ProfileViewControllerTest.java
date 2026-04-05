package de.hhu.thesis_jensclicker.test.web;


import de.hhu.thesis_jensclicker.CSModules.CSModuleRepository;
import de.hhu.thesis_jensclicker.CSModules.CSModuleService;
import de.hhu.thesis_jensclicker.DAO.ThesisCSModule.ThesisCSModuleRepository;
import de.hhu.thesis_jensclicker.DAO.ThesisCSModule.ThesisCSModuleService;
import de.hhu.thesis_jensclicker.DAO.ThesisFachgebiet.ThesisFachgebietRepository;
import de.hhu.thesis_jensclicker.DAO.ThesisFachgebiet.ThesisFachgebietService;
import de.hhu.thesis_jensclicker.Fachgebiete.FachgebietService;
import de.hhu.thesis_jensclicker.File.FileRepository;
import de.hhu.thesis_jensclicker.Link.LinkRepository;
import de.hhu.thesis_jensclicker.Thesis.ThesisRepository;
import de.hhu.thesis_jensclicker.configuration.AppUserService;
import de.hhu.thesis_jensclicker.helper.user.ProfilBuilder;
import de.hhu.thesis_jensclicker.helper.user.WithMockOAuth2User;
import de.hhu.thesis_jensclicker.security.SecurityConfig;
import de.propra.profil.application.repository.ProfilRepository;
import de.propra.profil.application.service.ProfilService;
import de.propra.profil.domain.model.profil.Profil;
import de.propra.profil.web.ProfileViewController;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ProfileViewController.class)
@Import({SecurityConfig.class})
public class ProfileViewControllerTest {
    @Autowired
    MockMvc mvc;
    @MockitoBean
    ProfilService profilService;
    @MockitoBean
    ProfilRepository profilRepository;
    @MockitoBean
    AppUserService appUserService;

    @MockitoBean
    CSModuleRepository csModuleRepository;
    @MockitoBean
    CSModuleService csModuleService;
    @MockitoBean
    ThesisCSModuleRepository thesisCSModuleRepository;
    @MockitoBean
    ThesisCSModuleService s;
    @MockitoBean
    ThesisFachgebietService thesisFachgebietService;
    @MockitoBean
    ThesisFachgebietRepository thesisFachgebietRepository;
    @MockitoBean
    FachgebietService fachgebietService;
    @MockitoBean
    FileRepository fileRepository;
    @MockitoBean
    LinkRepository linkRepository;
    @MockitoBean
    ThesisRepository thesisRepository;

    private Profil mockProfil;
    private Profil mockProfilB;

    @BeforeEach
    void setUp() {
        mockProfil = ProfilBuilder.aProfil();
        mockProfilB = ProfilBuilder.bProfil();

        when(profilService.getProfileByGithubLogin("testuser"))
                .thenReturn(mockProfil);

        when(profilService.existsProfileByGithubLogin("testuser"))
                .thenReturn(true);

        when(profilService.profil(mockProfil.getId()))
                .thenReturn(mockProfil);

        when(profilService.profil(mockProfilB.getId()))
                .thenReturn(mockProfilB);
    }

    @Test
    @DisplayName("Die Route /view/profile/all liefert die korrekte Seite")
    @WithMockOAuth2User(roles = {"STUDI"})
    void test_01() throws Exception {
        mvc.perform(get("/view/profile/all"))
                .andExpect(view().name("view-profile/list"));
    }

    @Test
    @DisplayName("Die Seite /view/profile/all liefert alle Profile zurück")
    @WithMockOAuth2User(roles = {"STUDI"})
    void test_02() throws Exception {
        MvcResult result = mvc.perform(get("/view/profile/all"))
                .andReturn();
        String html = result.getResponse().getContentAsString();
        assertThat(html.contains("Test User"));
    }

    @Test
    @DisplayName("Die Route /view/profile/search ist ein Redirect auf die korrekte Seite wenn query nicht angegeben ist")
    @WithMockOAuth2User(roles = {"STUDI"})
    void test_03() throws Exception {
        mvc.perform(get("/view/profile/search"))
                .andExpect(redirectedUrl("/view/profile/all"));
    }

    @Test
    @DisplayName("Die Suche liefert korrekte Ergebnisse")
    @WithMockOAuth2User(roles = {"STUDI"})
    void test_04() throws Exception {
        MvcResult result = mvc.perform(get("/view/profile/search").requestAttr("query", "test"))
                .andReturn();
        String html = result.getResponse().getContentAsString();
        assertThat(html.contains("Test User"));
    }

    @Test
    @DisplayName("Die Profilansicht funktioniert")
    @WithMockOAuth2User(roles = {"STUDI"})
    void test_05() throws Exception {
        MvcResult result = mvc.perform(get("/view/profile/" + mockProfil.getId()).requestAttr("query", "test"))
                .andExpect(status().isOk())
                .andReturn();
        String html = result.getResponse().getContentAsString();
        assertThat(html.contains("Test User"));
    }

    @Test
    @DisplayName("Die Dateiansicht funktioniert")
    @WithMockOAuth2User(roles = {"STUDI"})
    void test_06() throws Exception {
        String filename = mockProfilB.getProfileFiles().stream().findFirst().get().getName();
        MvcResult result = mvc.perform(get("/view/profile/" + mockProfilB.getId() + "/" + filename))
                .andReturn();

        String html = result.getResponse().getContentAsString();
        assertThat(html.contains(filename));
    }

    @Test
    @DisplayName("Dateien können heruntergeladen werden")
    @WithMockOAuth2User(roles = {"STUDI"})
    void test_07() throws Exception {
        String filename = mockProfilB.getProfileFiles().stream().findFirst().get().getName();
        MvcResult result = mvc.perform(get("/view/profile/" + mockProfilB.getId() + "/" + filename + "/download"))
                .andReturn();

        assertThat(result.getResponse().getContentLength() == 1234L);
        assertThat(result.getResponse().getHeader("Content-Type").equals("text/plain"));

    }

    @Test
    @DisplayName("Bei einer nicht gefundenen Datei wird 'nicht gefunden' zurückgegeben")
    @WithMockOAuth2User(roles = {"STUDI"})
    void test_08() throws Exception {
        MvcResult result = mvc.perform(get("/view/profile/" + mockProfil.getId() + "/invalid"))
                .andExpect(status().isNotFound())
                .andReturn();
        String html = result.getResponse().getContentAsString();
    }

}
