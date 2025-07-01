package hexlet.code.app.controller;

import hexlet.code.app.dto.TaskCreateDTO;
import hexlet.code.app.dto.TaskUpdateDTO;
import hexlet.code.app.exception.ResourceNotFoundException;
import hexlet.code.app.mapper.TaskMapper;
import hexlet.code.app.model.Task;
import hexlet.code.app.repository.TaskRepository;
import hexlet.code.app.specification.TaskSpecification;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import static org.springframework.data.jpa.domain.Specification.allOf;

import java.util.List;

@RestController
@RequestMapping("/api/tasks")
@RequiredArgsConstructor
public class TaskController {
    private final TaskRepository taskRepository;
    private final TaskMapper taskMapper;
    @GetMapping
    public List<Task> getAll(@RequestParam(required = false) String titleCont,
                             @RequestParam(required = false) Long assigneeId,
                             @RequestParam(required = false) String status,
                             @RequestParam(required = false) Long labelId) {
        Specification<Task> spec = allOf(
                TaskSpecification.hasTitleContaining(titleCont),
                TaskSpecification.hasAssigneeId(assigneeId),
                TaskSpecification.hasStatusSlug(status),
                TaskSpecification.hasLabelId(labelId)
        );
        return taskRepository.findAll(spec);
    }
    @GetMapping("/{id}")
    public ResponseEntity<Task> get(@PathVariable Long id) {
        return taskRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
    @PostMapping
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Task> create(@Valid @RequestBody TaskCreateDTO taskCreateDTO) {
        Task task = taskMapper.toEntity(taskCreateDTO);
        return ResponseEntity.ok(taskRepository.save(task));
    }
    @PutMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Task> update(@PathVariable Long id, @Valid @RequestBody TaskUpdateDTO taskUpdateDTO) {
        Task task = taskRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Task not found with id " + id));
        task = taskMapper.toEntity(taskUpdateDTO, task);
        return ResponseEntity.ok(taskRepository.save(task));
    }
    @DeleteMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        if (!taskRepository.existsById(id)) {
            throw new ResourceNotFoundException("Task not found with id " + id); // Проверка существования задачи
        }
        taskRepository.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
