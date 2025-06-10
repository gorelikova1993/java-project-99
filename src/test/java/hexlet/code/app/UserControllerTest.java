package hexlet.code.app;

import com.fasterxml.jackson.databind.ObjectMapper;
import hexlet.code.app.dto.UserCreateDto;
import hexlet.code.app.model.User;
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
import static net.javacrumbs.jsonunit.assertj.JsonAssertions.assertThatJson;
import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;



import org.springframework.http.MediaType;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;

import java.nio.charset.StandardCharsets;

@SpringBootTest
@AutoConfigureMockMvc
public class UserControllerTest {
    
    @Autowired
    private WebApplicationContext wac;
    
    @Autowired
    private MockMvc mockMvc;
    
    @Autowired
    private ObjectMapper om;
    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private PasswordEncoder passwordEncoder;
    
    private User testUser;
    
    @BeforeEach
    public void setUp() {
        mockMvc = MockMvcBuilders.webAppContextSetup(wac)
                .defaultResponseCharacterEncoding(StandardCharsets.UTF_8)
                .build();
        
        userRepository.deleteAll(); // очистка БД
        
        // Сохраняем тестового пользователя
        testUser = new User();
        testUser.setFirstName("Jane");
        testUser.setLastName("Doe");
        testUser.setEmail("jane@example.com");
        testUser.setPassword(passwordEncoder.encode("secret"));
        testUser = userRepository.save(testUser);
    }
    
    @Test
    void testGetAllUsers() throws Exception {
        var result = mockMvc.perform(get("/api/users").with(jwt()))
                .andExpect(status().isOk())
                .andReturn();
        
        var body = result.getResponse().getContentAsString();
        assertThatJson(body).isArray();
    }
    
    @Test
    void testGetUserById() throws Exception {
        var result = mockMvc.perform(get("/api/users/{id}", testUser.getId()).with(jwt()))
                .andExpect(status().isOk())
                .andReturn();
        
        var body = result.getResponse().getContentAsString();
        assertThatJson(body).and(
                json -> json.node("email").isEqualTo(testUser.getEmail()),
                json -> json.node("firstName").isEqualTo(testUser.getFirstName())
        );
    }
    
    @Test
    void testCreateUser() throws Exception {
        var dto = new UserCreateDto();
        dto.setEmail("new@example.com");
        dto.setPassword("password");
        dto.setFirstName("New");
        dto.setLastName("User");
        
        var request = post("/api/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(om.writeValueAsString(dto));
        
        mockMvc.perform(request)
                .andExpect(status().isCreated());
        
        var user = userRepository.findByEmail("new@example.com").orElseThrow();
        assertThat(user.getFirstName()).isEqualTo("New");
        assertThat(passwordEncoder.matches("password", user.getPassword())).isTrue();
    }
    
    @Test
    void testUpdateUser() throws Exception {
        var payload = """
                {
                    "firstName": "Updated",
                    "lastName": "Name"
                }
                """;
        
        var request = put("/api/users/{id}", testUser.getId())
                .with(jwt())
                .contentType(MediaType.APPLICATION_JSON)
                .content(payload);
        
        mockMvc.perform(request)
                .andExpect(status().isOk());
        
        var updatedUser = userRepository.findById(testUser.getId()).orElseThrow();
        assertThat(updatedUser.getFirstName()).isEqualTo("Updated");
        assertThat(updatedUser.getLastName()).isEqualTo("Name");
    }
    
    @Test
    void testDeleteUser() throws Exception {
        mockMvc.perform(delete("/api/users/{id}", testUser.getId()).with(jwt()))
                .andExpect(status().isNoContent());
        
        assertThat(userRepository.existsById(testUser.getId())).isFalse();
    }
    
    @Test
    void testGetUserNotFound() throws Exception {
        mockMvc.perform(get("/api/users/{id}", 9999).with(jwt()))
                .andExpect(status().isNotFound());
    }
    
    
}
