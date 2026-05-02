package toby.ai.tobyreminder.service.ports.in;

import toby.ai.tobyreminder.dto.request.ReminderRequest;
import toby.ai.tobyreminder.dto.response.CountResponse;
import toby.ai.tobyreminder.dto.response.ReminderResponse;

import java.util.List;

public interface ReminderService {

    List<ReminderResponse> findByListId(Long listId);

    ReminderResponse create(ReminderRequest request);

    ReminderResponse update(Long id, ReminderRequest request);

    void toggleComplete(Long id);

    void delete(Long id);

    List<ReminderResponse> findBySmart(String type);

    CountResponse getCount();
}
