package hexlet.code;

import com.fasterxml.jackson.databind.ObjectMapper;
import hexlet.code.dto.TaskCreateDTO;
import hexlet.code.dto.TaskUpdateDTO;
import hexlet.code.model.Task;
import hexlet.code.model.TaskStatus;
import hexlet.code.model.User;
import hexlet.code.repository.TaskRepository;
import hexlet.code.repository.TaskStatusRepository;
import hexlet.code.repository.UserRepository;
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
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import java.nio.charset.StandardCharsets;
import static net.javacrumbs.jsonunit.assertj.JsonAssertions.assertThatJson;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class TaskControllerTest {
    @Autowired
    private WebApplicationContext wac;
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper om;
    @Autowired
    private TaskRepository taskRepository;
    @Autowired
    private TaskStatusRepository taskStatusRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;
    private User testUser;
    private TaskStatus taskStatus;
    private Task task;
    @BeforeEach
    public void setUp() {
        mockMvc = MockMvcBuilders.webAppContextSetup(wac)
                .defaultResponseCharacterEncoding(StandardCharsets.UTF_8).build();
        taskRepository.deleteAll();
        taskStatusRepository.deleteAll();
        userRepository.deleteAll();
        //create test status
        taskStatus = new TaskStatus();
        taskStatus.setName("ToReview");
        taskStatus.setSlug("to_review");
        taskStatus = taskStatusRepository.save(taskStatus);
        // Сохраняем тестового пользователя
        testUser = new User();
        testUser.setFirstName("Jane");
        testUser.setLastName("Doe");
        testUser.setEmail("jane@example.com");
        testUser.setPassword(passwordEncoder.encode("secret"));
        testUser = userRepository.save(testUser);
        //create test
        task = new Task();
        task.setName("Test title");
        task.setDescription("Test content");
        task.setTaskStatus(taskStatus);
        task.setIndex(12);
        task.setAssignee(testUser);
        task = taskRepository.save(task);
    }
    @Test
    void testGetAllTaskStatus() throws Exception {
        var result = mockMvc.perform(get("/api/tasks")
                .with(jwt())).andExpect(status().isOk()).andReturn();
        var body = result.getResponse().getContentAsString();
        assertThatJson(body).isArray();
    }
    @Test
    void testGetTaskById() throws Exception {
        // Получаем задачу по id
        var result = mockMvc.perform(get("/api/tasks/{id}",
                task.getId()).with(jwt().jwt(jwt ->
                jwt.claim("sub", testUser.getEmail())))).andExpect(status().isOk()).andReturn();
        var body = result.getResponse().getContentAsString();
        // Проверяем, что возвращаемая задача соответствует ожидаемому
        assertThatJson(body).node("name").isEqualTo("Test title");
        assertThatJson(body).node("description").isEqualTo("Test content");
        assertThatJson(body).node("taskStatus.name").isEqualTo("ToReview");
    }
    @Test
    void testCreateTask() throws Exception {
        Long id = testUser.getId();
        // Данные для новой задачи
        TaskCreateDTO testTask = new TaskCreateDTO();
        testTask.setIndex(12);
        testTask.setAssigneeId(testUser.getId());
        testTask.setTitle("Test title");
        testTask.setContent("Test content");
        testTask.setStatus("ToReview");
        // Отправляем запрос на создание задачи
        var result = mockMvc.perform(post("/api/tasks")
                .with(jwt().jwt(jwt ->
                        jwt.claim("sub", testUser.getEmail())))
                .contentType(MediaType.APPLICATION_JSON)
                .content(om.writeValueAsString(testTask)))
                .andExpect(status().isCreated()).andReturn();
        var body = result.getResponse().getContentAsString();
        // Проверяем, что возвращаемая задача соответствует ожидаемой
        assertThatJson(body).node("name").isEqualTo("Test title");
        assertThatJson(body).node("description").isEqualTo("Test content");
    }
    @Test
    void testUpdateTask() throws Exception {
        TaskUpdateDTO updatedTask = new TaskUpdateDTO();
        updatedTask.setTitle("Updated Task Name");
        updatedTask.setContent("Updated Description");

        var result = mockMvc.perform(put("/api/tasks/{id}",
                task.getId()).with(jwt()
                .jwt(jwt -> jwt.claim("sub", testUser.getEmail())))
                .contentType(MediaType.APPLICATION_JSON)
                .content(om.writeValueAsString(updatedTask))).andExpect(status().isOk()).andReturn();
        var body = result.getResponse().getContentAsString();
        // Проверяем, что имя задачи обновлено
        assertThatJson(body).node("name").isEqualTo("Updated Task Name");
        assertThatJson(body).node("description").isEqualTo("Updated Description");
    }
    @Test
    void testDeleteTask() throws Exception {
        // Удаляем задачу
        mockMvc.perform(delete("/api/tasks/{id}", task.getId()).with(jwt()
                .jwt(jwt -> jwt.claim("sub", testUser.getEmail())))).andExpect(status().isNoContent());
        // Проверяем, что задача была удалена из базы
        mockMvc.perform(get("/api/tasks/{id}", task.getId()).with(jwt()
                .jwt(jwt -> jwt.claim("sub", testUser.getEmail())))).andExpect(status().isNotFound());
    }
    @Test
    void testFilterByTitleCont() throws Exception {
        // Дополнительная задача, чтобы убедиться, что фильтрация работает
        Task extraTask = new Task();
        extraTask.setName("Create login form");
        extraTask.setDescription("Another task");
        extraTask.setTaskStatus(taskStatus);
        extraTask.setAssignee(testUser);
        extraTask.setIndex(99);
        taskRepository.save(extraTask);
        // Фильтрация по слову "Create"
        var result = mockMvc.perform(get("/api/tasks")
                        .param("titleCont", "Create")
                        .with(jwt().jwt(jwt -> jwt.claim("sub", testUser.getEmail()))))
                .andExpect(status().isOk())
                .andReturn();
        var body = result.getResponse().getContentAsString(StandardCharsets.UTF_8);
        // Проверяем, что в ответе только одна задача
        assertThatJson(body).isArray().hasSize(1);
        assertThatJson(body).node("[0].name").isEqualTo("Create login form");
    }
    @Test
    void testFilterByAssigneeAndStatus() throws Exception {
        // Фильтрация по существующему исполнителю и статусу
        var result = mockMvc.perform(get("/api/tasks")
                        .param("assigneeId", testUser.getId().toString())
                        .param("status", "to_review")
                        .with(jwt().jwt(jwt -> jwt.claim("sub", testUser.getEmail()))))
                .andExpect(status().isOk())
                .andReturn();
        var body = result.getResponse().getContentAsString(StandardCharsets.UTF_8);
        // Ожидаем, что вернётся исходная задача
        assertThatJson(body).isArray().hasSize(1);
        assertThatJson(body).node("[0].name").isEqualTo("Test title");
    }
}
