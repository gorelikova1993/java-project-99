package hexlet.code.dto;


import lombok.Getter;
import lombok.Setter;

import java.util.Set;

@Getter
@Setter
public class TaskUpdateDTO {
    private String title;
    private String content;
    private Set<Long> labelIds;
}
