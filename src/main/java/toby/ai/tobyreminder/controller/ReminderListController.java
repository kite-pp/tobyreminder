package toby.ai.tobyreminder.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import toby.ai.tobyreminder.dto.request.ReminderListRequest;
import toby.ai.tobyreminder.dto.response.ReminderListResponse;
import toby.ai.tobyreminder.service.ports.in.ReminderListService;

import java.util.List;

@RestController
@RequestMapping("/api/lists")
@RequiredArgsConstructor
public class ReminderListController {

    private final ReminderListService reminderListService;

    @GetMapping
    public List<ReminderListResponse> findAll() {
        return reminderListService.findAll();
    }

    @GetMapping("/{id}")
    public ReminderListResponse findById(@PathVariable Long id) {
        return reminderListService.findById(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ReminderListResponse create(@RequestBody ReminderListRequest request) {
        return reminderListService.create(request);
    }

    @PutMapping("/{id}")
    public ReminderListResponse update(@PathVariable Long id, @RequestBody ReminderListRequest request) {
        return reminderListService.update(id, request);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        reminderListService.delete(id);
    }
}
