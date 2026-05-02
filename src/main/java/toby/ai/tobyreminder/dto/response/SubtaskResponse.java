package toby.ai.tobyreminder.dto.response;

import lombok.Builder;
import lombok.Getter;
import toby.ai.tobyreminder.domain.Subtask;

import java.time.LocalDateTime;

@Getter
@Builder
public class SubtaskResponse {

    private Long id;
    private String title;
    private boolean completed;
    private int sortOrder;
    private LocalDateTime createdAt;

    public static SubtaskResponse from(Subtask subtask) {
        return SubtaskResponse.builder()
                .id(subtask.getId())
                .title(subtask.getTitle())
                .completed(subtask.isCompleted())
                .sortOrder(subtask.getSortOrder())
                .createdAt(subtask.getCreatedAt())
                .build();
    }
}
