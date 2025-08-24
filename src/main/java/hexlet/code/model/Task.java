package hexlet.code.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Entity
@Getter
@Setter
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@EntityListeners(AuditingEntityListener.class)
public class Task {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Include
    private Long id;
    @NotBlank
    @Size(min = 1)
    @JsonIgnore
    private String name;
    private int index;
    @JsonIgnore
    private String description;
    @JsonIgnore
    @ManyToOne
    @JoinColumn(name = "task_status_id")
    private TaskStatus taskStatus;
    @ManyToOne
    @JoinColumn(name = "assignee_id")
    private User assignee;
    @CreatedDate
    private LocalDate createdAt;
    @JsonIgnore
    @ManyToMany
    @JoinTable(
            name = "task_labels",
            joinColumns = @JoinColumn(name = "task_id"),
            inverseJoinColumns = @JoinColumn(name = "label_id")
    )
    private Set<Label> labels = new HashSet<>();
    
    @JsonProperty("title")
    public String getJsonTitle() {
        return this.name;
    }
    
    @JsonProperty("content")
    public String getJsonContent() {
        return this.description;
    }
    
    @JsonProperty("status")
    public String getJsonStatus() {
        return this.taskStatus != null ? this.taskStatus.getSlug() : null;
    }
    
    @JsonProperty("taskLabelIds")
    public java.util.Set<Long> getJsonTaskLabelIds() {
        if (this.labels == null) {
            return java.util.Set.of();
        }
        return this.labels.stream().map(hexlet.code.model.Label::getId)
                .collect(java.util.stream.Collectors.toSet());
    }
    @JsonProperty("assignee_id")
    public Long getAssigneeId() {
        return this.assignee != null ? this.assignee.getId() : null;
    }
}
