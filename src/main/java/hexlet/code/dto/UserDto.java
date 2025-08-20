package hexlet.code.dto;

import hexlet.code.model.User;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class UserDto {
    private Long id;
    private String firstName;
    private String lastName;
    private String email;
    private LocalDateTime createdAt;
    public UserDto() {
    }
    public UserDto(User user) {
        if (user == null) {
            throw new IllegalArgumentException("User cannot be null");
        }
        this.id = user.getId();
        this.firstName = user.getFirstName();
        this.lastName = user.getLastName();
        this.email = user.getEmail();
        this.createdAt = user.getCreatedAt();
    }
}
