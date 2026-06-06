package com.worksphere.backend.task;

import com.worksphere.backend.task.dto.TaskRequest;
import com.worksphere.backend.task.dto.TaskResponse;
import com.worksphere.backend.task.dto.TaskStatusUpdateRequest;
import com.worksphere.backend.task.dto.TaskUpdateRequest;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/tasks")
@Tag(name = "Task", description = "Task management")
@RequiredArgsConstructor
public class TaskController {

    private final TaskService taskService;

    @PostMapping
    public ResponseEntity<TaskResponse> createTask(@Valid @RequestBody TaskRequest request) {
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(taskService.createTask(request));
    }

    @GetMapping("/project/{projectId}")
    public ResponseEntity<List<TaskResponse>> getByProject(@PathVariable Long projectId, @RequestParam(required = false) TaskStatus status) {

        if (status != null) {
            return ResponseEntity.ok(
                    taskService.getTasksByProjectAndStatus(projectId, status)
            );
        }

        return ResponseEntity.ok(
                taskService.getTaskByProject(projectId)
        );
    }

    @GetMapping("/{taskId}")
    public ResponseEntity<TaskResponse> getTask(@PathVariable Long taskId) {
        return ResponseEntity.ok(
                taskService.getTask(taskId)
        );
    }

    @PutMapping("/{taskId}")
    public ResponseEntity<TaskResponse> updateTask(@PathVariable Long taskId, @Valid @RequestBody TaskUpdateRequest request) {
        return ResponseEntity.ok(
                taskService.updateTask(taskId, request)
        );
    }

    @PatchMapping("/{taskId}/status")
    public ResponseEntity<TaskResponse> updateStatus(@PathVariable Long taskId, @Valid @RequestBody TaskStatusUpdateRequest request) {
        return ResponseEntity.ok(
                taskService.updateTaskStatus(taskId, request)
        );
    }

    @DeleteMapping("/{taskId}")
    public ResponseEntity<Void> deleteTask(@PathVariable Long taskId) {
        taskService.deleteTask(taskId);
        return ResponseEntity.noContent().build();
    }

}
