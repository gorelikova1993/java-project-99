package hexlet.code.dto;


import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TaskStatusUpdateDto {
    private String name;
    private String slug;
}
