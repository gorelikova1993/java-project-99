package hexlet.code.app.controller;

import hexlet.code.app.dto.TaskStatusCreateDTO;
import hexlet.code.app.dto.TaskStatusUpdateDto;
import hexlet.code.app.model.TaskStatus;
import hexlet.code.app.repository.TaskStatusRepository;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.List;

@RestController
@RequestMapping("/api/task_statuses")
@RequiredArgsConstructor
public class TaskStatusController {
    private final TaskStatusRepository repository;
    @GetMapping
    public List<TaskStatus> getAll() {
        return repository.findAll();
    }
    @GetMapping("/{id}")
    public ResponseEntity<TaskStatus> get(@PathVariable Long id) {
        return repository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
    @PostMapping
    public ResponseEntity<TaskStatus> create(@Valid @RequestBody TaskStatusCreateDTO dto) {
        if (repository.findBySlug(dto.getSlug()).isPresent()) {
            throw new IllegalArgumentException("Slug already exists");
        }
        TaskStatus status = new TaskStatus();
        status.setName(dto.getName());
        status.setSlug(dto.getSlug());
        return ResponseEntity.status(HttpStatus.CREATED).body(repository.save(status));
    }
    @PutMapping("/{id}")
    public ResponseEntity<TaskStatus> update(@PathVariable Long id, @RequestBody TaskStatusUpdateDto dto) {
        return repository.findById(id).map(status -> {
            if (dto.getName() != null) {
                status.setName(dto.getName());
            }
            if (dto.getSlug() != null) {
                status.setSlug(dto.getSlug());
            }
            return ResponseEntity.ok(repository.save(status));
        }).orElse(ResponseEntity.notFound().build());
    }
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        repository.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
