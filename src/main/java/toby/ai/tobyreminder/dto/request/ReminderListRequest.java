package toby.ai.tobyreminder.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ReminderListRequest {

    private String name;
    private String color;
    private String icon;
    @JsonProperty("isDefault")
    private Boolean isDefault;
}
