package toby.ai.tobyreminder.service.ports.in;

import toby.ai.tobyreminder.dto.request.ReminderListRequest;
import toby.ai.tobyreminder.dto.response.ReminderListResponse;

import java.util.List;

public interface ReminderListService {

    List<ReminderListResponse> findAll();

    ReminderListResponse findById(Long id);

    ReminderListResponse create(ReminderListRequest request);

    ReminderListResponse update(Long id, ReminderListRequest request);

    void delete(Long id);

    void reorder(List<toby.ai.tobyreminder.dto.request.OrderItem> items);
}
