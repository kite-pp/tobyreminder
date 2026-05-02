package toby.ai.tobyreminder.dto.response;

import lombok.Builder;
import lombok.Getter;
import toby.ai.tobyreminder.domain.Reminder;
import toby.ai.tobyreminder.domain.enums.Priority;

import java.time.LocalDateTime;

@Getter
@Builder
public class ReminderResponse {

    private Long id;
    private Long listId;
    private String title;
    private String notes;
    private LocalDateTime dueDate;
    private Priority priority;
    private boolean flagged;
    private boolean completed;
    private LocalDateTime completedAt;
    private LocalDateTime createdAt;

    public static ReminderResponse from(Reminder reminder) {
        return ReminderResponse.builder()
                .id(reminder.getId())
                .listId(reminder.getList() != null ? reminder.getList().getId() : null)
                .title(reminder.getTitle())
                .notes(reminder.getNotes())
                .dueDate(reminder.getDueDate())
                .priority(reminder.getPriority())
                .flagged(reminder.isFlagged())
                .completed(reminder.isCompleted())
                .completedAt(reminder.getCompletedAt())
                .createdAt(reminder.getCreatedAt())
                .build();
    }
}
