package toby.ai.tobyreminder.service.ports.in;

import toby.ai.tobyreminder.dto.request.SubtaskRequest;
import toby.ai.tobyreminder.dto.response.SubtaskResponse;

public interface SubtaskService {

    SubtaskResponse create(Long reminderId, SubtaskRequest request);

    void toggleComplete(Long reminderId, Long subtaskId);

    void delete(Long reminderId, Long subtaskId);
}
