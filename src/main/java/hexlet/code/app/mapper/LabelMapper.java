package hexlet.code.app.mapper;
import hexlet.code.app.dto.LabelCreateDTO;
import hexlet.code.app.dto.LabelDTO;
import hexlet.code.app.dto.LabelUpdateDTO;
import hexlet.code.app.model.Label;
import org.springframework.stereotype.Component;

@Component
public class LabelMapper {
    public LabelDTO toDto(Label label) {
        LabelDTO dto = new LabelDTO();
        dto.setId(label.getId());
        dto.setName(label.getName());
        dto.setCreatedAt(label.getCreatedAt());
        return dto;
    }
    
    public Label fromCreateDto(LabelCreateDTO dto) {
        Label label = new Label();
        label.setName(dto.getName());
        return label;
    }
    
    public Label fromDto(LabelDTO dto) {
        Label label = new Label();
        label.setId(dto.getId());
        label.setName(dto.getName());
        label.setCreatedAt(dto.getCreatedAt());
        return label;
    }
    
    public void update(Label label, LabelUpdateDTO dto) {
        label.setName(dto.getName());
    }
}
