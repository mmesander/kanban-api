package nu.youritguy.kanban_api.adapter.out.persistence;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;
import nu.youritguy.kanban_api.domain.models.TaskStatus;

import java.time.Instant;
import java.util.UUID;

@Getter
@Setter
@Entity
@Table(name = "tasks")
public class TaskJpaEntity {
    @Id
    private UUID id;

    @Column(length = 100, nullable = false)
    private String title;

    @Column(length = 500)
    private String description;

    @Column(nullable = false)
    private TaskStatus status;

    @Column(nullable = false)
    private Instant createdAt;

    @Column(nullable = false)
    private Instant updatedAt;

    public TaskJpaEntity() {}

    public TaskJpaEntity(UUID id, String title, String description, TaskStatus status, Instant createdAt, Instant updatedAt) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.status = status;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }
}
