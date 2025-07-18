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

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.springframework.beans.factory.annotation.Autowired;


import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring")
public abstract class TaskMapper {
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private LabelRepository labelRepository;
    @Autowired
    private TaskStatusRepository taskStatusRepository;
    @Mapping(target = "name", source = "title")
    @Mapping(target = "description", source = "content")
    @Mapping(target = "assignee", expression = "java(getAssignee(taskCreateDTO.getAssigneeId()))")
    @Mapping(target = "taskStatus", expression = "java(getTaskStatus(taskCreateDTO.getStatus()))")
    @Mapping(target = "labels", expression = "java(getLabels(taskCreateDTO.getLabelIds()))")
    @Mapping(target = "createdAt", expression = "java(java.time.LocalDate.now())")
    public abstract Task toEntity(TaskCreateDTO taskCreateDTO);
    @Mapping(target = "name", source = "title")
    @Mapping(target = "description", source = "content")
    public abstract void updateEntity(TaskUpdateDTO dto, @MappingTarget Task task);
    @Mapping(target = "title", source = "name")
    @Mapping(target = "content", source = "description")
    @Mapping(target = "status", source = "taskStatus.name")
    @Mapping(target = "assigneeId", source = "assignee.id")
    @Mapping(target = "labelIds", expression = "java(getLabelIds(task))")
    public abstract TaskCreateDTO toDto(Task task);
    // ======== Вспомогательные методы ниже ==========
    protected User getAssignee(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found: " + id));
    }
    protected TaskStatus getTaskStatus(String statusName) {
        return taskStatusRepository.findByName(statusName)
                .orElseThrow(() -> new RuntimeException("Task status not found: " + statusName));
    }
    protected Set<Label> getLabels(Set<Long> labelIds) {
        if (labelIds == null) {
            return new HashSet<>();
        }
        return new HashSet<>(labelRepository.findAllById(labelIds));
    }
    protected Set<Long> getLabelIds(Task task) {
        if (task.getLabels() == null) {
            return new HashSet<>();
        }
        return task.getLabels().stream()
                .map(Label::getId)
                .collect(Collectors.toSet());
    }
}
