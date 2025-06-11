package hexlet.code.app.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

import java.util.Set;

@Setter
@Getter
public class TaskCreateDTO {
    private int index;
    private Long assignee_id;
    @NotBlank
    private String title;
    private String content;
    private String status;
    private Set<Long> labelIds;
}
