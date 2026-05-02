package toby.ai.tobyreminder.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import toby.ai.tobyreminder.domain.enums.Priority;
import toby.ai.tobyreminder.dto.request.OrderItem;
import toby.ai.tobyreminder.dto.request.ReminderRequest;
import toby.ai.tobyreminder.dto.response.CountResponse;
import toby.ai.tobyreminder.dto.response.ReminderResponse;
import toby.ai.tobyreminder.service.ports.in.ReminderService;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/reminders")
@RequiredArgsConstructor
public class ReminderController {

    private final ReminderService reminderService;

    @GetMapping
    public List<ReminderResponse> findReminders(
            @RequestParam(required = false) Long listId,
            @RequestParam(required = false) String smart,
            @RequestParam(required = false) String q) {
        if (q != null) return reminderService.search(q);
        if (smart != null) return reminderService.findBySmart(smart);
        if (listId != null) return reminderService.findByListId(listId);
        throw new IllegalArgumentException("listId, smart, or q is required");
    }

    @GetMapping("/count")
    public CountResponse getCount() {
        return reminderService.getCount();
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ReminderResponse create(@RequestBody ReminderRequest request) {
        return reminderService.create(request);
    }

    @PutMapping("/{id}")
    public ReminderResponse update(@PathVariable Long id, @RequestBody ReminderRequest request) {
        return reminderService.update(id, request);
    }

    @PatchMapping("/{id}/complete")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void toggleComplete(@PathVariable Long id) {
        reminderService.toggleComplete(id);
    }

    @PatchMapping("/{id}/flag")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void toggleFlag(@PathVariable Long id) {
        reminderService.toggleFlag(id);
    }

    @PatchMapping("/{id}/priority")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void updatePriority(@PathVariable Long id, @RequestBody Map<String, String> body) {
        reminderService.updatePriority(id, Priority.valueOf(body.get("priority")));
    }

    @PatchMapping("/order")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void reorder(@RequestBody List<OrderItem> items) {
        reminderService.reorder(items);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        reminderService.delete(id);
    }

    @DeleteMapping("/completed")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteCompleted(@RequestParam(required = false) Long listId) {
        reminderService.deleteCompleted(listId);
    }
}
