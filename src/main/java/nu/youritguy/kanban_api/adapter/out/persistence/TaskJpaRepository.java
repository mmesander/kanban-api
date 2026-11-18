package nu.youritguy.kanban_api.adapter.out.persistence;

import nu.youritguy.kanban_api.domain.models.TaskStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface TaskJpaRepository extends JpaRepository<TaskJpaEntity, UUID> {
    List<TaskJpaEntity> findAllByStatus(TaskStatus status);
    Optional<TaskJpaEntity> findById(UUID id);
}
