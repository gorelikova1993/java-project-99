package hexlet.code.app.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

import java.util.Set;

@Setter
@Getter
public class TaskCreateDTO {
    private int index;
    @JsonProperty("assignee_id")
    private Long assigneeId;
    @NotBlank
    @JsonProperty("title")
    private String title;
    @JsonProperty("content")
    private String content;
    @JsonProperty("status")
    private String status;
    private Set<Long> labelIds;
}
