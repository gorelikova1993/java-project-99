package hexlet.code.controller;

import hexlet.code.dto.TaskStatusCreateDTO;
import hexlet.code.dto.TaskStatusDTO;
import hexlet.code.dto.TaskStatusUpdateDto;
import hexlet.code.mapper.TaskStatusMapper;
import hexlet.code.model.TaskStatus;
import hexlet.code.repository.TaskStatusRepository;
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
    private  final TaskStatusMapper taskStatusMapper;
    @GetMapping
    public ResponseEntity<List<TaskStatusDTO>>  getAll() {
        var statuses = repository.findAll()
                .stream()
                .map(taskStatusMapper::toDto)
                .toList();
        
        var headers = new org.springframework.http.HttpHeaders();
        headers.add("X-Total-Count", String.valueOf(statuses.size()));
        headers.add("Access-Control-Expose-Headers", "X-Total-Count");
        
        return ResponseEntity.ok().headers(headers).body(statuses);
    }
    @GetMapping("/{id}")
    public ResponseEntity<TaskStatusDTO> get(@PathVariable Long id) {
        return repository.findById(id)
                .map(taskStatusMapper::toDto)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
    @PostMapping
    public ResponseEntity<TaskStatusDTO> create(@Valid @RequestBody TaskStatusCreateDTO dto) {
        TaskStatus entity = taskStatusMapper.toEntity(dto);
        TaskStatus saved = repository.save(entity);
        return ResponseEntity.status(HttpStatus.CREATED).body(taskStatusMapper.toDto(saved));
    }
    @PutMapping("/{id}")
    public ResponseEntity<TaskStatusDTO> update(@PathVariable Long id, @RequestBody TaskStatusUpdateDto dto) {
        return repository.findById(id).map(entity -> {
            taskStatusMapper.updateEntity(dto, entity);
            TaskStatus saved = repository.save(entity);
            return ResponseEntity.ok(taskStatusMapper.toDto(saved));
        }).orElse(ResponseEntity.notFound().build());
    }
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        repository.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
