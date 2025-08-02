package hexlet.code.initializer;

import hexlet.code.model.TaskStatus;
import hexlet.code.repository.TaskStatusRepository;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;


import java.time.LocalDateTime;

@Component
@RequiredArgsConstructor
public class TaskStatusInitializer {
    private final TaskStatusRepository repository;
    @PostConstruct
    public void init() {
        createIfNotExists("Draft", "draft");
        createIfNotExists("To Review", "to_review");
        createIfNotExists("To Be Fixed", "to_be_fixed");
        createIfNotExists("To Publish", "to_publish");
        createIfNotExists("Published", "published");
    }
    private void createIfNotExists(String name, String slug) {
        if (repository.findBySlug(slug).isEmpty()) {
            TaskStatus status = new TaskStatus();
            status.setName(name);
            status.setSlug(slug);
            status.setCreatedAt(LocalDateTime.now());
            repository.save(status);
        }
    }
}
