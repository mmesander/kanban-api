package nu.youritguy.kanban_api.adapter.in.rest.controllers;

import nu.youritguy.kanban_api.application.ports.in.CreateTaskUseCase;
import nu.youritguy.kanban_api.application.ports.in.DeleteTaskUseCase;
import nu.youritguy.kanban_api.application.ports.in.GetTaskUseCase;
import nu.youritguy.kanban_api.application.ports.in.PatchTaskUseCase;
import nu.youritguy.kanban_api.domain.exceptions.RecordNotFoundException;
import nu.youritguy.kanban_api.domain.models.Task;
import nu.youritguy.kanban_api.domain.models.TaskOverview;
import nu.youritguy.kanban_api.domain.models.TaskStatus;
import nu.youritguy.kanban_api.domain.models.TaskSummary;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.endsWith;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.nullValue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(TaskController.class)
class TaskControllerTest {

    private static final String BASE_URL = "/api/tasks";

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private GetTaskUseCase getTaskUseCase;
    @MockBean
    private CreateTaskUseCase createTaskUseCase;
    @MockBean
    private PatchTaskUseCase patchTaskUseCase;
    @MockBean
    private DeleteTaskUseCase deleteTaskUseCase;

    private final UUID id = UUID.fromString("11111111-2222-3333-4444-555555555555");
    private Task task;

    @BeforeEach
    void setUp() {
        Instant now = Instant.parse("2024-01-01T10:00:00Z");
        task = new Task(id, "Title", "Desc", TaskStatus.TO_DO, now, now);
    }

    @Nested
    class GetTasks {
        @Test
        void returnsAllTasksWithoutStatusFilter() throws Exception {
            when(getTaskUseCase.getAllTasks()).thenReturn(List.of(task));

            mockMvc.perform(get(BASE_URL))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$", hasSize(1)))
                    .andExpect(jsonPath("$[0].id").value(id.toString()))
                    .andExpect(jsonPath("$[0].title").value("Title"))
                    .andExpect(jsonPath("$[0].description").value("Desc"))
                    .andExpect(jsonPath("$[0].status").value("TO_DO"));

            verify(getTaskUseCase, never()).getTasksByStatus(any());
        }

        @Test
        void blankStatusParamReturnsAllTasks() throws Exception {
            when(getTaskUseCase.getAllTasks()).thenReturn(List.of(task));

            mockMvc.perform(get(BASE_URL).param("status", "  "))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$", hasSize(1)));

            verify(getTaskUseCase).getAllTasks();
        }

        @Test
        void filtersOutNullTasks() throws Exception {
            List<Task> tasks = new ArrayList<>();
            tasks.add(task);
            tasks.add(null);
            when(getTaskUseCase.getAllTasks()).thenReturn(tasks);

            mockMvc.perform(get(BASE_URL))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$", hasSize(1)));
        }

        @ParameterizedTest
        @CsvSource({"DONE, DONE", "In Progress, IN_PROGRESS", "' to_do ', TO_DO"})
        void filtersByStatus(String param, TaskStatus expected) throws Exception {
            when(getTaskUseCase.getTasksByStatus(new GetTaskUseCase.GetTasksByStatusCommand(expected)))
                    .thenReturn(List.of(task));

            mockMvc.perform(get(BASE_URL).param("status", param))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$", hasSize(1)));

            verify(getTaskUseCase, never()).getAllTasks();
        }

        @Test
        void unknownStatusReturns400() throws Exception {
            mockMvc.perform(get(BASE_URL).param("status", "FOO"))
                    .andExpect(status().isBadRequest())
                    .andExpect(content().string(containsString("Status [FOO] unknown")));

            verifyNoInteractions(getTaskUseCase);
        }
    }

    @Nested
    class GetTaskById {
        @Test
        void returnsTask() throws Exception {
            when(getTaskUseCase.getTaskById(new GetTaskUseCase.GetTaskByIdCommand(id))).thenReturn(task);

            mockMvc.perform(get(BASE_URL + "/" + id))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.id").value(id.toString()))
                    .andExpect(jsonPath("$.title").value("Title"));
        }

        @Test
        void returns404WhenNotFound() throws Exception {
            when(getTaskUseCase.getTaskById(any())).thenThrow(new RecordNotFoundException(id));

            mockMvc.perform(get(BASE_URL + "/" + id))
                    .andExpect(status().isNotFound())
                    .andExpect(content().string("Task [" + id + "] not found"));
        }

        @Test
        void returns400ForInvalidUuid() throws Exception {
            mockMvc.perform(get(BASE_URL + "/not-a-uuid"))
                    .andExpect(status().isBadRequest());

            verifyNoInteractions(getTaskUseCase);
        }
    }

    @Test
    void getTaskOverviewReturnsGroupedTasks() throws Exception {
        Task done = new Task(UUID.randomUUID(), "Done", null, TaskStatus.DONE, task.getCreatedAt(), task.getUpdatedAt());
        when(getTaskUseCase.getTaskOverview()).thenReturn(TaskOverview.createTaskOverview(List.of(task, done)));

        mockMvc.perform(get(BASE_URL + "/overview"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.toDo", hasSize(1)))
                .andExpect(jsonPath("$.toDo[0].id").value(id.toString()))
                .andExpect(jsonPath("$.inProgress", hasSize(0)))
                .andExpect(jsonPath("$.done", hasSize(1)))
                .andExpect(jsonPath("$.done[0].title").value("Done"));
    }

    @Test
    void getTaskSummaryReturnsCounts() throws Exception {
        when(getTaskUseCase.getTaskSummary()).thenReturn(TaskSummary.createTaskSummary(1, 2, 3, 6));

        mockMvc.perform(get(BASE_URL + "/summary"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.toDo").value(1))
                .andExpect(jsonPath("$.inProgress").value(2))
                .andExpect(jsonPath("$.done").value(3))
                .andExpect(jsonPath("$.total").value(6));
    }

    @Nested
    class CreateTask {
        @Test
        void createsTaskAndReturns201WithLocation() throws Exception {
            when(createTaskUseCase.createTask(
                    new CreateTaskUseCase.CreateTaskCommand("Title", "Desc", TaskStatus.IN_PROGRESS)))
                    .thenReturn(task);

            mockMvc.perform(post(BASE_URL)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content("""
                                    {"title": "Title", "description": "Desc", "status": "In Progress"}
                                    """))
                    .andExpect(status().isCreated())
                    .andExpect(header().string("Location", endsWith(BASE_URL + "/" + id)))
                    .andExpect(jsonPath("$.id").value(id.toString()))
                    .andExpect(jsonPath("$.title").value("Title"));
        }

        @Test
        void passesNullStatusWhenNotProvided() throws Exception {
            when(createTaskUseCase.createTask(
                    new CreateTaskUseCase.CreateTaskCommand("Title", null, null)))
                    .thenReturn(task);

            mockMvc.perform(post(BASE_URL)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content("""
                                    {"title": "Title"}
                                    """))
                    .andExpect(status().isCreated());

            verify(createTaskUseCase).createTask(new CreateTaskUseCase.CreateTaskCommand("Title", null, null));
        }

        @Test
        void returns400WhenTitleMissing() throws Exception {
            mockMvc.perform(post(BASE_URL)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content("""
                                    {"description": "Desc"}
                                    """))
                    .andExpect(status().isBadRequest())
                    .andExpect(content().string("title: Title should not be empty\n"));

            verifyNoInteractions(createTaskUseCase);
        }

        @Test
        void returns400WithAllFieldErrors() throws Exception {
            String body = String.format("{\"title\": \"%s\", \"description\": \"%s\"}",
                    "a".repeat(101), "b".repeat(501));

            mockMvc.perform(post(BASE_URL)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(body))
                    .andExpect(status().isBadRequest())
                    .andExpect(content().string(containsString("title: Title can't exceed 100 characters")))
                    .andExpect(content().string(containsString("description: Description can't exceed 500 characters")));

            verifyNoInteractions(createTaskUseCase);
        }

        @Test
        void returns400ForUnknownStatus() throws Exception {
            mockMvc.perform(post(BASE_URL)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content("""
                                    {"title": "Title", "status": "WRONG"}
                                    """))
                    .andExpect(status().isBadRequest())
                    .andExpect(content().string(containsString("Status [WRONG] unknown")));

            verifyNoInteractions(createTaskUseCase);
        }
    }

    @Nested
    class PatchTask {
        @Test
        void patchesTask() throws Exception {
            when(patchTaskUseCase.patchTask(eq(id),
                    eq(new PatchTaskUseCase.PatchTaskCommand("New", "", TaskStatus.DONE))))
                    .thenReturn(task);

            mockMvc.perform(patch(BASE_URL + "/" + id)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content("""
                                    {"title": "New", "description": "", "status": "done"}
                                    """))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.id").value(id.toString()));
        }

        @Test
        void emptyBodyPassesNullsToUseCase() throws Exception {
            when(patchTaskUseCase.patchTask(eq(id),
                    eq(new PatchTaskUseCase.PatchTaskCommand(null, null, null))))
                    .thenReturn(task);

            mockMvc.perform(patch(BASE_URL + "/" + id)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content("{}"))
                    .andExpect(status().isOk());
        }

        @Test
        void returns400ForEmptyTitle() throws Exception {
            mockMvc.perform(patch(BASE_URL + "/" + id)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content("""
                                    {"title": ""}
                                    """))
                    .andExpect(status().isBadRequest())
                    .andExpect(content().string("title: Title must be between 1 and 100 characters\n"));

            verifyNoInteractions(patchTaskUseCase);
        }

        @Test
        void returns404WhenNotFound() throws Exception {
            when(patchTaskUseCase.patchTask(eq(id), any())).thenThrow(new RecordNotFoundException(id));

            mockMvc.perform(patch(BASE_URL + "/" + id)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content("""
                                    {"title": "New"}
                                    """))
                    .andExpect(status().isNotFound())
                    .andExpect(content().string("Task [" + id + "] not found"));
        }
    }

    @Nested
    class MoveTask {
        @Test
        void movesTask() throws Exception {
            Task moved = new Task(id, "Title", "Desc", TaskStatus.IN_PROGRESS, task.getCreatedAt(), task.getUpdatedAt());
            when(patchTaskUseCase.moveTask(id, new PatchTaskUseCase.MoveTaskCommand(TaskStatus.IN_PROGRESS)))
                    .thenReturn(moved);

            mockMvc.perform(patch(BASE_URL + "/" + id + "/status")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content("""
                                    {"status": "IN_PROGRESS"}
                                    """))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.status").value("IN_PROGRESS"));
        }

        @Test
        void returns400WhenStatusBlank() throws Exception {
            mockMvc.perform(patch(BASE_URL + "/" + id + "/status")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content("""
                                    {"status": ""}
                                    """))
                    .andExpect(status().isBadRequest())
                    .andExpect(content().string("status: Status should not be empty\n"));

            verifyNoInteractions(patchTaskUseCase);
        }

        @Test
        void returns400WhenStatusUnknown() throws Exception {
            mockMvc.perform(patch(BASE_URL + "/" + id + "/status")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content("""
                                    {"status": "BLOCKED"}
                                    """))
                    .andExpect(status().isBadRequest())
                    .andExpect(content().string(containsString("Status [BLOCKED] unknown")));

            verifyNoInteractions(patchTaskUseCase);
        }

        @Test
        void returns404WhenNotFound() throws Exception {
            when(patchTaskUseCase.moveTask(eq(id), any())).thenThrow(new RecordNotFoundException(id));

            mockMvc.perform(patch(BASE_URL + "/" + id + "/status")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content("""
                                    {"status": "DONE"}
                                    """))
                    .andExpect(status().isNotFound());
        }
    }

    @Nested
    class DeleteTask {
        @Test
        void returns204() throws Exception {
            mockMvc.perform(delete(BASE_URL + "/" + id))
                    .andExpect(status().isNoContent())
                    .andExpect(content().string(""));

            verify(deleteTaskUseCase).deleteTask(id);
        }

        @Test
        void returns404WhenNotFound() throws Exception {
            doThrow(new RecordNotFoundException(id)).when(deleteTaskUseCase).deleteTask(id);

            mockMvc.perform(delete(BASE_URL + "/" + id))
                    .andExpect(status().isNotFound())
                    .andExpect(content().string("Task [" + id + "] not found"));
        }
    }
}
