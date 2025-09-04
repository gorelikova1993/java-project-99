package hexlet.code.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TaskFilterDTO {
    private String titleCont;  // подстрока в названии (name)
    private Long assigneeId;   // id исполнителя
    private String status;     // slug статуса
    private Long labelId;      // id метки
}
