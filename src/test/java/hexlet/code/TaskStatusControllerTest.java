package hexlet.code;

import com.fasterxml.jackson.databind.ObjectMapper;
import hexlet.code.dto.TaskStatusCreateDTO;
import hexlet.code.dto.TaskStatusDTO;
import hexlet.code.dto.TaskStatusUpdateDto;
import hexlet.code.model.TaskStatus;
import hexlet.code.model.User;
import hexlet.code.repository.TaskStatusRepository;
import hexlet.code.repository.UserRepository;
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
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.assertj.core.api.Assertions.assertThat;

import org.springframework.http.MediaType;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;

import java.nio.charset.StandardCharsets;

@SpringBootTest
@AutoConfigureMockMvc
public class TaskStatusControllerTest {
    @Autowired
    private WebApplicationContext wac;
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper om;
    @Autowired
    private TaskStatusRepository taskStatusRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;
    private User testUser;
    private TaskStatus taskStatus;
    @BeforeEach
    public void setUp() {
        mockMvc = MockMvcBuilders.webAppContextSetup(wac)
                .defaultResponseCharacterEncoding(StandardCharsets.UTF_8).build();
        taskStatusRepository.deleteAll();
        //create test status
        taskStatus = new TaskStatus();
        taskStatus.setName("ToReview");
        taskStatus.setSlug("to_review");
        // Создание пользователя
        testUser = new User();
        testUser.setFirstName("John");
        testUser.setLastName("Smith");
        testUser.setEmail("john@example.com");
        testUser.setPassword(passwordEncoder.encode("password"));
        testUser = userRepository.save(testUser);
        taskStatus = taskStatusRepository.save(taskStatus);
    }
    @Test
    void testGetAllTaskStatus() throws Exception {
        var result = mockMvc.perform(get("/api/task_statuses")
                .with(jwt())).andExpect(status().isOk()).andReturn();
        var body = result.getResponse().getContentAsString();
        assertThatJson(body).isArray();
    }
    @Test
    void testGetTaskStatusById() throws Exception {
        var result = mockMvc.perform(get("/api/task_statuses/{id}",
                taskStatus.getId()).with(jwt())).andExpect(status().isOk()).andReturn();
        var body = result.getResponse().getContentAsString();
        assertThatJson(body).and(json -> json.node("name").isEqualTo(taskStatus.getName()),
                json -> json.node("slug").isEqualTo(taskStatus.getSlug()));
    }
    @Test
    void testCreateTaskStatus() throws Exception {
        TaskStatusCreateDTO dto = new TaskStatusCreateDTO();
        dto.setName("New");
        dto.setSlug("new");
        // Выполнение POST-запроса
        var response = mockMvc.perform(post("/api/task_statuses")
                        .with(jwt().jwt(jwt -> jwt.claim("sub", testUser.getEmail())))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(om.writeValueAsString(dto)))
                .andExpect(status().isCreated()) // Изменено на 201
                .andReturn();
        String body = response.getResponse().getContentAsString();
        System.out.println("Response: " + body);
        TaskStatusDTO actualStatus = om.readValue(body, TaskStatusDTO.class);
        TaskStatus expectedStatus = taskStatusRepository.findBySlug("new")
                .orElseThrow(() -> new AssertionError("TaskStatus not found in database"));
        assertThat(actualStatus.getId()).isEqualTo(expectedStatus.getId());
        assertThat(actualStatus.getName()).isEqualTo(expectedStatus.getName());
        assertThat(actualStatus.getSlug()).isEqualTo(expectedStatus.getSlug());
    }
    @Test
    void testUpdateTaskStatus() throws Exception {
        TaskStatusUpdateDto dto = new TaskStatusUpdateDto();
        dto.setName("newStatus");
        dto.setSlug("newStatus");

        var response = mockMvc.perform(put("/api/task_statuses/{id}", taskStatus.getId())
                        .with(jwt().jwt(jwt -> jwt.claim("sub", taskStatus.getId())))
                .contentType(MediaType.APPLICATION_JSON)
                .content(om.writeValueAsString(dto)))
                .andExpect(status().isOk()).andReturn();
        String body = response.getResponse().getContentAsString();
        System.out.println("Response: " + body);
        var updatedTask = taskStatusRepository.findBySlug("newStatus").orElseThrow();
        assertThat(updatedTask.getName()).isEqualTo("newStatus");
    }
    @Test
    void testDeleteTaskStatus() throws Exception {
        mockMvc.perform(delete("/api/task_statuses/{id}",
                taskStatus.getId()).with(jwt())).andExpect(status().isNoContent());
        assertThat(taskStatusRepository.existsById(taskStatus.getId())).isFalse();
    }
    @Test
    void testGetTaskNotFound() throws Exception {
        mockMvc.perform(get("/api/task_statuses/{id}", 9999)
                .with(jwt())).andExpect(status().isNotFound());
    }
}
