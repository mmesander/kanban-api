package nu.youritguy.kanban_api.adapter.out.persistence;

import nu.youritguy.kanban_api.domain.models.TaskStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface TaskEntityRepository extends JpaRepository<TaskEntity, UUID> {
    Optional<TaskEntity> findById(UUID id);
    List<TaskEntity> findAllByStatus(TaskStatus status);
    int countByStatus(TaskStatus status);
    boolean existsById(UUID id);
    void deleteById(UUID id);
}
