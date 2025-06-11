package hexlet.code.app.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class LabelUpdateDTO {
    @NotBlank(message = "Name is required")
    @Size(min = 3, max = 1000, message = "Name must be between 3 and 1000 characters")
    private String name;
}
