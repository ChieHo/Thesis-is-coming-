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
import de.propra.profil.web.ProfileController;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.util.Arrays;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ProfileController.class)
@Import({SecurityConfig.class})
class TeacherWebUITest {

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


    @BeforeEach
    void setUp() {
        mockProfil = ProfilBuilder.aProfil();

        when(profilService.getProfileByGithubLogin(anyString()))
                .thenReturn(mockProfil);

        when(profilService.existsProfileByGithubLogin(anyString()))
                .thenReturn(true);
    }

    @Test
    @DisplayName("Teacher hat zugriff auf die url /teacher/edit")
    @WithMockOAuth2User(login = "testuser", id = 12345, roles = {"TEACHER"})
    void test_01() throws Exception {
        mvc.perform(get("/teacher/edit"))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("profil"))
                .andExpect(view().name("profile_edit"));
    }

    @Test
    @DisplayName("Redirect auf /teacher/edit bei Link und Thema hinzufügen")
    @WithMockOAuth2User(login = "testuser", id = 12345, roles = {"TEACHER"})
    void test_02() throws Exception {
        mvc.perform(post("/teacher/edit")
                        .with(csrf())
                        .param("link", "https://hhu.de")
                        .param("fachgebiet", "something")
                        .param("action", "addFachgebiet"))
                .andExpect(redirectedUrl("/teacher/edit"));
    }

    @Test
    @DisplayName("Wenn kein name angegeben wird, wird die Seite neu zurückgegegben mit Fehlermeldung")
    @WithMockOAuth2User(login = "testuser", id = 12345, roles = {"TEACHER"})
    void test_03() throws Exception {
        MvcResult mvcResult = mvc.perform(post("/teacher/edit")
                        .with(csrf())
                        .param("action", "save_profile"))
                .andExpect(status().isOk())
                .andReturn();
        String html = mvcResult.getResponse().getContentAsString();

        assertThat(html).contains("Geben Sie bitte einen Namen ein");

    }

    @Test
    @DisplayName("Teacher hat zugriff auf die Seite /teacher/edit/files")
    @WithMockOAuth2User(login = "testuser", id = 12345, roles = {"TEACHER"})
    void test_04() throws Exception {
        mvc.perform(get("/teacher/edit/files"))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("profile"))
                .andExpect(view().name("upload_file"));
    }

    @Test
    @DisplayName("Teacher gibt file upload und redirect nach /teacher/edit/files")
    @WithMockOAuth2User(login = "testuser", id = 12345, roles = {"TEACHER"})
    void test_05() throws Exception {
        MockMultipartFile file =
                new MockMultipartFile(
                        "file",
                        "test.md",
                        "text/markdown",
                        "# Hello World".getBytes()
                );
        mvc.perform(multipart("/teacher/edit/files/upload")
                        .file(file)
                        .with(csrf())
                        .param("description", "beschreibung"))
                .andExpect(redirectedUrl("/teacher/edit/files"));
    }

    @Test
    @DisplayName("Button addLink führt auf die gleiche seite Zurück")
    @WithMockOAuth2User(login = "sth", roles = {"TEACHER"})
    void test_06() throws Exception {
        mvc.perform(post("/teacher/edit")
                        .with(csrf())
                        .param("link", "https://hhu.de")
                        .param("action", "addLink"))
                .andExpect(redirectedUrl("/teacher/edit"));
    }

    @Test
    @DisplayName("Wenn ungültiger Link, dann wird profile_edit view zurückgegegben + Fehlermeldung")
    @WithMockOAuth2User(login = "sth", roles = {"TEACHER"})
    void test_07() throws Exception {
        MvcResult result = mvc.perform(post("/teacher/edit")
                        .with(csrf())
                        .param("link", "<script>Hello World<script>")
                        .param("action", "addLink"))
                .andExpect(view().name("profile_edit"))
                .andExpect(status().isOk())
                .andReturn();
        String html = result.getResponse().getContentAsString();
        assertThat(html).contains("Nur sichere HTTPS-Links sind erlaubt (z.B. https://example.com)");
    }

    @Test
    @DisplayName("Wenn kein Fachgebiet eingegegben wird, dann gebe view profile_edit zurück")
    @WithMockOAuth2User(login = "sth", roles = {"TEACHER"})
    void test_08() throws Exception {
        MvcResult result = mvc.perform(post("/teacher/edit")
                        .with(csrf())
                        .param("action", "addFachgebiet"))
                .andExpect(view().name("profile_edit"))
                .andReturn();

        String html = result.getResponse().getContentAsString();

        assertThat(html).contains("Geben Sie einen Fachgebiet ein");
    }

    @Test
    @DisplayName("Teacher gibt einen file mit über 10_000_00 größe")
    @WithMockOAuth2User(login = "testuser", id = 12345, roles = {"TEACHER"})
    void test_09() throws Exception {
        int size = 100_0000_000;
        byte[] bytes = new byte[size];
        Arrays.fill(bytes, (byte) 'A');
        MockMultipartFile file =
                new MockMultipartFile(
                        "file",
                        "test.md",
                        "text/markdown",
                        bytes
                );
        mvc.perform(multipart("/teacher/edit/files/upload")
                        .file(file)
                        .with(csrf())
                        .param("description", "beschreibung"))
                .andExpect(status().isUnsupportedMediaType());
    }

    @Test
    @DisplayName("Unsupported Types werden nicht im upload akzeptiert")
    @WithMockOAuth2User(login = "gw", roles = {"TEACHER"})
    void test_10() throws Exception {
        MockMultipartFile file = new MockMultipartFile("file", "test.exe",
                "application/octet-stream",
                "dummy content".getBytes());

        mvc.perform(multipart("/teacher/edit/files/upload")
                        .file(file)
                        .with(csrf())
                        .param("description", "beschreibung"))
                .andExpect(status().isUnsupportedMediaType());

    }


}

