package toby.ai.tobyreminder.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import toby.ai.tobyreminder.dto.request.SubtaskRequest;
import toby.ai.tobyreminder.dto.response.SubtaskResponse;
import toby.ai.tobyreminder.service.ports.in.SubtaskService;

@RestController
@RequestMapping("/api/reminders/{reminderId}/subtasks")
@RequiredArgsConstructor
public class SubtaskController {

    private final SubtaskService subtaskService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public SubtaskResponse create(
            @PathVariable Long reminderId,
            @RequestBody SubtaskRequest request) {
        return subtaskService.create(reminderId, request);
    }

    @PatchMapping("/{subtaskId}/complete")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void toggleComplete(
            @PathVariable Long reminderId,
            @PathVariable Long subtaskId) {
        subtaskService.toggleComplete(reminderId, subtaskId);
    }

    @DeleteMapping("/{subtaskId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(
            @PathVariable Long reminderId,
            @PathVariable Long subtaskId) {
        subtaskService.delete(reminderId, subtaskId);
    }
}
