package hexlet.code.app.dto;

import hexlet.code.app.model.User;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class UserDto {
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
        this.firstName = user.getFirstName();
        this.lastName = user.getLastName();
        this.email = user.getEmail();
        this.createdAt = user.getCreatedAt();
    }
}
