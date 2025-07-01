package hexlet.code.app.mapper;

import hexlet.code.app.dto.TaskCreateDTO;
import hexlet.code.app.dto.TaskUpdateDTO;
import hexlet.code.app.model.Label;
import hexlet.code.app.model.Task;
import hexlet.code.app.model.TaskStatus;
import hexlet.code.app.model.User;
import hexlet.code.app.repository.LabelRepository;
import hexlet.code.app.repository.TaskStatusRepository;
import hexlet.code.app.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.Set;
import java.util.stream.Collectors;

@Component
public class TaskMapper {
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private LabelRepository labelRepository;
    @Autowired
    private TaskStatusRepository taskStatusRepository;
    @Autowired
    private LabelMapper labelMapper;
    public Task toEntity(TaskCreateDTO taskCreateDTO) {
        Task task = new Task();
        task.setIndex(taskCreateDTO.getIndex());
        task.setName(taskCreateDTO.getTitle());
        task.setDescription(taskCreateDTO.getContent());
        // Получаем пользователя по assignee_id
        User assignee = userRepository.findById((long) taskCreateDTO.getAssigneeId())
                .orElseThrow(() -> new RuntimeException("User not found"));
        // Получаем статус задачи по названию status
        TaskStatus status = taskStatusRepository.findByName(taskCreateDTO.getStatus())
                .orElseThrow(() -> new RuntimeException("Task status not found"));
        task.setAssignee(assignee);
        task.setTaskStatus(status);
        task.setCreatedAt(LocalDate.now()); // Устанавливаем текущую дату как дату создания
        if (taskCreateDTO.getLabelIds() != null) {
            Set<Label> labels = labelRepository.findAllById(taskCreateDTO.getLabelIds())
                    .stream().collect(Collectors.toSet());
            task.setLabels(labels);
        }
        return task;
    }
    public Task toEntity(TaskUpdateDTO taskUpdateDTO, Task task) {
        task.setDescription(taskUpdateDTO.getContent());
        task.setName(taskUpdateDTO.getTitle());
        return task;
    }
    // Метод для преобразования Task в TaskCreateDTO
    public TaskCreateDTO toDto(Task task) {
        TaskCreateDTO taskCreateDTO = new TaskCreateDTO();
        taskCreateDTO.setIndex(task.getIndex());
        taskCreateDTO.setTitle(task.getName());
        taskCreateDTO.setContent(task.getDescription());
        taskCreateDTO.setAssigneeId(task.getAssignee().getId()); // Преобразуем ID пользователя в int
        taskCreateDTO.setStatus(task.getTaskStatus().getName()); // Получаем название статуса
        taskCreateDTO.setLabelIds(task.getLabels().stream()
                .map(Label::getId)
                .collect(Collectors.toSet()));
        return taskCreateDTO;
    }
}
