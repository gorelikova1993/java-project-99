package hexlet.code.app;

import com.fasterxml.jackson.databind.ObjectMapper;
import hexlet.code.app.model.User;
import hexlet.code.app.model.Label;
import hexlet.code.app.repository.LabelRepository;
import hexlet.code.app.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import java.nio.charset.StandardCharsets;

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
        assertThatJson(body).isArray().hasSize(1);
        assertThatJson(body).node("[0].name").isEqualTo("bug");
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
        var payload = """
                {
                    "name": "feature"
                }
                """;
        var response = mockMvc.perform(post("/api/labels")
                        .with(jwt().jwt(jwt -> jwt.claim("sub", testUser.getEmail())))
                        .contentType("application/json")
                        .content(payload))
                .andExpect(status().isCreated())
                .andReturn();
        var body = response.getResponse().getContentAsString();
        assertThatJson(body).node("name").isEqualTo("feature");
    }
    @Test
    void testUpdate() throws Exception {
        var payload = """
                {
                    "name": "enhancement"
                }
                """;
        var response = mockMvc.perform(put("/api/labels/" + label.getId())
                        .with(jwt().jwt(jwt -> jwt.claim("sub", testUser.getEmail())))
                        .contentType("application/json")
                        .content(payload))
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

