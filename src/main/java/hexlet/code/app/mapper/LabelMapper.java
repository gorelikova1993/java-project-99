package hexlet.code.app.mapper;
import hexlet.code.app.dto.LabelCreateDTO;
import hexlet.code.app.dto.LabelDTO;
import hexlet.code.app.dto.LabelUpdateDTO;
import hexlet.code.app.model.Label;
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
