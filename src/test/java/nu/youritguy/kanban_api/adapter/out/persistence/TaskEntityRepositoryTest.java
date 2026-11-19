package nu.youritguy.kanban_api.adapter.out.persistence;

import nu.youritguy.kanban_api.domain.models.TaskStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Tests the derived queries of the Spring Data repository against the H2 database
 * (with the real schema.sql).
 */
@DataJpaTest
class TaskEntityRepositoryTest {

    @Autowired
    private TaskEntityRepository repository;

    private final Instant now = Instant.now().truncatedTo(ChronoUnit.MICROS);

    @BeforeEach
    void setUp() {
        repository.deleteAll();
        repository.save(entity(TaskStatus.TO_DO));
        repository.save(entity(TaskStatus.TO_DO));
        repository.save(entity(TaskStatus.IN_PROGRESS));
    }

    private TaskEntity entity(TaskStatus status) {
        return new TaskEntity(UUID.randomUUID(), "Title " + status, "Desc", status, now, now);
    }

    @Test
    void findByIdReturnsSavedEntity() {
        TaskEntity saved = repository.save(entity(TaskStatus.DONE));

        Optional<TaskEntity> found = repository.findById(saved.getId());

        assertThat(found).isPresent();
        assertThat(found.get().getTitle()).isEqualTo("Title DONE");
        assertThat(found.get().getStatus()).isEqualTo(TaskStatus.DONE);
        assertThat(found.get().getCreatedAt()).isEqualTo(now);
    }

    @Test
    void findByIdReturnsEmptyForUnknownId() {
        assertThat(repository.findById(UUID.randomUUID())).isEmpty();
    }

    @Test
    void findAllByStatusReturnsOnlyMatchingEntities() {
        assertThat(repository.findAllByStatus(TaskStatus.TO_DO))
                .hasSize(2)
                .allMatch(e -> e.getStatus() == TaskStatus.TO_DO);
        assertThat(repository.findAllByStatus(TaskStatus.DONE)).isEmpty();
    }

    @Test
    void countByStatusCountsPerStatus() {
        assertThat(repository.countByStatus(TaskStatus.TO_DO)).isEqualTo(2);
        assertThat(repository.countByStatus(TaskStatus.IN_PROGRESS)).isEqualTo(1);
        assertThat(repository.countByStatus(TaskStatus.DONE)).isZero();
    }

    @Test
    void existsByIdAndDeleteById() {
        TaskEntity saved = repository.save(entity(TaskStatus.DONE));
        assertThat(repository.existsById(saved.getId())).isTrue();

        repository.deleteById(saved.getId());

        assertThat(repository.existsById(saved.getId())).isFalse();
    }
}
