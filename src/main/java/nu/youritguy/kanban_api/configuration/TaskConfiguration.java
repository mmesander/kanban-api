package nu.youritguy.kanban_api.configuration;

import nu.youritguy.kanban_api.application.ports.out.TaskRepository;
import nu.youritguy.kanban_api.application.services.TaskService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

// De config is nodig omdat de controller vraagt om een CreateTaskUseCase
// Aangezien dat een interface is, zoekt Spring een bean die dat type heeft
// En omdat de TaskService deze interface implementeert gaat spring op zoek naar de TaskService
// Omdat de service in de applicatielaag zit, wil ik hier geen @service voor gebruiken
// Daarom maak ik een bean op dit level zodat Spring de service kan herkennen en gebruiken en dus ook alle use cases
@Configuration
public class TaskConfiguration {
    @Bean
    public TaskService taskService(TaskRepository taskRepository) {
        return new TaskService(taskRepository);
    }
}
