package hexlet.code.app.specification;

import hexlet.code.app.model.Task;
import jakarta.persistence.criteria.Join;
import org.springframework.data.jpa.domain.Specification;

import java.util.Objects;

public class TaskSpecification {
    public static Specification<Task> hasTitleContaining(String titlePart) {
        return (root, query, cb) -> {
            if (Objects.isNull(titlePart)) {
                return null;
            }
            return cb.like(cb.lower(root.get("name")), "%" + titlePart.toLowerCase() + "%");
        };
    }
    public static Specification<Task> hasAssigneeId(Long assigneeId) {
        return (root, query, cb) -> {
            if (Objects.isNull(assigneeId)) {
                return null;
            }
            return cb.equal(root.get("assignee").get("id"), assigneeId);
        };
    }
    public static Specification<Task> hasStatusSlug(String statusSlug) {
        return (root, query, cb) -> {
            if (Objects.isNull(statusSlug)) {
                return null;
            }
            return cb.equal(root.get("taskStatus").get("slug"), statusSlug);
        };
    }
    public static Specification<Task> hasLabelId(Long labelId) {
        return (root, query, cb) -> {
            if (Objects.isNull(labelId)) {
                return null;
            }
            Join<Object, Object> labels = root.join("labels");
            return cb.equal(labels.get("id"), labelId);
        };
    }
}
