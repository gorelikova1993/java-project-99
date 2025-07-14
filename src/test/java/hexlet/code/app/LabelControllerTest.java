package hexlet.code.app;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import hexlet.code.app.dto.LabelDTO;
import hexlet.code.app.mapper.LabelMapper;
import hexlet.code.app.model.User;
import hexlet.code.app.model.Label;
import hexlet.code.app.repository.LabelRepository;
import hexlet.code.app.repository.UserRepository;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.util.List;

import static net.javacrumbs.jsonunit.assertj.JsonAssertions.assertThatJson;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class LabelControllerTest {
    @Autowired
    private WebApplicationContext wac;
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper om;
    @Autowired
    private LabelMapper labelMapper;
    @Autowired
    private LabelRepository labelRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;
    private User testUser;
    private Label label;
    @BeforeEach
    public void setUp() {
        mockMvc = MockMvcBuilders.webAppContextSetup(wac)
                .defaultResponseCharacterEncoding(StandardCharsets.UTF_8)
                .build();
        labelRepository.deleteAll();
        userRepository.deleteAll();
        // Создание пользователя
        testUser = new User();
        testUser.setFirstName("John");
        testUser.setLastName("Smith");
        testUser.setEmail("john@example.com");
        testUser.setPassword(passwordEncoder.encode("password"));
        testUser = userRepository.save(testUser);
        // Создание метки
        label = new Label();
        label.setName("bug");
        label = labelRepository.save(label);
    }
    @Test
    void testIndex() throws Exception {
        var response = mockMvc.perform(get("/api/labels")
                        .with(jwt().jwt(jwt -> jwt.claim("sub", testUser.getEmail()))))
                .andExpect(status().isOk())
                .andReturn();
        var body = response.getResponse().getContentAsString();
        List<LabelDTO> labelDTOS = om.readValue(body, new TypeReference<>() { });
        var actual = labelDTOS.stream().map(labelMapper::fromDto).toList();
        var expected = labelRepository.findAll();
        Assertions.assertThat(actual).containsExactlyInAnyOrderElementsOf(expected);
    }
    @Test
    void testShow() throws Exception {
        var response = mockMvc.perform(get("/api/labels/" + label.getId())
                        .with(jwt().jwt(jwt -> jwt.claim("sub", testUser.getEmail()))))
                .andExpect(status().isOk())
                .andReturn();
        var body = response.getResponse().getContentAsString();
        assertThatJson(body).node("name").isEqualTo("bug");
    }
    @Test
    void testCreate() throws Exception {
        Label payload = new Label();
        payload.setName("feature");
        payload.setCreatedAt(LocalDate.now());
        var response = mockMvc.perform(post("/api/labels")
                        .with(jwt().jwt(jwt -> jwt.claim("sub", testUser.getEmail())))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(om.writeValueAsString(payload)))
                .andExpect(status().isCreated())
                .andReturn();
        var body = response.getResponse().getContentAsString();
        assertThatJson(body).node("name").isEqualTo("feature");
    }
    @Test
    void testUpdate() throws Exception {
        Label payload = new Label();
        payload.setName("enhancement");
        payload.setCreatedAt(LocalDate.now());
        var response = mockMvc.perform(put("/api/labels/" + label.getId())
                        .with(jwt().jwt(jwt -> jwt.claim("sub", testUser.getEmail())))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(om.writeValueAsString(payload)))
                .andExpect(status().isOk())
                .andReturn();
        var body = response.getResponse().getContentAsString();
        assertThatJson(body).node("name").isEqualTo("enhancement");
    }
    @Test
    void testDelete() throws Exception {
        mockMvc.perform(delete("/api/labels/" + label.getId())
                        .with(jwt().jwt(jwt -> jwt.claim("sub", testUser.getEmail()))))
                .andExpect(status().isNoContent());
        mockMvc.perform(get("/api/labels/" + label.getId())
                        .with(jwt().jwt(jwt -> jwt.claim("sub", testUser.getEmail()))))
                .andExpect(status().isNotFound());
    }
}

