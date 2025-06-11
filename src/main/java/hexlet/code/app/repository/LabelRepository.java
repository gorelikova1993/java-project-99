package hexlet.code.app.repository;

import hexlet.code.app.model.Label;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface LabelRepository extends JpaRepository<Label, Long> {
    Optional<Label> findByName(String name);
    
    boolean existsByName(String name);
    
    @Query("SELECT l FROM Label l JOIN l.tasks t WHERE t.id = :taskId")
    List<Label> findAllByTaskId(Long taskId);
}
