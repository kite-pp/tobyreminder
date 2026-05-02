package toby.ai.tobyreminder.dto.request;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class ReminderListRequest {

    private String name;
    private String color;
    private String icon;
    private boolean isDefault;
}
