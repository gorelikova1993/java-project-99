package hexlet.code.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.validation.constraints.Size;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Getter
@Entity(name = "users")
@Setter
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@EntityListeners(AuditingEntityListener.class)
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String firstName;
    private String lastName;
    @NotBlank
    @Email(message = "Email should be valid")
    @EqualsAndHashCode.Include
    private String email;
    @NotBlank(message = "Password is required")
    @Size(min = 3, message = "Password must be at least 3 characters long")
    private String password;
    @Column(name = "created_at", updatable = false)
    @CreatedDate
    private LocalDateTime createdAt;
    @Column(name = "updated_at")
    @LastModifiedDate
    private LocalDateTime updatedAt;
}
