package davidebraghi.CapstoneProject_TimelineManager.controllers;

import davidebraghi.CapstoneProject_TimelineManager.services.TaskService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/projects/{projectId}")
public class DND_Controller {
    @Autowired
    public TaskService taskService;

    //  ad ogni Drag&Drop aggiorna l'array per tracciare
    //  la posizione nuova di categories e tasks
    //  POST - http://localhost:3001/api/projects/{projectId}/categories/{categoryId}/tasks/dnd

    @PostMapping("/categories/{categoryId}/tasks/dnd")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public ResponseEntity<?> updateTaskOrder(
            @PathVariable Long projectId,
            @PathVariable Long categoryId,
            @RequestBody Map<String, List<Long>> payload
    ) {
        List<Long> orderdTasksIds = payload.get("orderedTaskIds");
        if (orderdTasksIds == null || orderdTasksIds.isEmpty()) {
            return ResponseEntity.badRequest().body("Missing orderedTaskIds");
        }
        taskService.updateTaskOrder(projectId, categoryId, orderdTasksIds);
        return ResponseEntity.ok().build();
    }

//    PATCH - http://localhost:3001/api/projects/{projectId}/tasks/{taskId}/category/{newCategoryId}

    @PatchMapping("/tasks/{taskId}/category/{newCategoryId}")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public ResponseEntity<?> updateTaskCategory(
            @PathVariable Long projectId,
            @PathVariable Long taskId,
            @PathVariable Long newCategoryId
    ) {
        taskService.updateTaskCategory(projectId, taskId, newCategoryId);
        return ResponseEntity.ok().build();
    }
}