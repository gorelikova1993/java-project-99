package hexlet.code.mapper;

import hexlet.code.dto.TaskStatusCreateDTO;
import hexlet.code.dto.TaskStatusDTO;
import hexlet.code.dto.TaskStatusUpdateDto;
import hexlet.code.model.TaskStatus;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(componentModel = "spring")
public interface TaskStatusMapper {
    // entity → dto
    TaskStatusDTO toDto(TaskStatus taskStatus);
    
    // createDto → entity
    TaskStatus toEntity(TaskStatusCreateDTO dto);
    
    // updateDto → обновление существующей сущности
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateEntity(TaskStatusUpdateDto dto, @MappingTarget TaskStatus entity);
}
