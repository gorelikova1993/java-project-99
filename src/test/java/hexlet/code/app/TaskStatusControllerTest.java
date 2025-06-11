package hexlet.code.app;

import com.fasterxml.jackson.databind.ObjectMapper;
import hexlet.code.app.dto.TaskStatusCreateDTO;
import hexlet.code.app.model.TaskStatus;
import hexlet.code.app.repository.TaskStatusRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
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
    private TaskStatus taskStatus;
    
    @BeforeEach
    public void setUp() {
        mockMvc = MockMvcBuilders.webAppContextSetup(wac).defaultResponseCharacterEncoding(StandardCharsets.UTF_8).build();
        taskStatusRepository.deleteAll();
        //create test status
        taskStatus = new TaskStatus();
        taskStatus.setName("ToReview");
        taskStatus.setSlug("to_review");
        taskStatus = taskStatusRepository.save(taskStatus);
    }
    
    @Test
    void testGetAllTaskStatus() throws Exception {
        var result = mockMvc.perform(get("/api/task_statuses").with(jwt())).andExpect(status().isOk()).andReturn();
        var body = result.getResponse().getContentAsString();
        assertThatJson(body).isArray();
    }
    
    @Test
    void testGetTaskStatusById() throws Exception {
        var result = mockMvc.perform(get("/api/task_statuses/{id}", taskStatus.getId()).with(jwt())).andExpect(status().isOk()).andReturn();
        var body = result.getResponse().getContentAsString();
        assertThatJson(body).and(json -> json.node("name").isEqualTo(taskStatus.getName()), json -> json.node("slug").isEqualTo(taskStatus.getSlug()));
    }
    
    @Test
    void testCreateTaskStatus() throws Exception {
        var dto = new TaskStatusCreateDTO();
        dto.setName("New");
        dto.setSlug("new");
        var request = post("/api/task_statuses").contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsString(dto));
        mockMvc.perform(request).andExpect(status().isOk());
        var taskStatus = taskStatusRepository.findBySlug("new").orElseThrow();
        assertThat(taskStatus.getName()).isEqualTo("New");
    }
    
    @Test
    void testUpdateTaskStatus() throws Exception {
        var payload = """
                {
                    "name": "newStatus"
                }
                """;
        var request = put("/api/task_statuses/{id}", taskStatus.getId()).with(jwt()).contentType(MediaType.APPLICATION_JSON).content(payload);
        mockMvc.perform(request).andExpect(status().isOk());
        var updatedTask = taskStatusRepository.findBySlug(taskStatus.getSlug()).orElseThrow();
        assertThat(updatedTask.getName()).isEqualTo("newStatus");
    }
    
    @Test
    void testDeleteTaskStatus() throws Exception {
        mockMvc.perform(delete("/api/task_statuses/{id}", taskStatus.getId()).with(jwt())).andExpect(status().isNoContent());
        
        assertThat(taskStatusRepository.existsById(taskStatus.getId())).isFalse();
    }
    
    @Test
    void testGetTaskNotFound() throws Exception {
        mockMvc.perform(get("/api/task_statuses/{id}", 9999).with(jwt())).andExpect(status().isNotFound());
    }
}