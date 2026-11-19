package nu.youritguy.kanban_api.configuration;

import nu.youritguy.kanban_api.application.ports.out.TaskRepository;
import nu.youritguy.kanban_api.application.services.TaskService;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

class TaskConfigurationTest {

    @Test
    void taskServiceBeanUsesGivenRepository() {
        TaskRepository repository = mock(TaskRepository.class);

        TaskService service = new TaskConfiguration().taskService(repository);

        assertThat(service).isNotNull();
        service.getAllTasks();
        verify(repository).getAllTasks();
    }
}
