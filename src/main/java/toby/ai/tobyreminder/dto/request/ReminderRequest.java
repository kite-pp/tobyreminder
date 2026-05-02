package toby.ai.tobyreminder.dto.request;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import toby.ai.tobyreminder.domain.enums.Priority;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
public class ReminderRequest {

    private String title;
    private String notes;
    private Long listId;
    private LocalDateTime startDate;
    private LocalDateTime dueDate;
    private Priority priority;
    private Boolean flagged;

    public ReminderRequest(String title, String notes, Long listId) {
        this.title = title;
        this.notes = notes;
        this.listId = listId;
    }

    public ReminderRequest(String title, String notes, Long listId, LocalDateTime dueDate, Priority priority, Boolean flagged) {
        this.title = title;
        this.notes = notes;
        this.listId = listId;
        this.dueDate = dueDate;
        this.priority = priority;
        this.flagged = flagged;
    }
}
