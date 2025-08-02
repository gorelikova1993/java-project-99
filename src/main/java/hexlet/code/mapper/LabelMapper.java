package hexlet.code.mapper;
import hexlet.code.dto.LabelCreateDTO;
import hexlet.code.dto.LabelDTO;
import hexlet.code.dto.LabelUpdateDTO;
import hexlet.code.model.Label;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(componentModel = "spring")
public interface LabelMapper {
    LabelDTO toDto(Label label);
    Label fromCreateDto(LabelCreateDTO dto);
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void update(@MappingTarget Label label, LabelUpdateDTO dto);
}
