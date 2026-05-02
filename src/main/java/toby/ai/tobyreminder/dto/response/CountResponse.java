package toby.ai.tobyreminder.dto.response;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class CountResponse {

    private long today;
    private long scheduled;
    private long all;
    private long flagged;
    private long completed;
}
