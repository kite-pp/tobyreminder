package toby.ai.tobyreminder.dto.response;

import lombok.Builder;
import lombok.Getter;
import toby.ai.tobyreminder.domain.ReminderList;

import java.time.LocalDateTime;

@Getter
@Builder
public class ReminderListResponse {

    private Long id;
    private String name;
    private String color;
    private String icon;
    private boolean isDefault;
    private int sortOrder;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public static ReminderListResponse from(ReminderList reminderList) {
        return ReminderListResponse.builder()
                .id(reminderList.getId())
                .name(reminderList.getName())
                .color(reminderList.getColor())
                .icon(reminderList.getIcon())
                .isDefault(reminderList.isDefault())
                .sortOrder(reminderList.getSortOrder())
                .createdAt(reminderList.getCreatedAt())
                .updatedAt(reminderList.getUpdatedAt())
                .build();
    }
}
