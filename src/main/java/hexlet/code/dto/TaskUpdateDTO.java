package hexlet.code.dto;


import com.fasterxml.jackson.annotation.JsonAlias;
import lombok.Getter;
import lombok.Setter;

import java.util.Set;

@Getter
@Setter
public class TaskUpdateDTO {
    private String title;
    private String content;
    @JsonAlias("taskLabelIds")
    private Set<Long> labelIds;
}
