package toby.ai.tobyreminder.dto.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ReminderListRequest {

    private String name;
    private String color;
    private String icon;
    private boolean isDefault;
}
